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
package com.subterranean_security.crimson.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.proto.ClientAuth.Group;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.storage.ClientDB;
import com.subterranean_security.crimson.core.storage.MemMap;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.sv.ViewerProfile;

public enum ServerStore {
	;
	public static class Listeners {
		public static ArrayList<Listener> listeners = new ArrayList<Listener>();

	}

	public static class Connections {
		private static HashMap<Integer, Receptor> receptors = new HashMap<Integer, Receptor>();
		private static int users = 0;
		private static int clients = 0;

		public static void add(Receptor connection) {
			if (connection.getInstance() == Instance.VIEWER) {
				users++;
			} else {
				clients++;
			}
			receptors.put(connection.getCvid(), connection);
		}

		public static void remove(Receptor connection) {
			if (connection.getInstance() == Instance.VIEWER) {
				users--;
			} else {
				clients--;
			}
			receptors.remove(connection.getCvid());
		}

		public static Receptor getConnection(int svid) {
			return receptors.get(svid);
		}

		public static Set<Integer> getKeySet() {
			return receptors.keySet();
		}

		public static int countUsers() {
			return users;
		}

		public static int countClients() {
			return clients;
		}

		public static void sendToAll(Instance i, Message m) {
			for (int cvid : getKeySet()) {
				if (receptors.get(cvid).getInstance() == i) {
					receptors.get(cvid).handle.write(m);
				}
			}
		}
	}

	public static class Databases {
		public static ServerDB system;
		public static HashMap<String, ClientDB> loaded_viewers = new HashMap<String, ClientDB>();// UID

	}

	public static class Files {
		// TODO somehow get the correct filesystem
		ArrayList<LocalFilesystem> fs = new ArrayList<LocalFilesystem>();
	}

	public static class Authentication {
		public static ArrayList<Group> groups = null;
		private static ArrayList<String> passwords = null;

		static {
			try {
				groups = (ArrayList<Group>) Databases.system.getObject("groups");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				passwords = (ArrayList<String>) Databases.system.getObject("passwords");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static Group getGroup(String groupname) {
			for (Group g : groups) {
				if (g.getName().equals(groupname)) {
					return g;
				}
			}
			return null;
		}

		public static boolean tryPassword(String s) {
			for (String p : passwords) {
				if (s.equals(p)) {
					return true;
				}
			}
			return false;
		}
	}

	public static class Profiles {
		private static MemMap<Integer, ClientProfile> clientProfiles;
		private static MemMap<Integer, ViewerProfile> viewerProfiles;
		private static int svidCounter;

		static {
			try {
				clientProfiles = (MemMap<Integer, ClientProfile>) Databases.system.getObject("profiles.clients");
				clientProfiles.setDatabase(Databases.system);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				viewerProfiles = (MemMap<Integer, ViewerProfile>) Databases.system.getObject("profiles.viewers");
				viewerProfiles.setDatabase(Databases.system);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				svidCounter = Databases.system.getInteger("profiles.idcount");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static ClientProfile getClient(int svid) throws Exception {
			return clientProfiles.get(svid);
		}

		public static void addClient(ClientProfile p) {
			clientProfiles.put(p.getCvid(), p);
		}

		public static ViewerProfile getViewer(int svid) throws Exception {
			return viewerProfiles.get(svid);
		}

		public static void addViewer(ViewerProfile p) {
			viewerProfiles.put(p.getCvid(), p);
		}

		public static int nextID() {
			return svidCounter++;
		}
	}

}
