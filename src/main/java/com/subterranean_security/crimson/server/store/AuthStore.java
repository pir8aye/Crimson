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

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.auth.AuthGroup;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.auth.PasswordAuthGroup;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedList;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
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
			methods = (CachedList<AuthMethod>) DatabaseStore.getDatabase().getCachedCollection("auth.methods");
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

	public static KeyAuthGroup getGroup(String name) {
		try {
			return (KeyAuthGroup) DatabaseStore.getDatabase().getObject(getGroupMethod(name).getGroup());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome create(AttributeGroup am) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true).setTime(System.currentTimeMillis());

		if (getContainer(am.getInt(AK_AUTH.ID)) != null) {
			return outcome.setResult(false).setComment("The AuthID is already taken").build();
		}

		if (am.getType() == AuthType.GROUP) {
			KeyAuthGroup group = CryptoUtil.generateGroup(am.getName(), am.getGroupSeedPrefix().getBytes());
			am = AuthMethod.newBuilder().mergeFrom(am).setGroup(DatabaseStore.getDatabase().store(group)).build();
			groups.add(group);
		} else {
			PasswordAuthGroup group;
		}

		// update viewers
		NetworkStore.broadcastTo(Universal.Instance.VIEWER, Message.newBuilder()
				.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().addAuthMethod(am)).build());
		return outcome.setTime(System.currentTimeMillis() - outcome.getTime()).build();
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
		for (Integer i : ProfileStore.getClientKeyset()) {
			refreshVisibilityPermissions(i);
		}
	}

	// TODO do only when auth is created and client/viewer is added
	public static void refreshVisibilityPermissions(int cid) {
		ClientProfile cp = ProfileStore.getClient(cid);
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
			for (Integer i : ProfileStore.getViewerKeyset()) {
				ViewerProfile vp = ProfileStore.getViewer(i);
				log.debug("Adding visibility flag to viewer {} for client {}", vp.getCvid(), cid);
				vp.getPermissions().addFlag(cid, Perm.client.visibility);

				// TODO only send if changed
				changed.add(vp.getCvid());
			}
		} else {
			for (Integer i : ProfileStore.getViewerKeyset()) {
				ViewerProfile vp = ProfileStore.getViewer(i);
				if (clientAuth.getOwnerList().contains(vp.get(AK_VIEWER.USER))
						|| clientAuth.getMemberList().contains(vp.get(AK_VIEWER.USER))) {
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

	public static PasswordAuthGroup getPasswordGroup(String password) {
		for (PasswordAuthGroup container : passwords) {
			if (container.getPassword().equals(password))
				return container;
		}
		return null;
	}

	private static List<KeyAuthGroup> groups;

	public static KeyAuthGroup getKeyGroup(String name) {
		for (KeyAuthGroup container : groups) {
			if (container.getName().equals(name))
				return container;
		}
		return null;
	}

	/**
	 * Close the store and destroy the groups.
	 */
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