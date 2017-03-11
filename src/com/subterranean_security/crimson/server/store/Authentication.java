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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.misc.MemList;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class Authentication {
	private Authentication() {
	}

	private static final Logger log = LoggerFactory.getLogger(Authentication.class);

	private static MemList<AuthMethod> methods = null;

	static {
		try {
			methods = (MemList<AuthMethod>) DatabaseStore.getDatabase().getObject("auth.methods");
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
			am = AuthMethod.newBuilder().mergeFrom(am).setGroup(DatabaseStore.getDatabase()
					.store(CryptoUtil.generateGroup(am.getName(), am.getGroupSeedPrefix().getBytes()))).build();
		}
		methods.add(am);

		// update viewers
		ConnectionStore.sendToAll(Universal.Instance.VIEWER, Message.newBuilder()
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
		for (Integer i : ProfileStore.getClientKeyset()) {
			refreshVisibilityPermissions(i);
		}
	}

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
				if (clientAuth.getOwnerList().contains(vp.getUser())
						|| clientAuth.getMemberList().contains(vp.getUser())) {
					// this viewer has authority over this client
					log.debug("Adding visibility flag to viewer {} for client {}", vp.getCvid(), cid);
					vp.getPermissions().addFlag(cid, Perm.client.visibility);

					// TODO only send if changed
					changed.add(vp.getCvid());
				}

			}
		}
		for (int id : changed) {
			Receptor r = ConnectionStore.getConnection(id);
			if (r != null) {
				r.handle.write(Message.newBuilder()
						.setEvViewerProfileDelta(EV_ViewerProfileDelta.newBuilder()
								.addViewerPermissions(ViewerPermissions.translateFlag(cid, Perm.client.visibility)))
						.build());
			}
		}
	}
}