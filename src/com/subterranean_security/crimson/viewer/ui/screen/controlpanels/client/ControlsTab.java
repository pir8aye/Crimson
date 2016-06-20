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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.components.ProgressBarFactory;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;

public class ControlsTab extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	private ClientProfile profile;
	private Console console;

	private JButton btnShutdown;
	private JButton btnRestart;
	private JButton btnStandby;
	private JButton btnHibernate;
	private JButton btnUninstall;

	private JButton btnUpdate;

	public ControlsTab(ClientProfile profile, Console console) {
		this.profile = profile;
		this.console = console;

		init();
		refreshControls();
	}

	public void setControlsEnabled(boolean e) {
		btnShutdown.setEnabled(e);
		btnRestart.setEnabled(e);
		btnStandby.setEnabled(e);
		btnHibernate.setEnabled(e);
		btnUninstall.setEnabled(e);
		btnUpdate.setEnabled(e);

	}

	public void init() {
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

		JPanel panel_5 = new JPanel();
		panel.add(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));

		JProgressBar barShutdown = ProgressBarFactory.get();
		barShutdown.setPreferredSize(new Dimension(100, 4));
		panel_5.add(barShutdown, BorderLayout.SOUTH);

		btnShutdown = new JButton("Shutdown");
		btnShutdown.setPreferredSize(UICommon.dim_control_button);
		panel_5.add(btnShutdown);
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setControlsEnabled(false);
				barShutdown.setIndeterminate(true);
				console.addLine("Sending shutdown signal to client: " + profile.getHostname());

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCvid(), StateType.SHUTDOWN);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Shutdown error: " + outcome.getComment());
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Shutdown failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barShutdown.setIndeterminate(false);
					};

				}.execute();

			}
		});
		btnShutdown.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_off.png"));
		btnShutdown.setMargin(new Insets(2, 4, 2, 4));
		btnShutdown.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_6 = new JPanel();
		panel.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));

		JProgressBar barRestart = ProgressBarFactory.get();
		barRestart.setPreferredSize(new Dimension(100, 4));
		panel_6.add(barRestart, BorderLayout.SOUTH);

		btnRestart = new JButton("Restart");
		btnRestart.setPreferredSize(UICommon.dim_control_button);
		panel_6.add(btnRestart);
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setControlsEnabled(false);
				barRestart.setIndeterminate(true);
				console.addLine("Sending restart signal to client: " + profile.getHostname());

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCvid(), StateType.RESTART);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Restart error: " + outcome.getComment());
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Restart failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barRestart.setIndeterminate(false);
					};

				}.execute();

			}
		});
		btnRestart.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		btnRestart.setMargin(new Insets(2, 4, 2, 4));
		btnRestart.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_7 = new JPanel();
		panel.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));

		JProgressBar barStandby = ProgressBarFactory.get();
		barStandby.setPreferredSize(new Dimension(100, 4));
		panel_7.add(barStandby, BorderLayout.SOUTH);

		btnStandby = new JButton("Standby");
		btnStandby.setPreferredSize(UICommon.dim_control_button);
		panel_7.add(btnStandby);
		btnStandby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setControlsEnabled(false);
				barStandby.setIndeterminate(true);
				console.addLine("Sending standby signal to client: " + profile.getHostname());

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCvid(), StateType.STANDBY);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Standby error: " + outcome.getComment());
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Standby failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barStandby.setIndeterminate(false);
					};

				}.execute();

			}
		});
		btnStandby.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_test.png"));
		btnStandby.setMargin(new Insets(2, 4, 2, 4));
		btnStandby.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_8 = new JPanel();
		panel.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));

		JProgressBar barHibernate = ProgressBarFactory.get();
		barHibernate.setPreferredSize(new Dimension(100, 4));
		panel_8.add(barHibernate, BorderLayout.SOUTH);

		btnHibernate = new JButton("Hibernate");
		btnHibernate.setPreferredSize(UICommon.dim_control_button);
		panel_8.add(btnHibernate);
		btnHibernate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setControlsEnabled(false);
				barHibernate.setIndeterminate(true);
				console.addLine("Sending hibernate signal to client: " + profile.getHostname());

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCvid(), StateType.HIBERNATE);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Hibernate error: " + outcome.getComment());
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Standby failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barHibernate.setIndeterminate(false);
					};

				}.execute();
			}
		});
		btnHibernate.setIcon(UIUtil.getIcon("icons16/general/wizard.png"));
		btnHibernate.setMargin(new Insets(2, 4, 2, 4));
		btnHibernate.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_4.setBorder(new TitledBorder(null, "Crimson Client", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_4);

		JPanel panel_10 = new JPanel();
		panel_4.add(panel_10);
		panel_10.setLayout(new BorderLayout(0, 0));

		JProgressBar barUninstall = ProgressBarFactory.get();
		barUninstall.setPreferredSize(new Dimension(100, 4));
		panel_10.add(barUninstall, BorderLayout.SOUTH);

		btnUninstall = new JButton("Uninstall");
		panel_10.add(btnUninstall);
		btnUninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO confirm with user
				setControlsEnabled(false);
				barUninstall.setIndeterminate(true);
				console.addLine("Sending uninstall signal to client: " + profile.getHostname());

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCvid(), StateType.UNINSTALL);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Uninstall error: " + outcome.getComment());
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Standby failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barUninstall.setIndeterminate(false);
					};

				}.execute();

			}
		});
		btnUninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));
		btnUninstall.setMargin(new Insets(2, 4, 2, 4));
		btnUninstall.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnRelocate = new JButton("Relocate");
		btnRelocate.setEnabled(false);
		btnRelocate.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_4.add(btnRelocate);

		JPanel panel_9 = new JPanel();
		panel_4.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));

		JProgressBar barUpdate = ProgressBarFactory.get();
		barUpdate.setPreferredSize(new Dimension(100, 4));
		panel_9.add(barUpdate, BorderLayout.SOUTH);

		btnUpdate = new JButton("Update");
		panel_9.add(btnUpdate);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setControlsEnabled(false);
				barUpdate.setIndeterminate(true);

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.updateClient(profile.getCvid());
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								console.addLine("Update failed: " + outcome.getComment(), LineType.ORANGE);
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Update failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barUpdate.setIndeterminate(false);
					};

				}.execute();

			}
		});
		btnUpdate.setIcon(UIUtil.getIcon("icons16/general/update.png"));
		btnUpdate.setFont(new Font("Dialog", Font.BOLD, 10));

	}

	public void refreshControls() {
		// refresh update
	}

}
