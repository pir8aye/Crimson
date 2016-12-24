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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls;

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

import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.common.components.ProgressBarFactory;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls.ep.Confirmation;

public class ControlsTab extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	private ControlsTab thisCT = this;
	private EPanel ep;
	private ClientProfile profile;
	private Console console;

	private JButton btnShutdown;
	private JButton btnRestart;
	private JButton btnStandby;
	private JButton btnHibernate;
	private JButton btnUninstall;

	private JButton btnUpdate;

	private JButton btnKill;

	private JProgressBar barKill;

	private JProgressBar barRestartClient;

	private JButton btnRestartClient;

	public ControlsTab(EPanel ep, ClientProfile profile, Console console) {
		this.ep = ep;
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
		btnKill.setEnabled(e);
		btnRestartClient.setEnabled(e);
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
				console.addLine("Sending shutdown signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.SHUTDOWN);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Saving state and shutting down in one second", LineType.GREEN);
							} else {
								console.addLine(
										"Shutdown error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
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
				console.addLine("Sending restart signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.RESTART);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Saving state and restarting in one second", LineType.GREEN);
							} else {
								console.addLine(
										"Restart error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
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
				console.addLine("Sending standby signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.STANDBY);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Saving state and standing by in one second", LineType.GREEN);
							} else {
								console.addLine(
										"Standby error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
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
				console.addLine("Sending hibernate signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.HIBERNATE);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Saving state and hibernating in one second", LineType.GREEN);
							} else {
								console.addLine(
										"Hibernation error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Hibernate failed: " + e.getMessage(), LineType.ORANGE);
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

		JProgressBar barUninstall = ProgressBarFactory.get();
		barUninstall.setPreferredSize(new Dimension(100, 4));

		JPanel panel_12 = new JPanel();
		panel_4.add(panel_12);
		panel_12.setLayout(new BorderLayout(0, 0));

		btnKill = new JButton("Kill");
		btnKill.setToolTipText("Kills the client process.");
		btnKill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setControlsEnabled(false);
				barKill.setIndeterminate(true);
				console.addLine("Sending kill signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.KILL);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Killed process", LineType.GREEN);
							} else {
								console.addLine(
										"Kill error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Process kill failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barKill.setIndeterminate(false);
					};

				}.execute();
			}
		});
		btnKill.setMargin(new Insets(2, 4, 2, 4));
		btnKill.setFont(new Font("Dialog", Font.BOLD, 10));
		btnKill.setIcon(UIUtil.getIcon("icons16/general/delete.png"));
		panel_12.add(btnKill);

		barKill = ProgressBarFactory.get();
		barKill.setPreferredSize(new Dimension(100, 4));
		panel_12.add(barKill, BorderLayout.SOUTH);

		JPanel panel_11 = new JPanel();
		panel_4.add(panel_11);
		panel_11.setLayout(new BorderLayout(0, 0));

		btnRestartClient = new JButton("Restart");
		btnRestartClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setControlsEnabled(false);
				barRestartClient.setIndeterminate(true);
				console.addLine("Sending restart signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.changeClientState(profile.getCid(), StateType.RESTART_PROCESS);
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								console.addLine("Saving state and restarting client in one second", LineType.GREEN);
							} else {
								console.addLine(
										"Restart error: "
												+ (outcome.hasComment() ? outcome.getComment() : "Unknown error"),
										LineType.ORANGE);
							}
						} catch (InterruptedException | ExecutionException e) {
							console.addLine("Restart failed: " + e.getMessage(), LineType.ORANGE);
						}

						setControlsEnabled(true);
						barRestartClient.setIndeterminate(false);
					};

				}.execute();
			}
		});
		panel_11.add(btnRestartClient);
		btnRestartClient.setIcon(UIUtil.getIcon("icons16/general/arrow_refresh.png"));
		btnRestartClient.setPreferredSize(UICommon.dim_control_button);
		btnRestartClient.setFont(new Font("Dialog", Font.BOLD, 10));

		barRestartClient = ProgressBarFactory.get();
		barRestartClient.setPreferredSize(new Dimension(100, 4));
		panel_11.add(barRestartClient, BorderLayout.SOUTH);

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
						return ViewerCommands.updateClient(profile.getCid());
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

		JPanel panel_10 = new JPanel();
		panel_4.add(panel_10);
		panel_10.setLayout(new BorderLayout(0, 0));
		panel_10.add(barUninstall, BorderLayout.SOUTH);

		btnUninstall = new JButton("Uninstall");
		panel_10.add(btnUninstall);
		btnUninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setControlsEnabled(false);

				// confirm action with user
				c = new Confirmation(ep, thisCT, "The Crimson client will be uninstalled immediately.");
				ep.raise(c, 80);

				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						synchronized (c) {
							try {
								c.wait();
							} catch (InterruptedException e1) {
							}
						}
						return null;
					}

					protected void done() {
						if (!c.getResult()) {
							setControlsEnabled(true);
							return;
						}
						barUninstall.setIndeterminate(true);
						console.addLine("Sending uninstall signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

						new SwingWorker<Outcome, Void>() {

							@Override
							protected Outcome doInBackground() throws Exception {
								return ViewerCommands.changeClientState(profile.getCid(), StateType.UNINSTALL);
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
				}.execute();

			}
		});
		btnUninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));
		btnUninstall.setMargin(new Insets(2, 4, 2, 4));
		btnUninstall.setFont(new Font("Dialog", Font.BOLD, 10));

	}

	public void refreshControls() {
		// refresh update
	}

	private Confirmation c;

	public void notifyConfirmation() {
		synchronized (c) {
			c.notifyAll();
		}
	}

	@Override
	public void clientOffline() {
		btnHibernate.setEnabled(false);
		btnKill.setEnabled(false);
		btnRestart.setEnabled(false);
		btnRestartClient.setEnabled(false);
		btnShutdown.setEnabled(false);
		btnStandby.setEnabled(false);
		btnUninstall.setEnabled(false);
		btnUpdate.setEnabled(false);

	}

	@Override
	public void serverOffline() {
		clientOffline();

	}

	@Override
	public void clientOnline() {
		btnHibernate.setEnabled(true);
		btnKill.setEnabled(true);
		btnRestart.setEnabled(true);
		btnRestartClient.setEnabled(true);
		btnShutdown.setEnabled(true);
		btnStandby.setEnabled(true);
		btnUninstall.setEnabled(true);
		btnUpdate.setEnabled(true);
	}

	@Override
	public void serverOnline() {
		// TODO Auto-generated method stub

	}

}
