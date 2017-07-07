package com.subterranean_security.crimson.viewer.net.listener;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.store.ProfileStore.ProfileListener;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class ViewerProfileListener implements ProfileListener {

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