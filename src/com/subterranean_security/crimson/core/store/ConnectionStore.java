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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.net.NetworkNode;
import com.subterranean_security.crimson.core.proto.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.IDGen;

public final class ConnectionStore {
	private static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

	private ConnectionStore() {
	}

	/**
	 * Stores direct connections which may be between this viewer and the server
	 * or a client
	 */
	private static Map<Integer, Connector> directConnections;

	/**
	 * Network tree which describes the connections between: this viewer, the
	 * server, clients, and other viewers
	 */
	private static NetworkNode network;

	/**
	 * Observer which is notified of connection events
	 */
	private static ConnectionEventListener eventListener;

	public static void initialize(ConnectionEventListener e) {
		log.debug("Initializing connection storage");

		eventListener = e;
		directConnections = new HashMap<Integer, Connector>();
		network = new NetworkNode(Common.cvid);
	}

	public static void changeCvid(int oldCvid, int newCvid) {
		directConnections.put(newCvid, directConnections.remove(oldCvid));
	}

	public static void add(Connector c) {
		c.addObserver(eventListener);
		directConnections.put(c.getCvid(), c);
	}

	public static void remove(int cvid) {
		if (directConnections.containsKey(cvid)) {
			Connector removal = directConnections.remove(cvid);
			removal.close();
		}
	}

	public static int countUsers() {
		return 0;
	}

	public static int countClients() {
		return 0;
	}

	public static Set<Integer> getKeySet() {
		return directConnections.keySet();
	}

	public static Collection<Connector> getValues() {
		return directConnections.values();
	}

	public static Connector get(int cvid) {
		return directConnections.get(cvid);
	}

	public static ConnectionState getServerConnectionState() {
		if (ConnectionStore.get(0) != null) {
			return ConnectionStore.get(0).getState();
		}
		return ConnectionState.NOT_CONNECTED;
	}

	public static void closeAll() {
		for (Integer i : directConnections.keySet()) {
			directConnections.remove(i).close();
		}
	}

	public static int getSize() {
		return directConnections.size();
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
		if (directConnections.containsKey(m.getRid())) {
			get(m.getRid()).write(m);
		} else {
			if (directConnections.containsKey(0)) {
				get(0).write(m);
			}
		}
	}

	public static void route(Message.Builder m) {
		route(m.build());
	}

	public static Message waitForResponse(int cvid, int id, int timeout) throws InterruptedException, Timeout {
		return get(cvid).getResponse(id).get(timeout * 1000);
	}

	public static Message routeAndWait(Message.Builder m, int timeout) throws InterruptedException, Timeout {
		if (!m.hasId()) {
			m.setId(IDGen.msg());
		}
		route(m);
		return waitForResponse(m.getRid(), m.getId(), timeout * 1000);
	}

	public interface ConnectionEventListener extends Observer {

	}
}