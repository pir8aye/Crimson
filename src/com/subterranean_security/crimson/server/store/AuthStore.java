/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.server.store;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.auth.AuthGroup;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.auth.PasswordAuthGroup;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedList;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.server.net.ServerConnectionStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

/**
 * Manages the creation and retrieval of authentication schemes
 */
public class AuthStore {
	private AuthStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(AuthStore.class);

	private static CachedList<AuthMethod> methods = null;

	static {
		try {
			methods = (CachedList<AuthMethod>) DatabaseStore.getDatabase().getObject("auth.methods");
			methods.setDatabase(DatabaseStore.getDatabase());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static AuthMethod getGroupMethod(String groupname) {
		for (int i = 0; i < methods.size(); i++) {
			AuthMethod m = methods.get(i);
			if (m.getName().equals(groupname)) {
				return m;
			}
		}

		return null;
	}

	public static AuthenticationGroup getGroup(String name) {
		try {
			return (AuthenticationGroup) DatabaseStore.getDatabase().getObject(getGroupMethod(name).getGroup());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome create(AuthMethod am) {
		Outcome.Builder outcome = Outcome.newBuilder();
		remove(am.getId());

		if (am.getType() == AuthType.GROUP) {
			AuthenticationGroup group = CryptoUtil.generateGroup(am.getName(), am.getGroupSeedPrefix().getBytes());
			am = AuthMethod.newBuilder().mergeFrom(am).setGroup(DatabaseStore.getDatabase().store(group)).build();
		}
		methods.add(am);

		// update viewers
		ServerConnectionStore.sendToAll(Universal.Instance.VIEWER, Message.newBuilder()
				.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().addAuthMethod(am)).build());
		return outcome.setResult(true).build();
	}

	public static void remove(int id) {
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).getId() == id) {
				methods.remove(i);
				return;
			}
		}
	}

	public static AuthMethod getPassword(String s) {
		for (int i = 0; i < methods.size(); i++) {
			if (s.equals(methods.get(i).getPassword())) {
				return methods.get(i);
			}
		}
		return null;
	}

	public static void refreshAllVisibilityPermissions() {
		for (Integer i : ServerProfileStore.getClientKeyset()) {
			refreshVisibilityPermissions(i);
		}
	}

	public static void refreshVisibilityPermissions(int cid) {
		ClientProfile cp = ServerProfileStore.getClient(cid);
		if (cp == null) {
			log.warn("Could not refresh permissions for nonexistant client: {}", cid);
			return;
		}
		AuthMethod clientAuth = null;
		for (int i = 0; i < methods.size(); i++) {
			AuthMethod am = methods.get(i);
			if (cp.getAuthID() == am.getId()) {
				clientAuth = am;
				break;
			}

		}

		ArrayList<Integer> changed = new ArrayList<Integer>();

		if (clientAuth == null) {
			// no auth; append all viewers
			for (Integer i : ServerProfileStore.getViewerKeyset()) {
				ViewerProfile vp = ServerProfileStore.getViewer(i);
				log.debug("Adding visibility flag to viewer {} for client {}", vp.getCvid(), cid);
				vp.getPermissions().addFlag(cid, Perm.client.visibility);

				// TODO only send if changed
				changed.add(vp.getCvid());
			}
		} else {
			for (Integer i : ServerProfileStore.getViewerKeyset()) {
				ViewerProfile vp = ServerProfileStore.getViewer(i);
				if (clientAuth.getOwnerList().contains(vp.get(AKeySimple.VIEWER_USER))
						|| clientAuth.getMemberList().contains(vp.get(AKeySimple.VIEWER_USER))) {
					// this viewer has authority over this client
					log.debug("Adding visibility flag to viewer {} for client {}", vp.getCvid(), cid);
					vp.getPermissions().addFlag(cid, Perm.client.visibility);

					// TODO only send if changed
					changed.add(vp.getCvid());
				}

			}
		}
		for (int id : changed) {
			Connector r = ConnectionStore.get(id);
			if (r != null) {
				r.write(Message.newBuilder()
						.setEvViewerProfileDelta(EV_ViewerProfileDelta.newBuilder()
								.addViewerPermissions(ViewerPermissions.translateFlag(cid, Perm.client.visibility)))
						.build());
			}
		}
	}

	/**
	 * Get a general authentication container. Searches through group containers
	 * before password containers.
	 * 
	 * @param authId
	 * @return The requested authentication container or null if not found
	 */
	public static AuthGroup getContainer(int authId) {
		for (KeyAuthGroup container : groups) {
			if (container.getId() == authId)
				return container;
		}
		for (PasswordAuthGroup container : passwords) {
			if (container.getId() == authId)
				return container;
		}
		return null;
	}

	private static List<PasswordAuthGroup> passwords;

	public static PasswordAuthGroup getPasswordContainer(String password) {
		for (PasswordAuthGroup container : passwords) {
			if (container.getPassword().equals(password))
				return container;
		}
		return null;
	}

	private static List<KeyAuthGroup> groups;

	public static KeyAuthGroup getGroupContainer(String name) {
		for (KeyAuthGroup container : groups) {
			if (container.getName().equals(name))
				return container;
		}
		return null;
	}

	public static void close() {
		for (KeyAuthGroup container : groups) {
			try {
				container.destroy();
			} catch (DestroyFailedException e) {
				log.error("Failed to destroy a group auth container: {}", container.getName());
			}
		}
		groups.clear();

		for (PasswordAuthGroup container : passwords) {
			try {
				container.destroy();
			} catch (DestroyFailedException e) {
				log.error("Failed to destroy a password auth container: {}", container.getId());
			}
		}
		passwords.clear();
	}
}