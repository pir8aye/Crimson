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
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.auth.AuthGroup;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.auth.PasswordAuthGroup;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
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
import com.subterranean_security.crimson.sv.store.ProfileStore;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

/**
 * @author cilki
 * @since 4.0.0
 */
public class AuthStore {
	private AuthStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(AuthStore.class);

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

	/**
	 * Remove an authentication group.
	 * 
	 * @param authID
	 *            The authID of the group to remove
	 * @return The result of this action
	 */
	public static Outcome remove(int authID) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true).setTime(System.currentTimeMillis());
		// TODO
		return outcome.setTime(System.currentTimeMillis() - outcome.getTime()).build();
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
	 * Retrieve an authentication group.
	 * 
	 * @param authID
	 *            The ID of the group to find.
	 * @return The requested authentication group or null if not found.
	 */
	public static AttributeGroup getGroup(int authID) {
		for (AttributeGroup authGroup : ProfileStore.getServer().getGroupsOfType(TypeIndex.AUTH)) {
			if (authGroup.getInt(AK_AUTH.ID) == authID) {
				return authGroup;
			}
		}
		return null;
	}

	/**
	 * Retrieve a password authentication group.
	 * 
	 * @param password
	 *            The password of the group to find.
	 * @return The requested password authentication group or null if not found.
	 */
	public static AttributeGroup getPasswordGroup(String password) {
		for (AttributeGroup authGroup : ProfileStore.getServer().getGroupsOfType(TypeIndex.AUTH)) {
			if (authGroup.getStr(AK_AUTH.PASSWORD).equals(password)) {
				return authGroup;
			}
		}
		return null;
	}

	/**
	 * Retrieve a key authentication group.
	 * 
	 * @param name
	 *            The name of the key authentication group.
	 * @return The requested key authentication group or null if not found.
	 */
	public static AttributeGroup getKeyGroup(String name) {
		for (AttributeGroup authGroup : ProfileStore.getServer().getGroupsOfType(TypeIndex.AUTH)) {
			if (authGroup.getStr(AK_AUTH.NAME).equals(name)) {
				return authGroup;
			}
		}
		return null;
	}

}