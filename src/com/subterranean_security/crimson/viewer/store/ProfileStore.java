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
package com.subterranean_security.crimson.viewer.store;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.misc.MemList;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class ProfileStore {
	private ProfileStore() {
	}

	private static ServerProfile server = new ServerProfile();

	private static String localUser = null;

	public static MemList<ClientProfile> clients;

	static {
		try {
			clients = (MemList<ClientProfile>) DatabaseStore.getDatabase().getObject("profiles.clients");
			clients.setDatabase(DatabaseStore.getDatabase());
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

			if (clients.get(i).get(AKeySimple.NET_HOSTNAME).equalsIgnoreCase(hostname)) {
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
		for (ViewerProfile vp : server.users) {
			if (vp.get(AKeySimple.VIEWER_USER).equals(user)) {
				return vp;
			}
		}
		return null;
	}

	public static void update(EV_ServerProfileDelta change) {
		server.amalgamate(change);
	}

	public static void update(EV_ViewerProfileDelta change) {
		update(EV_ServerProfileDelta.newBuilder().addViewerUser(change).build());

	}

	public static void update(EV_ProfileDelta change) {
		boolean onlineChanged = false;
		boolean firstConnection = false;

		ClientProfile cp = getClient(change.getCvid());
		if (cp != null) {

			boolean online = cp.getOnline();
			cp.amalgamate(change);
			onlineChanged = online != cp.getOnline();

		} else {
			firstConnection = true;

			// add new profile
			cp = new ClientProfile(change.getCvid());
			cp.amalgamate(change);

			onlineChanged = cp.getOnline();
			clients.add(cp);

		}
		cp.initialize();

		if (onlineChanged) {
			if (cp.getOnline()) {

				if (firstConnection && UINotification.getPolicy().getOnNewClientConnect()) {
					UINotification.addConsoleInfo(
							"(new client) Connection established: " + cp.get(AKeySimple.NET_EXTERNALIP));
				} else if (UINotification.getPolicy().getOnOldClientConnect()) {
					UINotification.addConsoleInfo("Connection established: " + cp.get(AKeySimple.NET_EXTERNALIP));
				}

				clientNowOnline(cp);
			} else {
				if (UINotification.getPolicy().getOnClientDisconnect()) {
					UINotification.addConsoleInfo("Connection closed: " + cp.get(AKeySimple.NET_EXTERNALIP));
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
					UINotification
							.addConsoleInfo("The client (" + cp.get(AKeySimple.NET_EXTERNALIP) + ") has disconnected");
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