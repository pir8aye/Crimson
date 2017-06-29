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

import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class ViewerProfileStore extends ProfileStore {
	private ViewerProfileStore() {
	}

	static {
		setProfileListener(new ViewerProfileListener());
	}

	public static void initialize(String lcvid) throws NoSuchElementException, SQLException {
		if (lcvid == null)
			throw new IllegalArgumentException();

		try {
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".viewers");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".viewers", new CachedMap<Integer, ViewerProfile>());
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".viewers");
		}

		try {
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".clients");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".clients", new CachedMap<Integer, ClientProfile>());
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".clients");
		}

		try {
			serverProfiles = (CachedMap<Integer, ServerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".servers");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".servers", new CachedMap<Integer, ServerProfile>());
			serverProfiles = (CachedMap<Integer, ServerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".servers");
		}

	}

	/**
	 * Retrieve the local profile
	 * 
	 * @return The ViewerProfile associated with the current cvid
	 */
	public static ViewerProfile getLocalViewer() {
		return getViewer(LcvidStore.cvid);
	}

	private static class ViewerProfileListener implements ProfileListener {

		@Override
		public void clientOnline(ClientProfile cp) {
			if (UINotification.getPolicy().getOnOldClientConnect()) {
				UINotification.addConsoleInfo("Connection established: " + cp.get(AK_NET.EXTERNAL_IPV4));
			}
			clientNowOnline(cp);
		}

		@Override
		public void viewerOnline(ViewerProfile vp) {
			// TODO Auto-generated method stub

		}

		@Override
		public void clientOnlineFirstTime(ClientProfile cp) {
			if (UINotification.getPolicy().getOnNewClientConnect()) {
				UINotification
						.addConsoleInfo("Connection established: " + cp.get(AK_NET.EXTERNAL_IPV4) + " (new client)");
			}
			clientNowOnline(cp);
		}

		@Override
		public void viewerOnlineFirstTime(ViewerProfile vp) {
			// TODO Auto-generated method stub

		}

		@Override
		public void clientOffline(ClientProfile cp) {
			if (UINotification.getPolicy().getOnClientDisconnect()) {
				UINotification.addConsoleInfo("Connection closed: " + cp.get(AK_NET.EXTERNAL_IPV4));
			}
			clientNowOffline(cp);
		}

		@Override
		public void viewerOffline(ViewerProfile vp) {
			// TODO Auto-generated method stub

		}

		private void clientNowOnline(ClientProfile cp) {
			if (MainFrame.main.panel.listLoaded)
				MainFrame.main.panel.list.addClient(cp);
			if (MainFrame.main.panel.graphLoaded)
				MainFrame.main.panel.graph.addClient(cp);

			for (ClientCPFrame ccpf : UIStore.clientControlPanels) {
				if (ccpf.profile.getCvid() == cp.getCvid()) {
					ccpf.clientOnline();
				}
			}
		}

		private void clientNowOffline(ClientProfile cp) {
			// Remove client from table and detail if applicable
			ClientProfile detailTarget = MainFrame.main.dp.getTarget();
			if (detailTarget != null && cp.getCvid() == detailTarget.getCvid()) {
				MainFrame.main.dp.closeDetail();

				// avoid double notifications
				if (!UINotification.getPolicy().getOnClientDisconnect()) {
					UINotification.addConsoleInfo("The client (" + cp.get(AK_NET.EXTERNAL_IPV4) + ") has disconnected");
				}
			}

			if (MainFrame.main.panel.listLoaded) {
				MainFrame.main.panel.list.removeClient(cp);
			}

			// Send offline message to any open control panels
			for (ClientCPFrame ccpf : UIStore.clientControlPanels) {
				if (ccpf.profile.getCvid() == cp.getCvid()) {
					ccpf.clientOffline();
				}
			}
		}

	}

}