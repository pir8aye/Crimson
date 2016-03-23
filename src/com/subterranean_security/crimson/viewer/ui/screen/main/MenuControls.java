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

	public MenuControls() {
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
		lblConnections.setBounds(8, 17, 67, 17);
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
		lblLoggedInUsers.setBounds(8, 34, 115, 17);
		panel_1.add(lblLoggedInUsers);

		JLabel lblTotalConn = new JLabel("Clients connected:");
		lblTotalConn.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTotalConn.setBounds(8, 51, 115, 17);
		panel_1.add(lblTotalConn);

		JLabel lblCpuTemperature_1 = new JLabel("CPU temperature:");
		lblCpuTemperature_1.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCpuTemperature_1.setBounds(8, 74, 130, 17);
		panel_1.add(lblCpuTemperature_1);

		JLabel lblCpuTemperature = new JLabel("Crimson CPU usage:");
		lblCpuTemperature.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCpuTemperature.setBounds(8, 91, 130, 17);
		panel_1.add(lblCpuTemperature);

		JLabel lblCrimsonMemoryFootprint = new JLabel("Crimson RAM footprint:");
		lblCrimsonMemoryFootprint.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCrimsonMemoryFootprint.setBounds(8, 109, 130, 17);
		panel_1.add(lblCrimsonMemoryFootprint);

		JLabel label_1 = new JLabel("1");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setFont(new Font("Dialog", Font.BOLD, 10));
		label_1.setBounds(138, 34, 50, 17);
		panel_1.add(label_1);

		JLabel label_2 = new JLabel("0");
		label_2.setHorizontalAlignment(SwingConstants.TRAILING);
		label_2.setFont(new Font("Dialog", Font.BOLD, 10));
		label_2.setBounds(138, 51, 50, 17);
		panel_1.add(label_2);

		JLabel lblC = new JLabel("" + ViewerStore.ServerInfo.getCpuTemp());
		lblC.setHorizontalAlignment(SwingConstants.TRAILING);
		lblC.setFont(new Font("Dialog", Font.BOLD, 10));
		lblC.setBounds(138, 74, 50, 17);
		panel_1.add(lblC);

		JLabel label_4 = new JLabel("2%");
		label_4.setHorizontalAlignment(SwingConstants.TRAILING);
		label_4.setFont(new Font("Dialog", Font.BOLD, 10));
		label_4.setBounds(138, 91, 50, 17);
		panel_1.add(label_4);

		JLabel lblMb = new JLabel("3.4 MB");
		lblMb.setHorizontalAlignment(SwingConstants.TRAILING);
		lblMb.setFont(new Font("Dialog", Font.BOLD, 10));
		lblMb.setBounds(138, 109, 50, 17);
		panel_1.add(lblMb);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Local",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(198, 0, 198, 184);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUsername.setBounds(12, 17, 69, 17);
		panel_2.add(lblUsername);

		JLabel lblAdmin = new JLabel("admin");
		lblAdmin.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAdmin.setFont(new Font("Dialog", Font.BOLD, 10));
		lblAdmin.setBounds(103, 17, 83, 17);
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

		JLabel label_3 = new JLabel("Crimson RAM footprint:");
		label_3.setFont(new Font("Dialog", Font.BOLD, 10));
		label_3.setBounds(6, 109, 130, 17);
		panel_2.add(label_3);

		JLabel label_5 = new JLabel("Crimson CPU usage:");
		label_5.setFont(new Font("Dialog", Font.BOLD, 10));
		label_5.setBounds(6, 91, 130, 17);
		panel_2.add(label_5);

		JLabel label_6 = new JLabel("CPU temperature:");
		label_6.setFont(new Font("Dialog", Font.BOLD, 10));
		label_6.setBounds(6, 74, 130, 17);
		panel_2.add(label_6);

		JLabel label_7 = new JLabel("40 C");
		label_7.setHorizontalAlignment(SwingConstants.TRAILING);
		label_7.setFont(new Font("Dialog", Font.BOLD, 10));
		label_7.setBounds(136, 74, 50, 17);
		panel_2.add(label_7);

		JLabel label_8 = new JLabel("2%");
		label_8.setHorizontalAlignment(SwingConstants.TRAILING);
		label_8.setFont(new Font("Dialog", Font.BOLD, 10));
		label_8.setBounds(136, 91, 50, 17);
		panel_2.add(label_8);

		JLabel label_9 = new JLabel("3.4 MB");
		label_9.setHorizontalAlignment(SwingConstants.TRAILING);
		label_9.setFont(new Font("Dialog", Font.BOLD, 10));
		label_9.setBounds(136, 109, 50, 17);
		panel_2.add(label_9);

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
}
