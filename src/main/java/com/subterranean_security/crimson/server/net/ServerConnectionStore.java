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
package com.subterranean_security.crimson.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkAdded;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkRemoved;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;

public final class ServerConnectionStore extends ConnectionStore {
	private static final Logger log = LoggerFactory.getLogger(ServerConnectionStore.class);

	private ServerConnectionStore() {
	}

	/**
	 * Broadcast a message to viewers which hold the given permission on the
	 * client
	 * 
	 * @param cid
	 *            The CID of a specific client
	 * @param permission
	 *            The permission that the viewer must have on the client
	 * @param m
	 *            The message to send
	 */
	public static void sendToViewersWithAuthorityOverClient(int cid, short permission, Message.Builder m) {
		System.out.println("Sending to viewers with authority over: " + cid);
		for (Connector c : ConnectionStore.getConnections()) {

			if (c.getInstance() == Universal.Instance.VIEWER
					&& ServerProfileStore.getViewer(c.getCvid()).getPermissions().getFlag(cid, permission)) {
				c.write(m.build());
			}
		}
	}

	public static void addLink(int cvid1, int cvid2) {
		log.debug("Linking: {} and {}", cvid1, cvid2);
		EV_NetworkDelta.Builder ev = EV_NetworkDelta.newBuilder()
				.setLinkAdded(LinkAdded.newBuilder().setCvid1(cvid1).setCvid2(cvid2));

		NetworkStore.broadcastTo(Instance.VIEWER, Message.newBuilder().setEvNetworkDelta(ev).build());
	}

	public static void removeLink(int cvid1, int cvid2) {
		log.debug("Delinking: {} and {}", cvid1, cvid2);
		EV_NetworkDelta.Builder ev = EV_NetworkDelta.newBuilder()
				.setLinkRemoved(LinkRemoved.newBuilder().setCvid1(cvid1).setCvid2(cvid2));

		NetworkStore.broadcastTo(Instance.VIEWER, Message.newBuilder().setEvNetworkDelta(ev).build());
	}

}