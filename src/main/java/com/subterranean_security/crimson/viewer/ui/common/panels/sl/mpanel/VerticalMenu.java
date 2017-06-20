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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details.AboutDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details.ClientIndependentDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details.ConfigDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details.InterfaceDetail;
import com.subterranean_security.crimson.viewer.ui.screen.generator.GenDialog;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.MenuControls;

public class VerticalMenu extends JPanel {

	private static final long serialVersionUID = 1L;

	private JMenu mnControls;
	private MenuControls mc;
	private MPanel mp;

	// details
	private InterfaceDetail interfaceDetail;
	private AboutDetail aboutDetail;
	private ConfigDetail configDetail;
	private ClientIndependentDetail clientDetail;

	private JButton btn_interface;
	private JButton btn_generator;
	private JButton btn_about;
	private JButton btn_config;
	private JButton btn_client_independent;
	private JButton btn_install_tools;

	public VerticalMenu(MPanel mp) {
		this.mp = mp;
		init();
		initDetails();
	}

	public void closeControls() {
		MenuSelectionManager.defaultManager().clearSelectedPath();
	}

	private void initDetails() {
		interfaceDetail = new InterfaceDetail(mp);
		aboutDetail = new AboutDetail(mp);
		configDetail = new ConfigDetail(mp);
		clientDetail = new ClientIndependentDetail(mp);
	}

	private void init() {
		setForeground(UICommon.bg);
		setBackground(UICommon.bg);

		setLayout(new BorderLayout(0, 0));

		JPanel menuBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) menuBar.getLayout();
		flowLayout.setHgap(2);
		flowLayout.setVgap(1);
		add(menuBar, BorderLayout.CENTER);

		btn_interface = new JButton(UIUtil.getIcon("icons16/general/application.png"));
		btn_interface.setFocusable(false);
		btn_interface.setFocusPainted(false);

		menuBar.add(btn_interface);
		btn_interface.addActionListener(e -> {
			clearButtonFocus();

			if (mp.raise(interfaceDetail)) {
				btn_interface.setFocusable(true);
				btn_interface.requestFocus();
			}
		});
		btn_interface.setMargin(new Insets(2, 4, 2, 4));

		btn_client_independent = new JButton(UIUtil.getIcon("icons16/general/dopplr.png"));
		btn_client_independent.setToolTipText("Client-Independent Tools");
		btn_client_independent.setFocusable(false);
		btn_client_independent.setFocusPainted(false);
		menuBar.add(btn_client_independent);
		btn_client_independent.addActionListener(e -> {
			clearButtonFocus();

			if (mp.raise(clientDetail)) {
				btn_client_independent.setFocusable(true);
				btn_client_independent.requestFocus();

			}
		});
		btn_client_independent.setMargin(new Insets(2, 4, 2, 4));

		btn_generator = new JButton(UIUtil.getIcon("icons16/general/compile.png"));
		btn_generator.setFocusable(false);
		menuBar.add(btn_generator);
		btn_generator.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else if (ViewerProfileStore.getLocalViewer().getPermissions().getFlag(Perm.server.generator.generate_jar)) {
				if (UIStore.genDialog == null) {
					UIStore.genDialog = new GenDialog();
					UIStore.genDialog.setLocationRelativeTo(null);
					UIStore.genDialog.setVisible(true);
				} else {
					UIStore.genDialog.setLocationRelativeTo(null);
					UIStore.genDialog.toFront();
				}

			} else {
				MainFrame.main.np.addNote("error", "Insufficient permissions!");
			}
		});
		btn_generator.setMargin(new Insets(2, 4, 2, 4));

		btn_install_tools = new JButton(UIUtil.getIcon("icons16/general/flashdrive.png"));
		menuBar.add(btn_install_tools);
		btn_install_tools.setFocusable(false);
		btn_install_tools.setMargin(new Insets(2, 4, 2, 4));
		btn_install_tools.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon!");
		});

		btn_config = new JButton(UIUtil.getIcon("icons16/general/cog.png"));
		btn_config.setFocusable(false);
		btn_config.setFocusPainted(false);

		menuBar.add(btn_config);
		btn_config.addActionListener(e -> {
			clearButtonFocus();

			if (mp.raise(configDetail)) {
				btn_config.setFocusable(true);
				btn_config.requestFocus();

			}
		});
		btn_config.setMargin(new Insets(2, 4, 2, 4));

		btn_about = new JButton(UIUtil.getIcon("c-16.png"));
		btn_about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearButtonFocus();

				if (mp.raise(aboutDetail)) {
					btn_about.setFocusable(true);
					btn_about.requestFocus();

				}
			}
		});
		btn_about.setMargin(new Insets(2, 4, 2, 4));
		btn_about.setFocusable(false);
		btn_about.setFocusPainted(false);
		menuBar.add(btn_about);

		JButton btn_help = new JButton(UIUtil.getIcon("icons16/general/health.png"));
		btn_help.setToolTipText("Help");
		btn_help.setFocusable(false);
		menuBar.add(btn_help);
		btn_help.addActionListener(e -> {

		});
		btn_help.setMargin(new Insets(2, 4, 2, 4));

		JMenuBar panel = new JMenuBar();
		add(panel, BorderLayout.NORTH);

		mnControls = new JMenu();
		mnControls.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(mnControls);
		mnControls.setIcon(UIUtil.getIcon("icons16/general/box_front.png"));
		mnControls.setToolTipText("Controls");
		mnControls.addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				mc.startStreams();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				mc.stopStreams();
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				mc.stopStreams();
			}
		});

		mc = new MenuControls();

		mnControls.add(mc);

	}

	public void clearButtonFocus() {
		btn_about.setFocusable(false);
		btn_config.setFocusable(false);
		btn_interface.setFocusable(false);
		btn_client_independent.setFocusable(false);
		btn_install_tools.setFocusable(false);

	}

}
