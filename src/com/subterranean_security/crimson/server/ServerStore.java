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

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.proto.ClientAuth.Group;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.storage.MemMap;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.core.storage.ViewerDB;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.sv.ViewerProfile;

public enum ServerStore {
	;

	private static final Logger log = CUtil.Logging.getLogger(ServerStore.class);

	public static class Listeners {
		public static ArrayList<Listener> listeners = new ArrayList<Listener>();

		public static void load() {
			unloadAll();
			try {
				for (ListenerConfig lc : ((ArrayList<ListenerConfig>) Databases.system.getObject("listeners"))) {
					listeners.add(new Listener(lc));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static void unloadAll() {
			for (Listener l : listeners) {
				try {
					l.close();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			listeners.clear();
		}

		// TODO unload all except current
		public static void unload() {

		}

	}

	public static class Connections {
		private static HashMap<Integer, Receptor> receptors = new HashMap<Integer, Receptor>();
		private static int users = 0;
		private static int clients = 0;

		public static void add(Receptor connection) {
			log.debug("Adding receptor (CVID: {})", connection.getCvid());
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

		public static Receptor getConnection(int cvid) {
			return receptors.get(cvid);
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
		public static HashMap<String, ViewerDB> loaded_viewers = new HashMap<String, ViewerDB>();// UID

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

		}

		public static ClientProfile getClient(int svid) {
			try {
				return clientProfiles.get(svid);
			} catch (Exception e) {
				return null;
			}
		}

		public static void addClient(ClientProfile p) {
			clientProfiles.put(p.getCvid(), p);
		}

		public static ViewerProfile getViewer(int svid) {
			try {
				return viewerProfiles.get(svid);
			} catch (Exception e) {
				return null;
			}
		}

		public static ViewerProfile getViewer(String user) {
			try {
				for (Integer i : viewerProfiles.keyset()) {
					ViewerProfile vp = viewerProfiles.get(i);
					if (vp.getUser().equals(user)) {
						return vp;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.error("Profile not found for user: {}", user);
			return null;
		}

		public static Set<Integer> getViewerKeyset() {
			return viewerProfiles.keyset();
		}

		public static void addViewer(ViewerProfile p) {
			viewerProfiles.put(p.getCvid(), p);
		}

		// TODO remove
		public static void updateCvid(String username, int cvid) {
			try {
				for (Integer i : viewerProfiles.keyset()) {
					ViewerProfile vp = viewerProfiles.get(i);
					if (vp.getUser().equals(username)) {
						vp.setCvid(cvid);
						viewerProfiles.remove(i);
						viewerProfiles.put(cvid, vp);
						return;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
