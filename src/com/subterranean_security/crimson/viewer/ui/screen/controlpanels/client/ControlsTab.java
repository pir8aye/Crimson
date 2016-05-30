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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.panel.Console;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class ControlsTab extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	public ControlsTab(ClientProfile profile, Console console) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);

		JPanel panel_1 = new JPanel();
		panel_3.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel_1.add(panel);
		panel.setBorder(new TitledBorder(null, "Client Power", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JButton btnShutdown = new JButton("Shutdown");
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						btnShutdown.setEnabled(false);
						console.addLine("Sending shutdown signal to client: " + profile.getHostname());
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, profile.getCvid(), StateType.SHUTDOWN)) {
							console.addLine("Shutdown error: " + error.toString());
						}
						btnShutdown.setEnabled(true);
					}
				}).start();

			}
		});
		btnShutdown.setIcon(UUtil.getIcon("icons16/general/lcd_tv_off.png"));
		btnShutdown.setMargin(new Insets(2, 4, 2, 4));
		btnShutdown.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnShutdown);

		JButton btnRestart = new JButton("Restart");
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						btnRestart.setEnabled(false);
						console.addLine("Sending restart signal to client: " + profile.getHostname());
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, profile.getCvid(), StateType.RESTART)) {
							console.addLine("Restart error: " + error.toString());
						}
						btnRestart.setEnabled(true);
					}
				}).start();

			}
		});
		btnRestart.setIcon(UUtil.getIcon("icons16/general/arrow_redo.png"));
		btnRestart.setMargin(new Insets(2, 4, 2, 4));
		btnRestart.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnRestart);

		JButton btnStandby = new JButton("Standby");
		btnStandby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						btnStandby.setEnabled(false);
						console.addLine("Sending standby signal to client: " + profile.getHostname());
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, profile.getCvid(), StateType.STANDBY)) {
							console.addLine("Standby error: " + error.toString());
						}
						btnStandby.setEnabled(true);
					}
				}).start();
			}
		});
		btnStandby.setIcon(UUtil.getIcon("icons16/general/lcd_tv_test.png"));
		btnStandby.setMargin(new Insets(2, 4, 2, 4));
		btnStandby.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnStandby);

		JButton btnHibernate = new JButton("Hibernate");
		btnHibernate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						btnHibernate.setEnabled(false);
						console.addLine("Sending hibernate signal to client: " + profile.getHostname());
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, profile.getCvid(), StateType.HIBERNATE)) {
							console.addLine("Hibernate error: " + error.toString());
						}
						btnHibernate.setEnabled(true);
					}
				}).start();
			}
		});
		btnHibernate.setIcon(UUtil.getIcon("icons16/general/wizard.png"));
		btnHibernate.setMargin(new Insets(2, 4, 2, 4));
		btnHibernate.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnHibernate);

		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_4.setBorder(new TitledBorder(null, "Crimson Client", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_4);

		JButton btnUninstall = new JButton("Uninstall");
		btnUninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						btnUninstall.setEnabled(false);
						console.addLine("Sending uninstall signal to client: " + profile.getHostname());
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, profile.getCvid(), StateType.UNINSTALL)) {
							console.addLine("Uninstall error: " + error.toString());
						}
						btnUninstall.setEnabled(true);
					}
				}).start();
			}
		});
		btnUninstall.setIcon(UUtil.getIcon("icons16/general/radioactivity.png"));
		btnUninstall.setMargin(new Insets(2, 4, 2, 4));
		btnUninstall.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_4.add(btnUninstall);
		
		JButton btnRelocate = new JButton("Relocate");
		btnRelocate.setEnabled(false);
		btnRelocate.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_4.add(btnRelocate);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.setEnabled(false);
		btnUpdate.setIcon(UUtil.getIcon("icons16/general/upload_for_cloud.png"));
		btnUpdate.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_4.add(btnUpdate);

	}

	public void init() {

	}

}
