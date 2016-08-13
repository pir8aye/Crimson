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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.storage.ClientDB;
import com.subterranean_security.crimson.core.storage.MemList;
import com.subterranean_security.crimson.core.storage.MemMap;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.core.util.AuthenticationGroup;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

public enum ServerStore {
	;

	private static final Logger log = LoggerFactory.getLogger(ServerStore.class);

	public static class Listeners {

		private static boolean running = false;

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

		public static boolean isRunning() {
			return running;
		}

		public static void stop() {
			if (running) {
				log.info("Stopping network listeners");
				running = false;
				unloadAll();
			}

		}

		public static void start() {
			if (!running) {
				log.info("Starting network listeners");
				running = true;
				load();
			}
		}

	}

	public static class Connections {
		private static HashMap<Integer, Receptor> receptors = new HashMap<Integer, Receptor>();
		private static int users = 0;
		private static int clients = 0;

		public static void add(Receptor r) {
			log.debug("Adding receptor (CVID: {})", r.getCvid());
			if (r.getInstance() == Instance.VIEWER) {
				users++;
			} else {
				clients++;
				Profiles.getClient(r.getCvid()).setOnline(true);
				sendToViewersWithAuthorityOverClient(r.getCvid(),
						Message.newBuilder().setUrgent(true)
								.setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(r.getCvid()).setOnline(true)),
						"client_visibility");
			}
			receptors.put(r.getCvid(), r);
		}

		public static void remove(int cvid) {
			log.debug("Removing receptor (CVID: {})", cvid);
			if (receptors.containsKey(cvid)) {
				Receptor r = receptors.remove(cvid);
				if (r.getInstance() == Instance.VIEWER) {
					users--;
				} else {
					clients--;
					Profiles.getClient(cvid).setOnline(false);
					sendToViewersWithAuthorityOverClient(cvid, Message.newBuilder().setUrgent(true).setEvProfileDelta(
							EV_ProfileDelta.newBuilder().setCvid(cvid).setOnline(false)), "client_visibility");
				}
				r.close();
			}

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

		public static void sendToViewersWithAuthorityOverClient(int cid, Message.Builder m, String permission) {
			for (int cvid : getKeySet()) {
				// TODO filter
				if (receptors.get(cvid).getInstance() == Instance.VIEWER) {
					receptors.get(cvid).handle.write(m.build());
				}
			}
		}

		public static void sendToClientsUnderAuthorityOfViewer(int vid, Message.Builder m, String permission) {
			for (int cvid : getKeySet()) {
				// TODO filter
				if (receptors.get(cvid).getInstance() == Instance.CLIENT) {
					receptors.get(cvid).handle.write(m.build());
				}
			}
		}

		public static void close() {
			for (int cvid : getKeySet()) {
				receptors.get(cvid).close();
			}
			receptors.clear();
		}
	}

	public static class Databases {
		public static ServerDB system;
		public static HashMap<String, ClientDB> loaded_viewers = new HashMap<String, ClientDB>();// UID

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

		private static MemList<AuthMethod> methods = null;

		static {
			try {
				methods = (MemList<AuthMethod>) Databases.system.getObject("auth.methods");
				methods.setDatabase(Databases.system);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public static AuthMethod getGroupMethod(String groupname) {
			for (int i = 0; i < methods.size(); i++) {
				AuthMethod m = methods.get(i);
				if (m.getName().equals(groupname)) {
					return m;
				}
			}

			return null;
		}

		public static AuthenticationGroup getGroup(String name) {
			try {
				return (AuthenticationGroup) Databases.system.get(getGroupMethod(name).getGroup());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public static Outcome create(AuthMethod am) {
			Outcome.Builder outcome = Outcome.newBuilder();
			remove(am.getId());

			if (am.getType() == AuthType.GROUP) {
				am = AuthMethod.newBuilder().mergeFrom(am).setGroup(
						Databases.system.store(Crypto.generateGroup(am.getName(), am.getGroupSeedPrefix().getBytes())))
						.build();
			}
			methods.add(am);

			try {
				Databases.system.flushHeap();
			} catch (SQLException e) {
				outcome.setComment("Failed to flush heap!");
			}
			System.gc();

			// update viewers
			ServerStore.Connections.sendToAll(Instance.VIEWER, Message.newBuilder().setUrgent(true)
					.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().addAuthMethod(am)).build());
			return outcome.setResult(true).build();
		}

		public static void remove(int id) {
			for (int i = 0; i < methods.size(); i++) {
				if (methods.get(i).getId() == id) {
					methods.remove(i);
					return;
				}
			}
		}

		public static boolean tryPassword(String s) {
			for (int i = 0; i < methods.size(); i++) {
				if (s.equals(methods.get(i).getPassword())) {
					return true;
				}
			}
			return false;
		}

		public static void refreshVisibilityPermissions() {
			for (Integer i : Profiles.getClientKeyset()) {
				refreshVisibilityPermissions(i);
			}
		}

		public static void refreshVisibilityPermissions(int cid) {
			ClientProfile cp = Profiles.getClient(cid);
			if (cp == null) {
				log.warn("Could not refresh permissions for nonexistant client: {}", cid);
				return;
			}
			AuthMethod clientAuth = null;
			for (int i = 0; i < methods.size(); i++) {
				AuthMethod am = methods.get(i);
				if (cp.getAuthID() == am.getId()) {
					clientAuth = am;
					break;
				}

			}
			for (Integer i : Profiles.getViewerKeyset()) {
				ViewerProfile vp = Profiles.getViewer(i);
				if (clientAuth.getMemberList().contains(vp.getUser())) {
					// this viewer has authority over this client
				}

			}
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

		public static ClientProfile getClient(int cid) {
			try {
				return clientProfiles.get(cid).reinit();
			} catch (Exception e) {
				return null;
			}
		}

		public static void addClient(ClientProfile p) {
			clientProfiles.put(p.getCvid(), p);
			Authentication.refreshVisibilityPermissions(p.getCvid());
		}

		public static ViewerProfile getViewer(int vid) {
			try {
				return viewerProfiles.get(vid);
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
			return null;
		}

		public static ArrayList<ViewerProfile> getViewersWithAuthorityOnClient(int cid) {
			ArrayList<ViewerProfile> vps = new ArrayList<ViewerProfile>();
			try {
				for (Integer i : viewerProfiles.keyset()) {
					// TODO filter
					vps.add(viewerProfiles.get(i));

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return vps;
		}

		public static ArrayList<ClientProfile> getClientsUnderAuthority(int vid) {
			ArrayList<ClientProfile> cps = new ArrayList<ClientProfile>();
			try {
				for (Integer i : clientProfiles.keyset()) {
					// TODO filter
					cps.add(clientProfiles.get(i));

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cps;
		}

		public static Set<Integer> getViewerKeyset() {
			return viewerProfiles.keyset();
		}

		public static Set<Integer> getClientKeyset() {
			return clientProfiles.keyset();
		}

		public static void addViewer(ViewerProfile p) {
			viewerProfiles.put(p.getCvid(), p);
		}

	}

}
