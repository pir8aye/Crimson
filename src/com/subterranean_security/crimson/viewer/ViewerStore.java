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
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.net.Delta.ProfileDelta_EV;
import com.subterranean_security.crimson.core.proto.net.Login.ServerInfoDelta_EV;
import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerStore {

	;

	public static class Listeners {
		public static ArrayList<Listener> listeners = new ArrayList<Listener>();

	}

	public static class LocalServer {

		/**
		 * The server executable
		 */
		public static final File bundledServer = new File(Common.base.getAbsolutePath() + "/Crimson-Server.jar");

		public static Process process;
		private static OutputStream os;

		public static boolean startLocalServer() {
			try {
				process = Runtime.getRuntime().exec("java -jar \"" + bundledServer.getAbsolutePath() + "\"");
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

	public static class ServerInfo {
		private static Date lastLoginTime;
		private static String lastLoginLocation;

		public static void integrate(ServerInfoDelta_EV ev) {
			// TODO
		}

		public static Date getLastLoginTime() {
			return lastLoginTime;
		}

		public static String getLastLoginLocation() {
			return lastLoginLocation;
		}

	}

	public static class Profiles {
		public static ArrayList<ClientProfile> profiles = new ArrayList<ClientProfile>();

		public static void add(ClientProfile p) {
			profiles.add(p);
		}

		public static void remove(Integer id) {
			Iterator<ClientProfile> pp = profiles.iterator();
			while (pp.hasNext()) {
				if (pp.next().getSvid() == id) {
					pp.remove();
					return;
				}
			}
		}

		public static void update(ProfileDelta_EV change) {
			boolean flag = true;
			for (ClientProfile p : profiles) {
				if (p.getSvid() == change.getClientid()) {
					flag = false;
					amalgamate(p, change);

					break;
				}
			}

			if (flag) {
				ClientProfile np = new ClientProfile(change.getClientid());
				amalgamate(np, change);
				profiles.add(np);
			}

			if (MainFrame.main.panel.listLoaded) {
				MainFrame.main.panel.list.refreshTM();
			}
			if (MainFrame.main.panel.graphLoaded) {
				// TODO refresh graph
			}

		}

		private static void amalgamate(ClientProfile p, ProfileDelta_EV c) {
			if (c.hasActiveWindow()) {
				p.setActiveWindow(c.getActiveWindow());
			}
			if (c.hasCpuModel()) {
				p.setCpuModel(c.getCpuModel());
			}
			if (c.hasCpuTemp()) {
				p.setCpuTemp(c.getCpuTemp());
			}
			if (c.hasCrimsonVersion()) {
				p.setCrimsonVersion(c.getCrimsonVersion());
			}
			if (c.hasNetHostname()) {
				p.setHostname(c.getNetHostname());
			}
			if (c.hasJavaVersion()) {
				p.setJavaVersion(c.getJavaArch());
			}
			if (c.hasLanguage()) {
				p.setLanguage(c.getLanguage());
			}
			if (c.hasOsFamily()) {
				p.setOsFamily(c.getOsFamily());
			}
			if (c.hasUserName()) {
				p.setUsername(c.getUserName());
			}

		}

	}

}
