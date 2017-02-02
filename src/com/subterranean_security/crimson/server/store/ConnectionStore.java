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

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.universal.Universal;

public final class ConnectionStore {
	private ConnectionStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

	private static HashMap<Integer, Receptor> receptors = new HashMap<Integer, Receptor>();
	private static int users = 0;
	private static int clients = 0;

	public static void add(Receptor r) {
		log.debug("Adding receptor (CVID: {})", r.getCvid());
		if (r.getInstance() == Universal.Instance.VIEWER) {
			users++;
		} else {
			clients++;
			Authentication.refreshVisibilityPermissions(r.getCvid());
			ProfileStore.getClient(r.getCvid()).setOnline(true);
			// sendToViewersWithAuthorityOverClient(r.getCvid(),
			// Perm.client.visibility,
			// Message.newBuilder().setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(r.getCvid())
			// .putStrAttr(SimpleAttribute.CLIENT_ONLINE.ordinal(), "1")));
		}
		receptors.put(r.getCvid(), r);
	}

	public static void remove(int cvid) {
		log.debug("Removing receptor (CVID: {})", cvid);

		// remove receptor
		if (receptors.containsKey(cvid)) {
			Receptor r = receptors.remove(cvid);
			if (r.getInstance() == Universal.Instance.VIEWER) {
				users--;
			} else {
				clients--;
				ProfileStore.getClient(cvid).setOnline(false);
				sendToViewersWithAuthorityOverClient(cvid, Perm.client.visibility,
						Message.newBuilder().setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(cvid)
								.putStrAttr(SimpleAttribute.CLIENT_ONLINE.ordinal(), "0")));
			}
			r.close();
		}

	}

	public static Receptor getConnection(int cvid) {
		return receptors.get(cvid);
	}

	public static Set<Integer> getKeySet() {
		return receptors.keySet();
	}

	public static int countUsers() {
		return users;
	}

	public static int countClients() {
		return clients;
	}

	public static void sendToAll(Universal.Instance i, Message m) {
		for (int cvid : getKeySet()) {
			if (receptors.get(cvid).getInstance() == i) {
				receptors.get(cvid).handle.write(m);
			}
		}
	}

	public static void sendToViewersWithAuthorityOverClient(int cid, int perm, Message.Builder m) {
		for (int cvid : getKeySet()) {
			if (receptors.get(cvid).getInstance() == Universal.Instance.VIEWER
					&& ProfileStore.getViewer(cvid).getPermissions().getFlag(cid, perm)) {
				receptors.get(cvid).handle.write(m.build());
			}
		}
	}

	public static void close() {
		for (int cvid : getKeySet()) {
			receptors.get(cvid).close();
		}
		receptors.clear();
	}
}