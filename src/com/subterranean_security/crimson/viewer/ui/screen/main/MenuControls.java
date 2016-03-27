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

import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.component.Tray;

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

	public MenuControls() {
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
		lblConnections.setFont(new Font("Dialog", Font.BOLD, 10));
		lblConnections.setBounds(6, 17, 67, 17);
		panel_1.add(lblConnections);

		JLabel lblStopped = new JLabel("Stopped");
		lblStopped.setForeground(Color.RED);
		lblStopped.setFont(new Font("Dialog", Font.BOLD, 10));
		lblStopped.setHorizontalAlignment(SwingConstants.TRAILING);
		lblStopped.setBounds(91, 17, 95, 17);
		panel_1.add(lblStopped);

		JButton btnStartServer = new JButton("Start Server");
		btnStartServer.setBounds(12, 130, 88, 20);
		panel_1.add(btnStartServer);
		btnStartServer.setMargin(new Insets(2, 4, 2, 4));
		btnStartServer.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnStopServer = new JButton("Stop Server");
		btnStopServer.setBounds(100, 130, 88, 20);
		panel_1.add(btnStopServer);
		btnStopServer.setMargin(new Insets(2, 4, 2, 4));
		btnStopServer.setFont(new Font("Dialog", Font.BOLD, 10));

		JLabel lblLoggedInUsers = new JLabel("Users connected:");
		lblLoggedInUsers.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLoggedInUsers.setBounds(6, 34, 115, 17);
		panel_1.add(lblLoggedInUsers);

		JLabel lblTotalConn = new JLabel("Clients connected:");
		lblTotalConn.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTotalConn.setBounds(6, 51, 115, 17);
		panel_1.add(lblTotalConn);

		JLabel lblCpuTemperature_1 = new JLabel("CPU temperature:");
		lblCpuTemperature_1.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCpuTemperature_1.setBounds(6, 74, 115, 17);
		panel_1.add(lblCpuTemperature_1);

		JLabel lblCpuTemperature = new JLabel("Server CPU usage:");
		lblCpuTemperature.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCpuTemperature.setBounds(6, 91, 115, 17);
		panel_1.add(lblCpuTemperature);

		lblServerMemUsage = new JLabel("Server RAM footprint:");
		lblServerMemUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerMemUsage.setBounds(6, 109, 115, 17);
		panel_1.add(lblServerMemUsage);

		JLabel lblLoading_4 = new JLabel("loading...");
		lblLoading_4.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLoading_4.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLoading_4.setBounds(121, 34, 70, 17);
		panel_1.add(lblLoading_4);

		JLabel lblLoading_3 = new JLabel("loading...");
		lblLoading_3.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLoading_3.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLoading_3.setBounds(121, 51, 70, 17);
		panel_1.add(lblLoading_3);

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
		panel_2.setBounds(198, 0, 198, 184);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUsername.setBounds(6, 17, 69, 17);
		panel_2.add(lblUsername);

		JLabel lblAdmin = new JLabel("admin");
		lblAdmin.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAdmin.setFont(new Font("Dialog", Font.BOLD, 10));
		lblAdmin.setBounds(87, 17, 99, 17);
		panel_2.add(lblAdmin);

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

		JButton btnShutdown = new JButton("Exit");
		btnShutdown.setBounds(100, 130, 88, 20);
		panel_2.add(btnShutdown);
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnShutdown.setMargin(new Insets(2, 4, 2, 4));
		btnShutdown.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnLogOff = new JButton("Log Off");
		btnLogOff.setMargin(new Insets(2, 4, 2, 4));
		btnLogOff.setFont(new Font("Dialog", Font.BOLD, 10));
		btnLogOff.setBounds(12, 155, 88, 20);
		panel_2.add(btnLogOff);

		JLabel lblViewerRamFootprint = new JLabel("Viewer RAM footprint:");
		lblViewerRamFootprint.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerRamFootprint.setBounds(6, 109, 115, 17);
		panel_2.add(lblViewerRamFootprint);

		lblViewerCpuUsage = new JLabel("Viewer CPU usage:");
		lblViewerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerCpuUsage.setBounds(6, 91, 115, 17);
		panel_2.add(lblViewerCpuUsage);

		JLabel label_6 = new JLabel("CPU temperature:");
		label_6.setFont(new Font("Dialog", Font.BOLD, 10));
		label_6.setBounds(6, 74, 115, 17);
		panel_2.add(label_6);

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

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Views",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBounds(0, 160, 198, 77);
		panel.add(panel_4);
		panel_4.setLayout(null);

		final JToggleButton tglbtnList = new JToggleButton("Host List");
		tglbtnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.main.panel.switchToList();
			}
		});
		String view = null;
		try {
			view = ViewerStore.Databases.local.getString("view.last");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		tglbtnList.setSelected(view.equals("list"));
		tglbtnList.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnList.setMargin(new Insets(2, 4, 2, 4));
		tglbtnList.setBounds(12, 20, 88, 20);
		panel_4.add(tglbtnList);

		final JToggleButton tglbtnGraph = new JToggleButton("Host Graph");
		tglbtnGraph.setMargin(new Insets(2, 4, 2, 4));
		tglbtnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.main.panel.switchToGraph();
			}
		});
		tglbtnGraph.setSelected(view.equals("graph"));
		tglbtnGraph.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnGraph.setBounds(100, 20, 88, 20);
		panel_4.add(tglbtnGraph);

		ButtonGroup bg = new ButtonGroup();
		bg.add(tglbtnList);
		bg.add(tglbtnGraph);

		JButton btnNewButton = new JButton("History");
		btnNewButton.setEnabled(false);
		btnNewButton.setMargin(new Insets(2, 4, 2, 4));
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 10));
		btnNewButton.setBounds(12, 45, 88, 20);
		panel_4.add(btnNewButton);

		add(Box.createHorizontalStrut(width), BorderLayout.SOUTH);
		add(Box.createVerticalStrut(length), BorderLayout.EAST);

	}

	public void refresh() {
		valViewerRamUsage.setText(ViewerStore.Profiles.viewer.getCrimsonRamUsage());
		valViewerCpuTemp.setText(ViewerStore.Profiles.viewer.getCpuTemp());
		valViewerCpuUsage.setText(ViewerStore.Profiles.viewer.getCrimsonCpuUsage());
		valServerRamUsage.setText(ViewerStore.Profiles.server.getCrimsonRamUsage());
		valServerCpuTemp.setText(ViewerStore.Profiles.server.getCpuTemp());
		valServerCpuUsage.setText(ViewerStore.Profiles.server.getCrimsonCpuUsage());

	}
}
