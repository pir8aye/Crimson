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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerInfoDelta;
import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.ServerProfile;
import com.subterranean_security.crimson.sv.ViewerProfile;
import com.subterranean_security.crimson.viewer.net.ViewerConnector;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerStore {

	;

	private static final Logger log = CUtil.Logging.getLogger(ViewerStore.class);

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
	}

	public static class LocalServer {

		/**
		 * The server executable
		 */
		public static final File bundledServer = new File(Common.base.getAbsolutePath() + "/Crimson-Server.jar");

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
					os.write("\u0003\n".getBytes());
					os.flush();
					process.waitFor(3, TimeUnit.SECONDS);
					os.close();
					process.destroyForcibly();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static class Databases {
		public static LViewerDB local;

		static {
			try {
				local = new LViewerDB(new File(Common.var.getAbsolutePath() + "/viewer.db"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class Profiles {
		public static ServerProfile server = new ServerProfile();
		public static ClientProfile viewer = new ClientProfile();
		public static ViewerProfile vp;

		public static ArrayList<ClientProfile> clients = new ArrayList<ClientProfile>();

		public static void remove(Integer id) {
			Iterator<ClientProfile> pp = clients.iterator();
			while (pp.hasNext()) {
				if (pp.next().getCvid() == id) {
					pp.remove();
					return;
				}
			}
		}

		public static void update(EV_ServerInfoDelta change) {
			server.amalgamate(change);
		}

		public static void update(EV_ProfileDelta change) {
			System.out.println("Got PUPDATE for cvid: " + change.getCvid());
			boolean flag = true;
			for (ClientProfile p : clients) {
				if (p.getCvid() == change.getCvid()) {
					flag = false;
					p.amalgamate(change);

					break;
				}
			}
			if (flag) {
				ClientProfile np = new ClientProfile(change.getCvid());
				np.amalgamate(change);
				clients.add(np);
			}

			if (MainFrame.main.panel.listLoaded) {
				MainFrame.main.panel.list.refreshTM();
			}
			if (MainFrame.main.panel.graphLoaded) {
				// TODO refresh graph
			}

		}

	}

}
