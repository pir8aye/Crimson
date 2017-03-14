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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;
import com.subterranean_security.crimson.viewer.ui.screen.netman.NetMan;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SettingsDialog;
import com.subterranean_security.crimson.viewer.ui.screen.users.UserMan;

public class ConfigDetail extends JPanel {

	private static final long serialVersionUID = 1L;

	private MPanel parent;

	public ConfigDetail(MPanel mp) {
		parent = mp;

		init();
		initValues();

	}

	private void init() {
		setLayout(null);
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 39, 104, 108);
		add(panel);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 8, 104, 21);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblInterface = new JLabel("Config");
		lblInterface.setHorizontalAlignment(SwingConstants.CENTER);
		lblInterface.setIcon(UIUtil.getIcon("icons16/general/cog.png"));
		panel_1.add(lblInterface);

		JButton btn_users = new JButton(UIUtil.getIcon("icons16/general/clients.png"));
		btn_users.setText("Users");
		btn_users.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_users.setLocation(8, 56);
		btn_users.setSize(88, 20);
		btn_users.setFocusable(false);
		panel.add(btn_users);
		btn_users.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else if (ProfileStore.getLocalViewer().getPermissions().getFlag(Perm.server.users.view)) {
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
		btn_users.setMargin(new Insets(2, 4, 2, 4));

		JButton btn_network = new JButton(UIUtil.getIcon("icons16/general/computer.png"));
		btn_network.setText("Network");
		btn_network.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_network.setLocation(8, 32);
		btn_network.setSize(88, 20);
		btn_network.setFocusable(false);
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
		btn_network.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_network);

		JButton btn_server = new JButton(UIUtil.getIcon("icons16/general/server.png"));
		btn_server.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_server.setText("Server");
		btn_server.setLocation(8, 80);
		btn_server.setSize(88, 20);
		btn_server.setFocusable(false);
		btn_server.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();
		});
		btn_server.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_server);

		JButton btn_settings = new JButton(UIUtil.getIcon("c-16.png"));
		btn_settings.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_settings.setText("Settings");
		btn_settings.setLocation(8, 8);
		btn_settings.setSize(88, 20);
		btn_settings.setFocusable(false);
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
		btn_settings.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_settings);
	}

	private void initValues() {

	}
}
