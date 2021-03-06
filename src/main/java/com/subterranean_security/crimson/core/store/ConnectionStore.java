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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.exception.MessageFlowException;
import com.subterranean_security.crimson.core.net.executor.CharcoalExecutor;
import com.subterranean_security.crimson.core.net.executor.ViridianExecutor;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.net.listener.ConnectionEventListener;
import com.subterranean_security.crimson.core.net.thread.ConnectionPeriod;
import com.subterranean_security.crimson.core.net.thread.ConnectionThread;
import com.subterranean_security.crimson.core.net.thread.routines.RoundRobin;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.proto.core.Generator.NetworkTarget;
import com.subterranean_security.crimson.proto.core.net.sequences.Debug.RQ_DebugSession;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;

/**
 * A static store for managing direct connections and connection attempt
 * threads.
 * 
 * @author cilki
 * @see NetworkStore
 * @since 5.0.0
 */
public final class ConnectionStore {
	public static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

	private ConnectionStore() {
	}

	/**
	 * Stores direct connections which may exist between any pair of instances
	 */
	private static Map<Integer, Connector> directConnections;

	/**
	 * Observer which is notified of connection events
	 */
	private static ConnectionEventListener eventListener;

	static {
		directConnections = new HashMap<Integer, Connector>();
		threads = new ArrayList<>(6);

		switch (Universal.instance) {
		case CLIENT:
			eventListener = new com.subterranean_security.crimson.client.net.listener.ClientConnectionEventListener();
			break;
		case SERVER:
			eventListener = new com.subterranean_security.crimson.server.net.listener.ServerConnectionEventListener();
			break;
		case VIEWER:
			eventListener = new com.subterranean_security.crimson.viewer.net.listener.ViewerConnectionEventListener();
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
	 * Tests for a direct connection between the running instance and another
	 * instance
	 * 
	 * @param cvid
	 * @return True if there exists a direct connection to the specified cvid
	 */
	public static boolean connectedDirectly(int cvid) {
		return directConnections.containsKey(cvid);
	}

	public static Set<Integer> getKeySet() {
		return directConnections.keySet();
	}

	public static Collection<Connector> getConnections() {
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
		if (directConnections == null)
			throw new IllegalStateException();

		try {
			// terminate running connections
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

	public static boolean connectViridian() {
		if (connectedDirectly(Reserved.VIRIDIAN)) {
			return true;
		}

		Connector connector = ConnectionThread.makeConnection(new ExecutorFactory(ViridianExecutor.class),
				"subterranean-security.pw", 10102, true);
		if (connector != null) {
			connector.setCvid(Reserved.VIRIDIAN);
			add(connector);

			return true;
		}

		return false;
	}

	public static boolean connectCharcoal() {
		if (connectedDirectly(Reserved.CHARCOAL)) {
			return true;
		}

		Connector connector = ConnectionThread.makeConnection(new ExecutorFactory(CharcoalExecutor.class), "127.0.0.1",
				10100, false);
		if (connector != null) {
			connector.setCvid(Reserved.CHARCOAL);

			try {
				Message rs = connector.writeAndGetResponse(Message.newBuilder()
						.setRqDebugSession(RQ_DebugSession.newBuilder().setInstance(Universal.instance.toString()))
						.build()).get(2000);

				if (rs.getRsDebugSession() == null) {
					throw new MessageFlowException(RQ_DebugSession.class, rs);
				} else if (!rs.getRsDebugSession().getResult()) {
					log.debug("Charcoal rejected this instance :(");
					return false;
				}

			} catch (MessageTimeout | InterruptedException e1) {
				return false;
			}

			add(connector);

			return true;
		}

		return false;
	}

	/**
	 * A list of threads currently attempting connections
	 */
	private static List<ConnectionThread> threads;

	/**
	 * Terminate all running connection attempts
	 */
	public static void cancelAttempts() {
		if (threads == null)
			throw new IllegalStateException();

		try {
			// terminate connection attempts
			for (ConnectionThread ct : threads)
				ct.interrupt();
		} finally {
			threads.clear();
		}
	}

	public static Connector connect(ExecutorFactory executor, List<NetworkTarget> targets, ConnectionPeriod period,
			int maximumIterations, boolean forceCerts) throws InterruptedException {
		ConnectionThread ct = new ConnectionThread(
				new RoundRobin(executor, targets, period, maximumIterations, forceCerts));
		threads.add(ct);
		try {
			return ct.connect();
		} finally {
			threads.remove(ct);
		}
	}

}