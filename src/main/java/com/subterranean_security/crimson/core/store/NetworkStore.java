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
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.set.ProfileSet;
import com.subterranean_security.crimson.sv.profile.set.ProfileSetFactory;
import com.subterranean_security.crimson.universal.Universal;

/**
 * A static store for managing network connections, which may or may not be
 * directly connected and therefore present in the {@code ConnectionStore}.
 * 
 * @author cilki
 * @since 5.0.0
 */
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
		if (ConnectionStore.connectedDirectly(m.getTo())) {
			ConnectionStore.get(m.getTo()).write(m);
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
		return getResponse(m.getTo(), m.getId(), timeout);
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
	 * Broadcast a message to every member of a {@code ProfileSet}. If a profile in
	 * the set is not currently connected, it will be skipped.
	 * 
	 * @param message
	 *            The message to broadcast
	 * @param profiles
	 *            The profiles to receive {@code message}
	 */
	public static void broadcastTo(Message message, ProfileSet profiles) {
		for (Profile p : profiles) {
			if (ConnectionStore.connectedDirectly(p.getCvid())) {
				ConnectionStore.get(p.getCvid()).write(message);
			}
		}
	}

	/**
	 * Alias for {@code broadcastTo(Message, ProfileSet)}
	 * 
	 * @param message
	 *            The message to broadcast
	 * @param profileSetFactory
	 *            The profile set factory which will be built
	 */
	public static void broadcastTo(Message message, ProfileSetFactory profileSetFactory) {
		broadcastTo(message, profileSetFactory.build());
	}

	/**
	 * Broadcast a message to every connected instance
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

	/**
	 * @return The number of viewer instances in the network
	 */
	public static int countUsers() {
		return 0;
	}

	/**
	 * @return The number of client instances in the network
	 */
	public static int countClients() {
		return 0;
	}

}
