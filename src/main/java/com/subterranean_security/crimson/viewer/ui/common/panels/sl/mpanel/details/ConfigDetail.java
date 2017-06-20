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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MConstants;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;
import com.subterranean_security.crimson.viewer.ui.screen.netman.NetMan;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SettingsDialog;
import com.subterranean_security.crimson.viewer.ui.screen.users.UserMan;

public class ConfigDetail extends MDetail {

	private static final long serialVersionUID = 1L;

	public ConfigDetail(MPanel mp) {
		super(mp);

		init();
		initValues();

	}

	private void init() {
		lbl_header.setText("Config");
		lbl_header.setIcon(UIUtil.getIcon("icons16/general/cog.png"));

		JPanel body = new JPanel(null);
		body.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body.setBounds(MConstants.PANEL_X_OFFSET, 39, MConstants.PANEL_WIDTH, 108);
		add(body);

		JButton btn_users = getButton(56, "icons16/general/clients.png", "Users");
		btn_users.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else if (ViewerProfileStore.getLocalViewer().getPermissions().getFlag(Perm.server.users.view)) {
				if (UIStore.userMan == null) {
					UIStore.userMan = new UserMan();
					UIStore.userMan.setLocationRelativeTo(null);
					UIStore.userMan.setVisible(true);
				} else {
					UIStore.userMan.setLocationRelativeTo(null);
					UIStore.userMan.toFront();
				}
				parent.drop();
			} else {
				MainFrame.main.np.addNote("error", "Insufficient permissions!");
			}

		});
		body.add(btn_users);

		JButton btn_network = getButton(32, "icons16/general/computer.png", "Network");
		btn_network.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else if (UIStore.netMan == null) {
				UIStore.netMan = new NetMan();
				UIStore.netMan.setLocationRelativeTo(null);
				UIStore.netMan.setVisible(true);
				parent.drop();
			} else {
				UIStore.netMan.toFront();
				parent.drop();
			}

		});
		body.add(btn_network);

		JButton btn_server = getButton(80, "icons16/general/server.png", "Server");
		btn_server.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();
		});
		body.add(btn_server);

		JButton btn_settings = getButton(8, "c-16.png", "Settings");
		btn_settings.addActionListener(e -> {
			if (UIStore.settingsDialog == null) {
				UIStore.settingsDialog = new SettingsDialog();
				UIStore.settingsDialog.setLocationRelativeTo(null);
				UIStore.settingsDialog.setVisible(true);
			} else {
				UIStore.settingsDialog.setLocationRelativeTo(null);
				UIStore.settingsDialog.toFront();
			}
			parent.drop();
		});
		body.add(btn_settings);
	}

	private void initValues() {

	}
}
