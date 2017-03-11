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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.stream.VInfoSlave;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.Tray;

public class MenuControls extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int width = 400;
	private static final int length = 240;
	private JLabel valViewerRamUsage;
	private JLabel lblServerMemUsage;
	private JLabel lblViewerCpuUsage;
	private JLabel valViewerCpuUsage;
	private JLabel valViewerCpuTemp;
	private JLabel valServerRamUsage;
	private JLabel valServerCpuUsage;
	private JLabel valServerCpuTemp;

	public static MenuControls mc;
	private JLabel valClients;
	private JLabel valUsers;
	private JLabel valStatus;
	private JButton btnStartServer;
	private JButton btnStopServer;
	private JLabel valIp;
	private JLabel valUsername;
	private JToggleButton tglbtnList;
	private JToggleButton tglbtnGraph;

	public MenuControls() {
		init();
		String view = null;
		try {
			view = PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST);
		} catch (Exception e1) {
			view = "list";
		}
		tglbtnList.setSelected(view.equals("list"));
		tglbtnGraph.setSelected(view.equals("graph"));
	}

	public void init() {
		mc = this;
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setSize(new Dimension(400, 240));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Server",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(0, 0, 198, 160);
		panel.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblConnections = new JLabel("Status:");
		lblConnections.setIcon(UIUtil.getIcon("icons16/general/server.png"));
		lblConnections.setFont(new Font("Dialog", Font.BOLD, 10));
		lblConnections.setBounds(6, 17, 67, 17);
		panel_1.add(lblConnections);

		valStatus = new JLabel("Loading...");
		valStatus.setFont(new Font("Dialog", Font.BOLD, 10));
		valStatus.setHorizontalAlignment(SwingConstants.TRAILING);
		valStatus.setBounds(91, 17, 95, 17);
		panel_1.add(valStatus);

		btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						// TODO handle errors
						btnStartServer.setEnabled(false);
						btnStopServer.setEnabled(false);
						ViewerCommands.changeServerState(StateType.FUNCTIONING_ON);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						refresh();
					}
				}).start();

			}
		});
		btnStartServer.setBounds(12, 130, 88, 20);
		panel_1.add(btnStartServer);
		btnStartServer.setMargin(new Insets(2, 4, 2, 4));
		btnStartServer.setFont(new Font("Dialog", Font.BOLD, 10));

		btnStopServer = new JButton("Stop Server");
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						// TODO handle errors
						btnStartServer.setEnabled(false);
						btnStopServer.setEnabled(false);
						ViewerCommands.changeServerState(StateType.FUNCTIONING_OFF);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						refresh();
					}
				}).start();
			}
		});
		btnStopServer.setBounds(100, 130, 88, 20);
		panel_1.add(btnStopServer);
		btnStopServer.setMargin(new Insets(2, 4, 2, 4));
		btnStopServer.setFont(new Font("Dialog", Font.BOLD, 10));

		JLabel lblLoggedInUsers = new JLabel("Users connected:");
		lblLoggedInUsers.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		lblLoggedInUsers.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLoggedInUsers.setBounds(6, 34, 115, 17);
		panel_1.add(lblLoggedInUsers);

		JLabel lblTotalConn = new JLabel("Clients connected:");
		lblTotalConn.setIcon(UIUtil.getIcon("icons16/general/users_3.png"));
		lblTotalConn.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTotalConn.setBounds(6, 51, 115, 17);
		panel_1.add(lblTotalConn);

		JLabel lblServerCpuTemp = new JLabel("CPU temperature:");
		lblServerCpuTemp.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblServerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerCpuTemp.setBounds(6, 74, 115, 17);
		panel_1.add(lblServerCpuTemp);

		JLabel lblServerCpuUsage = new JLabel("CPU usage:");
		lblServerCpuUsage.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblServerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerCpuUsage.setBounds(6, 91, 115, 17);
		panel_1.add(lblServerCpuUsage);

		lblServerMemUsage = new JLabel("RAM footprint:");
		lblServerMemUsage.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		lblServerMemUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerMemUsage.setBounds(6, 109, 115, 17);
		panel_1.add(lblServerMemUsage);

		valUsers = new JLabel("loading...");
		valUsers.setHorizontalAlignment(SwingConstants.TRAILING);
		valUsers.setFont(new Font("Dialog", Font.BOLD, 10));
		valUsers.setBounds(121, 34, 70, 17);
		panel_1.add(valUsers);

		valClients = new JLabel("loading...");
		valClients.setHorizontalAlignment(SwingConstants.TRAILING);
		valClients.setFont(new Font("Dialog", Font.BOLD, 10));
		valClients.setBounds(121, 51, 70, 17);
		panel_1.add(valClients);

		valServerCpuTemp = new JLabel("loading...");
		valServerCpuTemp.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerCpuTemp.setBounds(121, 74, 70, 17);
		panel_1.add(valServerCpuTemp);

		valServerCpuUsage = new JLabel("loading...");
		valServerCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerCpuUsage.setBounds(121, 91, 70, 17);
		panel_1.add(valServerCpuUsage);

		valServerRamUsage = new JLabel("loading...");
		valServerRamUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerRamUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerRamUsage.setBounds(121, 109, 70, 17);
		panel_1.add(valServerRamUsage);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Local",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(198, 0, 198, 160);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUsername.setBounds(6, 17, 94, 17);
		panel_2.add(lblUsername);

		valUsername = new JLabel("loading...");
		valUsername.setHorizontalAlignment(SwingConstants.TRAILING);
		valUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		valUsername.setBounds(87, 17, 104, 17);
		panel_2.add(valUsername);

		JButton btnCloseToTray = new JButton("Run in Tray");
		btnCloseToTray.setBounds(12, 130, 88, 20);
		panel_2.add(btnCloseToTray);
		btnCloseToTray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tray.addTray();
			}
		});
		btnCloseToTray.setMargin(new Insets(2, 4, 2, 4));
		btnCloseToTray.setEnabled(SystemTray.isSupported());
		btnCloseToTray.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnLogOff = new JButton("Log Off");
		btnLogOff.setMargin(new Insets(2, 4, 2, 4));
		btnLogOff.setFont(new Font("Dialog", Font.BOLD, 10));
		btnLogOff.setBounds(100, 130, 88, 20);
		panel_2.add(btnLogOff);

		JLabel lblViewerRamFootprint = new JLabel("RAM footprint:");
		lblViewerRamFootprint.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		lblViewerRamFootprint.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerRamFootprint.setBounds(6, 109, 115, 17);
		panel_2.add(lblViewerRamFootprint);

		lblViewerCpuUsage = new JLabel("CPU usage:");
		lblViewerCpuUsage.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblViewerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerCpuUsage.setBounds(6, 91, 115, 17);
		panel_2.add(lblViewerCpuUsage);

		JLabel lblViewerCpuTemp = new JLabel("CPU temperature:");
		lblViewerCpuTemp.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblViewerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerCpuTemp.setBounds(6, 74, 115, 17);
		panel_2.add(lblViewerCpuTemp);

		valViewerCpuTemp = new JLabel("loading...");
		valViewerCpuTemp.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerCpuTemp.setBounds(121, 74, 70, 17);
		panel_2.add(valViewerCpuTemp);

		valViewerCpuUsage = new JLabel("loading...");
		valViewerCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerCpuUsage.setBounds(121, 91, 70, 17);
		panel_2.add(valViewerCpuUsage);

		valViewerRamUsage = new JLabel("loading...");
		valViewerRamUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerRamUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerRamUsage.setBounds(121, 109, 70, 17);
		panel_2.add(valViewerRamUsage);

		JLabel lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		lblIpAddress.setFont(new Font("Dialog", Font.BOLD, 10));
		lblIpAddress.setBounds(6, 34, 88, 17);
		panel_2.add(lblIpAddress);

		valIp = new JLabel("loading...");
		valIp.setHorizontalAlignment(SwingConstants.TRAILING);
		valIp.setFont(new Font("Dialog", Font.BOLD, 10));
		valIp.setBounds(87, 34, 104, 17);
		panel_2.add(valIp);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Views",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBounds(198, 160, 198, 77);
		panel.add(panel_4);
		panel_4.setLayout(null);

		tglbtnList = new JToggleButton("Host List");
		tglbtnList.setIcon(UIUtil.getIcon("icons16/general/view_list.png"));
		tglbtnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.main.panel.switchToList();
				MainFrame.main.mm.closeControls();
			}
		});

		tglbtnList.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnList.setMargin(new Insets(2, 2, 2, 2));
		tglbtnList.setBounds(12, 20, 88, 20);
		panel_4.add(tglbtnList);

		tglbtnGraph = new JToggleButton("Host Graph");
		tglbtnGraph.setIcon(UIUtil.getIcon("icons16/general/view_graph.png"));
		tglbtnGraph.setMargin(new Insets(2, 2, 2, 2));
		tglbtnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.main.panel.switchToGraph();
				MainFrame.main.mm.closeControls();
			}
		});

		tglbtnGraph.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnGraph.setBounds(100, 20, 88, 20);
		panel_4.add(tglbtnGraph);

		ButtonGroup bg = new ButtonGroup();
		bg.add(tglbtnList);
		bg.add(tglbtnGraph);

		JButton btnViewHistory = new JButton("History");
		btnViewHistory.setIcon(UIUtil.getIcon("icons16/general/view_history.png"));
		btnViewHistory.setEnabled(false);
		btnViewHistory.setMargin(new Insets(2, 4, 2, 4));
		btnViewHistory.setFont(new Font("Dialog", Font.BOLD, 10));
		btnViewHistory.setBounds(12, 45, 88, 20);
		panel_4.add(btnViewHistory);

		JButton btnViewConsole = new JButton("Console");
		btnViewConsole.setMargin(new Insets(2, 2, 2, 2));
		btnViewConsole.setIcon(UIUtil.getIcon("icons16/general/view_console.png"));
		btnViewConsole.setSelected(true);
		btnViewConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!MainFrame.main.ep.isMoving()) {
					btnViewConsole.setSelected(!btnViewConsole.isSelected());
					if (btnViewConsole.isSelected()) {
						MainFrame.main.panel.openConsole();
					} else {
						MainFrame.main.panel.closeConsole();
					}
				}

			}
		});
		btnViewConsole.setFont(new Font("Dialog", Font.BOLD, 10));
		btnViewConsole.setBounds(100, 45, 88, 20);
		panel_4.add(btnViewConsole);

		add(Box.createHorizontalStrut(width), BorderLayout.SOUTH);
		add(Box.createVerticalStrut(length), BorderLayout.EAST);

	}

	public void refresh() {
		valViewerRamUsage.setText(ProfileStore.getLocalClient().getAttr(SimpleAttribute.CLIENT_RAM_USAGE));
		valViewerCpuTemp.setText(
				ProfileStore.getLocalClient().getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TEMP).get());
		valViewerCpuUsage.setText(ProfileStore.getLocalClient().getAttr(SimpleAttribute.CLIENT_CPU_USAGE) + " %");
		valServerRamUsage.setText(ViewerState.isOnline() ? ProfileStore.getServer().getCrimsonRamUsage() : "");
		valServerCpuTemp.setText(ViewerState.isOnline() ? ProfileStore.getServer().getCpuTemp() : "");
		valServerCpuUsage.setText(ViewerState.isOnline() ? ProfileStore.getServer().getCrimsonCpuUsage() + " %" : "");
		valClients.setText(ViewerState.isOnline() ? "" + ProfileStore.getServer().getConnectedClients() : "");
		valUsers.setText(ViewerState.isOnline() ? "" + ProfileStore.getServer().getConnectedUsers() : "");
		valIp.setText(ProfileStore.getLocalViewer().getIp());
		valUsername.setText(ProfileStore.getLocalViewer().getUser());

		if (!ViewerState.isOnline()) {
			valStatus.setText("Offline");
			valStatus.setForeground(Color.gray);// TODO
			btnStartServer.setEnabled(false);
			btnStopServer.setEnabled(false);
		} else if (ProfileStore.getServer().getStatus()) {
			valStatus.setText("Running");
			valStatus.setForeground(new Color(0, 149, 39));
			btnStartServer.setEnabled(false);
			btnStopServer.setEnabled(true);
		} else {
			valStatus.setText("Stopped");
			valStatus.setForeground(new Color(200, 0, 0));
			btnStartServer.setEnabled(true);
			btnStopServer.setEnabled(false);
		}

	}

	private InfoMaster im;
	private InfoSlave is;

	public void startStreams() {
		im = new InfoMaster(
				InfoParam.newBuilder().setCpuTemp(true).setCrimsonCpuUsage(true).setCrimsonRamUsage(true).build(),
				1000);
		StreamStore.addStream(im);
		is = new VInfoSlave(
				InfoParam.newBuilder().setCpuTemp(true).setCrimsonCpuUsage(true).setCrimsonRamUsage(true).build());
		StreamStore.addStream(is);
	}

	public void stopStreams() {
		StreamStore.removeStreamBySID(im.getStreamID());
		StreamStore.removeStreamBySID(is.getStreamID());
	}
}
