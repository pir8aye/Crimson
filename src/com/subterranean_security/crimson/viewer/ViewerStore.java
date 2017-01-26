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
package com.subterranean_security.crimson.viewer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.MemList;
import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.net.ViewerConnector;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class ViewerStore {

	private static final Logger log = LoggerFactory.getLogger(ViewerStore.class);

	private ViewerStore() {
	}

	public static class Connections {
		private static HashMap<Integer, BasicConnector> connections = new HashMap<Integer, BasicConnector>();

		public static void put(int cvid, BasicConnector vc) {
			log.debug("Added new connection (CVID: {})", cvid);
			connections.put(cvid, vc);
		}

		public static BasicConnector get(int cvid) {
			return connections.get(cvid);
		}

		public static ViewerConnector getVC(int cvid) {
			return (ViewerConnector) connections.get(cvid);
		}

		public static void closeAll() {
			for (Integer i : connections.keySet()) {
				try {
					connections.remove(i).close();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public static int getSize() {
			return connections.size();
		}
	}

	public static class LocalServer {

		/**
		 * The server executable
		 */
		public static final File bundledServer = new File(
				Common.Directories.base.getAbsolutePath() + "/Crimson-Server.jar");

		public static Process process;
		private static OutputStream os;

		public static boolean startLocalServer() {
			String command = "java -jar \"" + bundledServer.getAbsolutePath() + "\"";
			log.debug("Starting local server ({})", command);
			try {
				process = Runtime.getRuntime().exec(command);
				os = process.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		public static void killLocalServer() {
			if (os != null) {
				// kill server
				try {
					os.write("quit\n".getBytes());
					os.flush();
					process.waitFor(3, TimeUnit.SECONDS);
					os.close();
				} catch (IOException e) {

				} catch (InterruptedException e) {

				}
			}
		}
	}

	public static class Databases {
		public static LViewerDB local;

		static {
			try {
				local = new LViewerDB(new File(Common.Directories.var.getAbsolutePath() + "/viewer.db"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class Profiles {

		private static ClientProfile vcp = new ClientProfile();

		private static ServerProfile server = new ServerProfile();

		private static String localUser = null;

		public static MemList<ClientProfile> clients;

		static {
			try {
				clients = (MemList<ClientProfile>) Databases.local.getObject("profiles.clients");
				clients.setDatabase(Databases.local);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public static void setLocalUser(String user) {
			localUser = user;
		}

		public static void removeClient(Integer id) {
			clients.remove(id);
		}

		public static ClientProfile getClient(String hostname) {
			for (int i = 0; i < clients.size(); i++) {

				if (clients.get(i).getAttr(SimpleAttribute.NET_HOSTNAME).equalsIgnoreCase(hostname)) {
					return clients.get(i);
				}
			}
			return null;
		}

		public static ClientProfile getClient(int cid) {
			for (int i = 0; i < clients.size(); i++) {

				if (clients.get(i).getCid() == cid) {
					return clients.get(i);
				}
			}
			return null;
		}

		public static ServerProfile getServer() {
			return server;
		}

		public static ClientProfile getLocalClient() {
			return vcp;
		}

		public static ViewerProfile getLocalViewer() {
			return getViewer(localUser);
		}

		public static void removeViewer(int vid) {

		}

		public static ViewerProfile getViewer(int vid) {
			for (ViewerProfile v : server.users) {
				if (v.getCvid() == vid) {
					return v;
				}
			}
			return null;
		}

		public static ViewerProfile getViewer(String user) {
			for (ViewerProfile v : server.users) {
				if (v.getUser().equals(user)) {
					return v;
				}
			}
			return null;
		}

		public static void update(EV_ServerProfileDelta change) {
			server.amalgamate(change);
		}

		public static void update(EV_ViewerProfileDelta change) {
			server.amalgamate(EV_ServerProfileDelta.newBuilder().addViewerUser(change).build());

		}

		public static void update(EV_ProfileDelta change) {
			boolean onlineChanged = false;
			boolean firstConnection = false;

			ClientProfile cp = getClient(change.getCvid());
			if (cp != null) {
				onlineChanged = change.containsStrAttr(SimpleAttribute.CLIENT_ONLINE.ordinal())
						&& !change.getStrAttrOrDefault(SimpleAttribute.CLIENT_ONLINE.ordinal(), "")
								.equals(cp.getAttr(SimpleAttribute.CLIENT_ONLINE));
				cp.amalgamate(change);

			} else {
				firstConnection = true;
				onlineChanged = change.containsStrAttr(SimpleAttribute.CLIENT_ONLINE.ordinal());

				// add new profile
				cp = new ClientProfile(change.getCvid());
				cp.amalgamate(change);
				clients.add(cp);

			}
			cp.initialize();

			if (onlineChanged) {
				if (change.getStrAttrOrDefault(SimpleAttribute.CLIENT_ONLINE.ordinal(), "").equals("1")) {

					if (firstConnection && UINotification.getPolicy().getOnNewClientConnect()) {
						UINotification.addConsoleInfo(
								"(new client) Connection established: " + cp.getAttr(SimpleAttribute.NET_EXTERNALIP));
					} else if (UINotification.getPolicy().getOnOldClientConnect()) {
						UINotification.addConsoleInfo(
								"Connection established: " + cp.getAttr(SimpleAttribute.NET_EXTERNALIP));
					}

					clientNowOnline(cp);
				} else {
					if (UINotification.getPolicy().getOnClientDisconnect()) {
						UINotification
								.addConsoleInfo("Connection closed: " + cp.getAttr(SimpleAttribute.NET_EXTERNALIP));
					}

					clientNowOffline(cp);
				}
			}

		}

		public static void clientNowOnline(ClientProfile cp) {
			if (MainFrame.main.panel.listLoaded)
				MainFrame.main.panel.list.addClient(cp);
			if (MainFrame.main.panel.graphLoaded)
				MainFrame.main.panel.graph.addClient(cp);

			for (ClientCPFrame ccpf : UIStore.clientControlPanels) {
				if (ccpf.profile.getCid() == cp.getCid()) {
					ccpf.clientOnline();
				}
			}
		}

		public static void clientNowOffline(ClientProfile cp) {
			// Remove client from table and detail if applicable
			if (MainFrame.main.panel.listLoaded) {
				MainFrame.main.panel.list.removeClient(cp);
				ClientProfile detailTarget = MainFrame.main.dp.getTarget();
				if (detailTarget != null && cp.getCid() == detailTarget.getCid()) {
					MainFrame.main.dp.closeDetail();

					// avoid double notifications
					if (!UINotification.getPolicy().getOnClientDisconnect()) {
						UINotification.addConsoleInfo(
								"The client (" + cp.getAttr(SimpleAttribute.NET_EXTERNALIP) + ") has disconnected");
					}

				}
			}

			// Remove client from graph
			if (MainFrame.main.panel.graphLoaded)
				MainFrame.main.panel.graph.removeClient(cp);

			// Send offline message to any open control panels
			for (ClientCPFrame ccpf : UIStore.clientControlPanels) {
				if (ccpf.profile.getCid() == cp.getCid()) {
					ccpf.clientOffline();
				}
			}
		}

	}

}
