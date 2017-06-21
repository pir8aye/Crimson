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
package com.subterranean_security.crimson.core.store;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.NetworkNode;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message.Builder;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;

public final class NetworkStore {
	private NetworkStore() {
	}

	/**
	 * Tree which describes the connections between entities on the network
	 */
	private static NetworkNode network;

	static {
		network = new NetworkNode(LcvidStore.cvid);
	}

	public static NetworkNode getNetworkTree() {
		return network;
	}

	/**
	 * Update the network tree with the specified delta
	 * 
	 * @param nd
	 */
	public static void updateNetwork(EV_NetworkDelta nd) {
		network.update(nd);
	}

	/**
	 * Transmit a message into the network
	 * 
	 * @param m
	 */
	public static void route(Message m) {
		if (ConnectionStore.connectedDirectly(m.getRid())) {
			ConnectionStore.get(m.getRid()).write(m);
		} else {
			if (ConnectionStore.connectedDirectly(Reserved.SERVER)) {
				ConnectionStore.get(Reserved.SERVER).write(m);
			}
		}
	}

	/**
	 * Alias for route(Message)
	 */
	public static void route(Message.Builder m) {
		route(m.build());
	}

	public static Message route(Message.Builder m, int timeout) throws InterruptedException, MessageTimeout {
		route(m);
		return getResponse(m.getRid(), m.getId(), timeout);
	}

	public static Message getResponse(int cvid, int id, int timeout) throws InterruptedException, MessageTimeout {
		return ConnectionStore.get(cvid).getResponse(id).get(timeout);
	}

	/**
	 * Broadcast a message
	 * 
	 * @param instance
	 *            Broadcast only to instances of this type
	 * @param message
	 *            The message to broadcast
	 */
	public static void broadcastTo(Universal.Instance instance, Message message) {
		if (message == null)
			throw new IllegalArgumentException();

		for (Connector c : ConnectionStore.getConnections()) {
			if (c.getInstance() == instance) {
				c.write(message);
			}
		}
	}

	/**
	 * Broadcast a message
	 * 
	 * @param message
	 *            The message to broadcast
	 */
	public static void broadcast(Message message) {
		if (message == null)
			throw new IllegalArgumentException();

		for (Connector c : ConnectionStore.getConnections())
			c.write(message);
	}

	public static int countUsers() {
		return 0;
	}

	public static int countClients() {
		return 0;
	}

}
