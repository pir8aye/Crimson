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

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.CertificateState;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFlowException;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.net.NetworkNode;
import com.subterranean_security.crimson.core.net.executor.CharcoalExecutor;
import com.subterranean_security.crimson.core.net.executor.ViridianExecutor;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.proto.Debug.RQ_DebugSession;
import com.subterranean_security.crimson.core.proto.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.debug.CharcoalAppender;
import com.subterranean_security.crimson.universal.Universal;

public abstract class ConnectionStore {
	private static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

	/**
	 * Stores direct connections which may exist between a viewer and the server
	 * or between a viewer and a client
	 */
	private static Map<Integer, Connector> directConnections;

	/**
	 * Tree which describes the connections between entities on the network
	 */
	private static NetworkNode network;

	/**
	 * Observer which is notified of connection events
	 */
	private static ConnectionEventListener eventListener;

	static {
		directConnections = new HashMap<Integer, Connector>();
		network = new NetworkNode(LcvidStore.cvid);

		switch (Universal.instance) {
		case CLIENT:
			eventListener = new com.subterranean_security.crimson.client.net.ClientConnectionStore.ClientConnectionEventListener();
			break;
		case SERVER:
			eventListener = new com.subterranean_security.crimson.server.net.ServerConnectionStore.ServerConnectionEventListener();
			break;
		case VIEWER:
			eventListener = new com.subterranean_security.crimson.viewer.net.ViewerConnectionStore.ViewerConnectionEventListener();
			break;
		default:
			break;

		}
	}

	public static void add(Connector c) {
		log.debug("Adding new Connector: {}", c.getCvid());
		c.addObserver(eventListener);
		directConnections.put(c.getCvid(), c);
	}

	public static Connector remove(int cvid) {
		if (directConnections.containsKey(cvid)) {
			Connector removal = directConnections.remove(cvid);
			removal.close();
			return removal;
		}
		return null;
	}

	/**
	 * Tests for a direct connection
	 * 
	 * @param cvid
	 * @return True if there exists a direct connection to the specified cvid
	 */
	public static boolean connectedDirectly(int cvid) {
		return directConnections.containsKey(cvid);
	}

	private static int users;
	private static int clients;

	public static int countUsers() {
		return users;
	}

	public static int countClients() {
		return clients;
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
		if (ConnectionStore.get(com.subterranean_security.crimson.core.util.IDGen.Reserved.SERVER) != null) {
			return ConnectionStore.get(com.subterranean_security.crimson.core.util.IDGen.Reserved.SERVER).getState();
		}
		return ConnectionState.NOT_CONNECTED;
	}

	public static void closeAll() {
		try {
			for (Integer i : directConnections.keySet()) {
				directConnections.get(i).close();
			}
		} finally {
			directConnections.clear();
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
			if (directConnections.containsKey(Reserved.SERVER)) {
				get(Reserved.SERVER).write(m);
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
		// empty
	}

	public static Connector makeConnection(ExecutorFactory exe, String server, int port, boolean forceCertificates) {
		log.debug("Attempting connection to: {}:{}", server, port);

		Connector connector = new Connector(exe.build());
		try {
			connector.connect(Config.ConnectionType.SOCKET, server, port, true);
		} catch (ConnectException | InterruptedException e) {
			log.debug("Connection failed: {}", e.getMessage());
			return null;
		}

		if (connector.getCertState() == CertificateState.REFUSED) {
			if (!forceCertificates) {
				// try insecure
				connector = new Connector(exe.build(), false);
				try {
					connector.connect(Config.ConnectionType.SOCKET, server, port, false);
				} catch (ConnectException | InterruptedException e) {
					log.debug("Connection failed: {}", e.getMessage());
					return null;
				}

				if (connector.getCertState() == CertificateState.REFUSED) {
					log.debug("Connection failed: Certificate error");
					return null;
				}

			} else {
				log.debug("Dropping connection with {} because certificate verification failed", server);
			}
		}

		return connector;
	}

	public static boolean connectViridian() {
		if (connectedDirectly(Reserved.VIRIDIAN)) {
			return true;
		}

		Connector connector = makeConnection(new ExecutorFactory(ViridianExecutor.class), "subterranean-security.pw",
				10102, true);
		if (connector != null) {
			connector.setCvid(Reserved.VIRIDIAN);
			add(connector);

			// trigger report buffer flush
			new Thread(new Runnable() {
				public void run() {
					Reporter.flushBuffer();
				}
			}).start();
			return true;
		}

		return false;
	}

	public static boolean connectCharcoal() {
		if (connectedDirectly(Reserved.CHARCOAL)) {
			return true;
		}

		Connector connector = makeConnection(new ExecutorFactory(CharcoalExecutor.class), "127.0.0.1", 10100, true);
		if (connector != null) {
			connector.setCvid(Reserved.CHARCOAL);

			try {
				Message rs = connector.writeAndGetResponse(Message.newBuilder()
						.setRqDebugSession(RQ_DebugSession.newBuilder().setInstance(Universal.instance.toString()))
						.build()).get(2000);

				if (!rs.hasRsDebugSession()) {
					throw new MessageFlowException(RQ_DebugSession.class, rs);
				} else if (!rs.getRsDebugSession().getResult()) {
					log.debug("Charcoal rejected this instance :(");
					return false;
				}

			} catch (Timeout | InterruptedException e1) {
				return false;
			}

			add(connector);

			try {
				CharcoalAppender.setup();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

}