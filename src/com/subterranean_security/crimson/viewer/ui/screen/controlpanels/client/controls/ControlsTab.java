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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.common.components.ProgressBarFactory;
import com.subterranean_security.crimson.viewer.ui.common.components.StatusConsole;
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
		refreshStatusConsole();
		refreshControls();
		startStreamingStatus();
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

		JPanel panel_3 = new JPanel();

		JScrollPane jsp = new JScrollPane(panel_3);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		panel_3.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(450, 85));
		panel_1.add(panel);
		panel.setBorder(
				new TitledBorder(UICommon.basic, "Client Power", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(null);

		powerStatusConsole = new StatusConsole(new String[] { "Uptime" });
		powerStatusConsole.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		powerStatusConsole.setSize(430, 22);
		powerStatusConsole.setLocation(12, 20);
		panel.add(powerStatusConsole);

		JPanel p_pwr_shutdown = new JPanel();
		p_pwr_shutdown.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_pwr_shutdown.setBounds(10, 45, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel.add(p_pwr_shutdown);
		p_pwr_shutdown.setLayout(new BorderLayout(0, 0));

		JProgressBar barShutdown = ProgressBarFactory.get();
		barShutdown.setPreferredSize(new Dimension(100, 4));
		p_pwr_shutdown.add(barShutdown, BorderLayout.SOUTH);

		btnShutdown = new JButton("Shutdown");
		p_pwr_shutdown.add(btnShutdown);
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setControlsEnabled(false);
				barShutdown.setIndeterminate(true);

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

		JPanel p_pwr_restart = new JPanel();
		p_pwr_restart.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_pwr_restart.setBounds(120, 45, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel.add(p_pwr_restart);
		p_pwr_restart.setLayout(new BorderLayout(0, 0));

		JProgressBar barRestart = ProgressBarFactory.get();
		barRestart.setPreferredSize(new Dimension(100, 4));
		p_pwr_restart.add(barRestart, BorderLayout.SOUTH);

		btnRestart = new JButton("Restart");
		p_pwr_restart.setPreferredSize(UICommon.dim_control_button);
		p_pwr_restart.add(btnRestart);
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

		JPanel p_pwr_standby = new JPanel();
		p_pwr_standby.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_pwr_standby.setBounds(230, 45, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel.add(p_pwr_standby);
		p_pwr_standby.setLayout(new BorderLayout(0, 0));

		JProgressBar barStandby = ProgressBarFactory.get();
		barStandby.setPreferredSize(new Dimension(100, 4));
		p_pwr_standby.add(barStandby, BorderLayout.SOUTH);

		btnStandby = new JButton("Standby");
		p_pwr_standby.add(btnStandby);
		btnStandby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				setControlsEnabled(false);
				barStandby.setIndeterminate(true);

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

		JPanel p_pwr_hibernate = new JPanel();
		p_pwr_hibernate.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_pwr_hibernate.setBounds(340, 45, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel.add(p_pwr_hibernate);
		p_pwr_hibernate.setLayout(new BorderLayout(0, 0));

		JProgressBar barHibernate = ProgressBarFactory.get();
		barHibernate.setPreferredSize(new Dimension(100, 4));
		p_pwr_hibernate.add(barHibernate, BorderLayout.SOUTH);

		btnHibernate = new JButton("Hibernate");
		p_pwr_hibernate.add(btnHibernate);
		btnHibernate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setControlsEnabled(false);
				barHibernate.setIndeterminate(true);

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
		panel_4.setPreferredSize(new Dimension(10, 130));
		panel_4.setBorder(
				new TitledBorder(UICommon.basic, "Crimson Client", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_4);

		clientStatusConsole = new StatusConsole(new String[] { "Status", "Install Date", "Location", "Last Contact" });
		clientStatusConsole.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		clientStatusConsole.setBounds(12, 20, 430, 65);

		clientStatusConsole.updateValue(1, profile.getAttr(SimpleAttribute.CLIENT_INSTALL_DATE));
		clientStatusConsole.updateValue(2, profile.getAttr(SimpleAttribute.CLIENT_BASE_PATH));

		panel_4.add(clientStatusConsole);

		JProgressBar barUninstall = ProgressBarFactory.get();
		barUninstall.setPreferredSize(new Dimension(100, 4));
		panel_4.setLayout(null);

		JPanel p_client_kill = new JPanel();
		p_client_kill.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_client_kill.setBounds(10, 90, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel_4.add(p_client_kill);
		p_client_kill.setLayout(new BorderLayout(0, 0));

		btnKill = new JButton("Kill Process");
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
		p_client_kill.add(btnKill);

		barKill = ProgressBarFactory.get();
		barKill.setPreferredSize(new Dimension(100, 4));
		p_client_kill.add(barKill, BorderLayout.SOUTH);

		JPanel p_client_restart = new JPanel();
		p_client_restart.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_client_restart.setBounds(120, 90, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel_4.add(p_client_restart);
		p_client_restart.setLayout(new BorderLayout(0, 0));

		btnRestartClient = new JButton("Restart");
		btnRestartClient.setMargin(new Insets(2, 4, 2, 4));
		btnRestartClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setControlsEnabled(false);
				barRestartClient.setIndeterminate(true);

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
		p_client_restart.add(btnRestartClient);
		btnRestartClient.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		btnRestartClient.setPreferredSize(UICommon.dim_control_button);
		btnRestartClient.setFont(new Font("Dialog", Font.BOLD, 10));

		barRestartClient = ProgressBarFactory.get();
		barRestartClient.setPreferredSize(new Dimension(100, 4));
		p_client_restart.add(barRestartClient, BorderLayout.SOUTH);

		JPanel p_client_update = new JPanel();
		p_client_update.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_client_update.setBounds(230, 90, UICommon.dim_control_button.width, UICommon.dim_control_button.height + 4);
		panel_4.add(p_client_update);
		p_client_update.setLayout(new BorderLayout(0, 0));

		JProgressBar barUpdate = ProgressBarFactory.get();
		barUpdate.setPreferredSize(new Dimension(100, 4));
		p_client_update.add(barUpdate, BorderLayout.SOUTH);

		btnUpdate = new JButton("Update");
		p_client_update.setPreferredSize(UICommon.dim_control_button);
		btnUpdate.setMargin(new Insets(2, 4, 2, 4));
		p_client_update.add(btnUpdate);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setControlsEnabled(false);
				barUpdate.setIndeterminate(true);
				stopStreamingStatus();
				clientStatusConsole.updateValue(0, "UPDATING...");
				console.addLine("Updating client (" + profile.getAttr(SimpleAttribute.CLIENT_VERSION) + " -> "
						+ Common.version + ")", LineType.BLUE);

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.updateClient(profile.getCid());
					}

					protected void done() {
						Outcome outcome = null;
						try {
							outcome = get();
						} catch (InterruptedException | ExecutionException e) {
							outcome = Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
						}

						setControlsEnabled(true);
						barUpdate.setIndeterminate(false);

						if (!outcome.getResult()) {
							console.addLine("Update failed: " + outcome.getComment(), LineType.ORANGE);
						} else {
							console.addLine("Update succeeded", LineType.GREEN);
							clientStatusConsole.updateValue(0, "RESTARTING CLIENT...");
							barUpdate.setValue(100);
							new SwingWorker<Void, Void>() {

								@Override
								protected Void doInBackground() throws Exception {
									Thread.sleep(500);
									return null;
								}

								@Override
								protected void done() {
									barUpdate.setValue(0);
								}

							}.execute();
						}

					};

				}.execute();

			}
		});
		btnUpdate.setIcon(UIUtil.getIcon("icons16/general/update.png"));
		btnUpdate.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel p_client_uninstall = new JPanel();
		p_client_uninstall.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p_client_uninstall.setBounds(340, 90, UICommon.dim_control_button.width,
				UICommon.dim_control_button.height + 4);
		panel_4.add(p_client_uninstall);
		p_client_uninstall.setLayout(new BorderLayout(0, 0));
		p_client_uninstall.add(barUninstall, BorderLayout.SOUTH);

		btnUninstall = new JButton("Uninstall");
		p_client_uninstall.add(btnUninstall);
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
						console.addLine(
								"Sending uninstall signal to client: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));

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

	private DateFormat uptimeFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");

	public void refreshStatusConsole() {
		try {
			powerStatusConsole.updateValue(0, CUtil.Misc.datediff(new Date(),
					uptimeFormat.parse(profile.getAttr(SimpleAttribute.OS_START_TIME))));
		} catch (ParseException e) {
			powerStatusConsole.updateValue(0, "N/A");
		}

		clientStatusConsole.updateValue(0, profile.getAttr(SimpleAttribute.CLIENT_STATUS));
		clientStatusConsole.updateValue(3, CUtil.Misc.datediff(new Date(), profile.getLastUpdate()));
	}

	public void refreshControls() {
		// refresh update
	}

	private Confirmation c;

	private StatusConsole clientStatusConsole;
	private StatusConsole powerStatusConsole;

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

	private InfoMaster im = null;
	private Timer refreshTimer = null;
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			refreshStatusConsole();
		}

	};

	public void startStreamingStatus() {
		if (im == null) {
			im = new InfoMaster(InfoParam.newBuilder().setClientStatus(true).build(), profile.getCid(), 1000);
			StreamStore.addStream(im);
			startRefreshingStatus();
		}
	}

	public void startRefreshingStatus() {
		if (refreshTimer == null) {
			refreshTimer = new Timer();
			refreshTimer.schedule(task, 0, 1000);
		}

	}

	public void stopStreamingStatus() {
		if (im != null) {
			StreamStore.removeStreamBySID(im.getStreamID());
			im = null;
			startRefreshingStatus();
		}
	}

	public void stopRefreshingStatus() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}

	}

	@Override
	public void tabOpened() {
		startStreamingStatus();
	}

	@Override
	public void tabClosed() {
		stopStreamingStatus();
	}

}
