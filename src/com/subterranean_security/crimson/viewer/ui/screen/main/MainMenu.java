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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.sv.PermissionTester;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.stream.VInfoSlave;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.screen.about.AboutDialog;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame.Type;
import com.subterranean_security.crimson.viewer.ui.screen.generator.GenDialog;
import com.subterranean_security.crimson.viewer.ui.screen.netman.NetMan;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SettingsDialog;
import com.subterranean_security.crimson.viewer.ui.screen.users.UserMan;

public class MainMenu extends JPanel {

	private static final long serialVersionUID = 1L;
	public ProgressArea progressArea;
	private InfoMaster im;
	private InfoSlave is;
	private JMenu mnControls;

	public MainMenu() {
		init();
	}

	public void closeControls() {
		mnControls.setSelected(false);
	}

	public void init() {
		setForeground(UICommon.bg);
		setBackground(UICommon.bg);

		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.CENTER);

		mnControls = new JMenu("Controls");
		mnControls.addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				im = new InfoMaster(InfoParam.newBuilder().setCpuTemp(true).setCrimsonCpuUsage(true)
						.setCrimsonRamUsage(true).build(), 1000);
				StreamStore.addStream(im);
				is = new VInfoSlave(InfoParam.newBuilder().setCpuTemp(true).setCrimsonCpuUsage(true)
						.setCrimsonRamUsage(true).build());
				StreamStore.addStream(is);
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				StreamStore.removeStream(im.getStreamID());
				StreamStore.removeStream(is.getStreamID());
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				System.out.println("menuCanceled");

			}
		});
		menuBar.add(mnControls);

		MenuControls mc = new MenuControls();

		mnControls.add(mc);

		JMenu mnManagement = new JMenu("Management");
		menuBar.add(mnManagement);

		final WideMenuItem wmFiles = new WideMenuItem(UIUtil.getIcon("icons16/general/folder.png"), "Files",
				"Manage Filesystems");
		wmFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!ViewerState.isOnline()) {
					MainFrame.main.np.addNote("error", "Offline mode is enabled!");
				} else {
					FMFrame fmf = new FMFrame(Type.SV);
					fmf.setVisible(true);
				}

				wmFiles.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnManagement.add(wmFiles);

		WideMenuItem wmNetwork = new WideMenuItem(UIUtil.getIcon("icons16/general/computer.png"), "Network",
				"Manage Networking");
		wmNetwork.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (!ViewerState.isOnline()) {
					MainFrame.main.np.addNote("error", "Offline mode is enabled!");
				} else if (UIStore.netMan == null) {
					UIStore.netMan = new NetMan();
					UIStore.netMan.setLocationRelativeTo(null);
					UIStore.netMan.setVisible(true);
				} else {
					UIStore.netMan.toFront();
				}

				wmNetwork.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnManagement.add(wmNetwork);

		final WideMenuItem wmGen = new WideMenuItem(UIUtil.getIcon("icons16/general/linechart.png"), "Generator",
				"Create an Installer");
		wmGen.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!ViewerState.isOnline()) {
					MainFrame.main.np.addNote("error", "Offline mode is enabled!");
				} else if (PermissionTester.verifyServerPermission(ViewerStore.Profiles.vp.getPermissions(),
						"generate")) {
					GenDialog gd = new GenDialog();
					gd.setVisible(true);
				} else {
					// TODO
					// MainFrame.main.np.addNote("error", "Permission denied");
					GenDialog gd = new GenDialog();
					gd.setVisible(true);
				}

				wmGen.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnManagement.add(wmGen);

		WideMenuItem wmGlobalControl = new WideMenuItem(UIUtil.getIcon("icons16/general/cog.png"), "Control Panel",
				"Global Control Panel");
		wmGlobalControl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});

		mnManagement.add(wmGlobalControl);

		WideMenuItem wmLogs = new WideMenuItem(UIUtil.getIcon("icons16/general/error_log.png"), "Logs",
				"View Crimson Logs");
		wmLogs.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});

		mnManagement.add(wmLogs);

		WideMenuItem wmUsers = new WideMenuItem(UIUtil.getIcon("icons16/general/clients.png"), "Users",
				"Manage Users/Groups");
		wmUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!ViewerState.isOnline()) {
					MainFrame.main.np.addNote("error", "Offline mode is enabled!");
				} else if (UIStore.userMan == null) {
					UIStore.userMan = new UserMan();
					UIStore.userMan.setLocationRelativeTo(null);
					UIStore.userMan.setVisible(true);
				} else {
					UIStore.userMan.toFront();
				}

				wmUsers.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnManagement.add(wmUsers);

		final WideMenuItem wmSettings = new WideMenuItem(UIUtil.getIcon("icons16/general/setting_tools.png"),
				"Settings", "Edit Preferences/Settings");
		wmSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO DONT allow multiple instances
				SettingsDialog sd = new SettingsDialog();
				sd.setVisible(true);

				wmSettings.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnManagement.add(wmSettings);

		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

		final WideMenuItem wmAbout = new WideMenuItem(UIUtil.getIcon("c-16.png"), "About Crimson", "Info on Crimson");
		wmAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO DONT allow multiple instances
				AboutDialog ad = new AboutDialog();
				ad.setVisible(true);

				wmAbout.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnAbout.add(wmAbout);

		final WideMenuItem wmUpgrade = new WideMenuItem(UIUtil.getIcon("icons16/general/barcode_2d.png"),
				"Serial Codes", "Add/Remove Keys");
		wmUpgrade.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				wmUpgrade.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnAbout.add(wmUpgrade);

		final WideMenuItem wmHelp = new WideMenuItem(UIUtil.getIcon("icons16/general/health.png"), "Help",
				"Open help center");
		wmHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				MainFrame.main.np.addNote("info", "Testing the notification area");
				wmHelp.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnAbout.add(wmHelp);

		final WideMenuItem wmTour = new WideMenuItem(UIUtil.getIcon("icons16/general/steering_wheel.png"), "Tour",
				"Explore the interface");
		wmTour.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				MainFrame.main.np.addNote("info", "Testing the notification area");
				wmTour.resetBG();
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});

		mnAbout.add(wmTour);

	}
}
