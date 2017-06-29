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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.util.DateUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.State.StateType;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.common.components.ProgressBarFactory;
import com.subterranean_security.crimson.viewer.ui.common.components.StatusConsole;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls.ep.Confirmation;

public class ControlsTab extends JPanel implements CPPanel, Observer {

	private static final long serialVersionUID = 1L;

	private EPanel ep;
	private ClientProfile profile;
	private Console console;

	private StatusConsole clientStatusConsole;

	private JMenuItem mntmShutdown;
	private JMenuItem mntmRestart;
	private JMenuItem mntmStandby;
	private JMenuItem mntmHibernate;
	private JMenuItem mntmLogoff;
	private JMenuItem mntmKillProcess;
	private JMenuItem mntmRestartProcess;
	private JMenuItem mntmUpdate;
	private JMenuItem mntmUninstall;
	private JMenuItem mntmRelocate;

	private JLabel statConsoleUptime;
	private JLabel statConsoleStatus;
	private JLabel statConsoleInstallDate;
	private JLabel statConsoleLocation;
	private JLabel statConsoleLastContact;

	public ControlsTab(EPanel ep, ClientProfile profile, Console console) {
		this.ep = ep;
		this.profile = profile;
		this.console = console;

		init();
		initValues();
	}

	public void setControlsEnabled(boolean e) {
		mntmShutdown.setEnabled(e);
		mntmRestart.setEnabled(e);
		mntmStandby.setEnabled(e);
		mntmHibernate.setEnabled(e);
		mntmLogoff.setEnabled(e);
		mntmKillProcess.setEnabled(e);
		mntmRestartProcess.setEnabled(e);
		mntmUpdate.setEnabled(e);
		mntmUninstall.setEnabled(e);
		mntmRelocate.setEnabled(e);
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();

		add(panel_3, BorderLayout.CENTER);

		clientStatusConsole = new StatusConsole();
		clientStatusConsole.setPreferredSize(new Dimension(450, 100));
		panel_3.add(clientStatusConsole);
		clientStatusConsole.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		statConsoleUptime = clientStatusConsole.addRow("Uptime");
		statConsoleStatus = clientStatusConsole.addRow("Status");
		statConsoleInstallDate = clientStatusConsole.addRow("Install Date");
		statConsoleLocation = clientStatusConsole.addRow("Location");
		statConsoleLastContact = clientStatusConsole.addRow("Last Contact");

		statConsoleInstallDate.setText(profile.get(AK_CLIENT.INSTALL_DATE));
		statConsoleLocation.setText(profile.get(AK_CLIENT.BASE_PATH));

		JProgressBar barUpdate = ProgressBarFactory.get();
		barUpdate.setPreferredSize(new Dimension(100, 4));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JMenu mnPower = new JMenu("Power");
		mnPower.setIcon(UIUtil.getIcon("icons16/general/power_surge.png"));
		menuBar.add(mnPower);

		mntmShutdown = new JMenuItem("Shutdown");
		mntmShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new CommandWorker(StateType.SHUTDOWN, profile).execute();
			}
		});
		mntmShutdown.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmShutdown.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_off.png"));
		mnPower.add(mntmShutdown);

		mntmRestart = new JMenuItem("Restart");
		mntmRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CommandWorker(StateType.RESTART, profile).execute();

			}
		});
		mntmRestart.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmRestart.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		mnPower.add(mntmRestart);

		mntmStandby = new JMenuItem("Standby");
		mntmStandby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CommandWorker(StateType.STANDBY, profile).execute();
			}
		});
		mntmStandby.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmStandby.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_test.png"));
		mnPower.add(mntmStandby);

		mntmHibernate = new JMenuItem("Hibernate");
		mntmHibernate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CommandWorker(StateType.HIBERNATE, profile).execute();
			}
		});
		mntmHibernate.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmHibernate.setIcon(UIUtil.getIcon("icons16/general/wizard.png"));
		mnPower.add(mntmHibernate);

		mntmLogoff = new JMenuItem("Logoff");
		mntmLogoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// new CommandWorker(StateType.SHUTDOWN, profile).execute();
			}
		});
		mntmLogoff.setFont(new Font("Dialog", Font.BOLD, 10));
		mnPower.add(mntmLogoff);

		JMenu mnClient = new JMenu("Client");
		mnClient.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		menuBar.add(mnClient);

		mntmKillProcess = new JMenuItem("Kill Process");
		mntmKillProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CommandWorker(StateType.KILL, profile).execute();
			}
		});
		mntmKillProcess.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmKillProcess.setIcon(UIUtil.getIcon("icons16/general/delete.png"));
		mnClient.add(mntmKillProcess);

		mntmRestartProcess = new JMenuItem("Restart Process");
		mntmRestartProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CommandWorker(StateType.RESTART_PROCESS, profile).execute();
			}
		});
		mntmRestartProcess.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmRestartProcess.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		mnClient.add(mntmRestartProcess);

		mntmUpdate = new JMenuItem("Update");
		mntmUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setControlsEnabled(false);
				barUpdate.setIndeterminate(true);
				stopStreaming();
				statConsoleStatus.setText("UPDATING...");
				console.addLine("Updating client (" + profile.get(AK_META.VERSION) + " -> " + Universal.version + ")",
						LineType.BLUE);

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.updateClient(profile.getCvid());
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
							statConsoleStatus.setText("RESTARTING CLIENT...");
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
		mntmUpdate.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmUpdate.setIcon(UIUtil.getIcon("icons16/general/update.png"));
		mnClient.add(mntmUpdate);

		mntmUninstall = new JMenuItem("Uninstall");
		mntmUninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setControlsEnabled(false);

				// confirm action with user
				c = new Confirmation(ep, ControlsTab.this, "The Crimson client will be uninstalled immediately.");
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
						console.addLine("Sending uninstall signal to client: " + profile.get(AK_NET.HOSTNAME));

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
							};

						}.execute();
					}
				}.execute();
			}
		});
		mntmUninstall.setFont(new Font("Dialog", Font.BOLD, 10));
		mntmUninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));
		mnClient.add(mntmUninstall);

		mntmRelocate = new JMenuItem("Relocate");
		mntmRelocate.setFont(new Font("Dialog", Font.BOLD, 10));
		mnClient.add(mntmRelocate);

		JMenu mnSound = new JMenu("Sound");
		mnSound.setIcon(UIUtil.getIcon("icons16/general/sound.png"));
		menuBar.add(mnSound);

		JMenu mnDesktop = new JMenu("Desktop");
		menuBar.add(mnDesktop);

	}

	class CommandWorker extends SwingWorker<Outcome, Void> {
		private ClientProfile profile;
		private StateType state;

		public CommandWorker(StateType state, ClientProfile cp) {
			this.state = state;
			this.profile = cp;

			setControlsEnabled(false);
		}

		@Override
		protected Outcome doInBackground() throws Exception {
			return ViewerCommands.changeClientState(profile.getCvid(), state);
		}

		protected void done() {
			try {
				Outcome outcome = get();
				if (outcome.getResult()) {
					console.addLine("Saving state and shutting down in one second", LineType.GREEN);
				} else {
					console.addLine(
							"Shutdown error: "
									+ (outcome.getComment().isEmpty() ? "Unknown error" : outcome.getComment()),
							LineType.ORANGE);
				}
			} catch (InterruptedException | ExecutionException e) {
				console.addLine("Shutdown failed: " + e.getMessage(), LineType.ORANGE);
			}

			setControlsEnabled(true);
		};

	}

	private void initValues() {
		refreshDates();
		statConsoleStatus.setText(profile.get(AK_CLIENT.STATUS));
	}

	private static DateFormat uptimeFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");

	public void refreshDates() {
		try {
			statConsoleUptime
					.setText(DateUtil.timeBetween(new Date(), uptimeFormat.parse(profile.get(AK_OS.START_TIME))));
		} catch (ParseException e) {
			statConsoleUptime.setText("N/A");
		}

		statConsoleLastContact.setText(DateUtil.timeBetween(System.currentTimeMillis(), profile.getTimestamp()));
	}

	private Confirmation c;

	public void notifyConfirmation() {
		synchronized (c) {
			c.notifyAll();
		}
	}

	@Override
	public void clientOffline() {
		setControlsEnabled(false);
	}

	@Override
	public void serverOffline() {
		clientOffline();

	}

	@Override
	public void clientOnline() {
		setControlsEnabled(true);
	}

	@Override
	public void serverOnline() {
		// TODO check if client is online

	}

	private InfoMaster im;
	private Timer refreshTimer;

	class DateRefreshTask extends TimerTask {

		@Override
		public void run() {
			refreshDates();
		}

	};

	public void startStreaming() {
		if (im == null) {
			im = new InfoMaster(InfoParam.newBuilder().addKey(AK_CLIENT.STATUS.getWireID()).build(), profile.getCvid(),
					1000);
			StreamStore.addStream(im);
		}
	}

	public void stopStreaming() {
		if (im != null) {
			StreamStore.removeStreamBySID(im.getStreamID());
			im = null;
		}
	}

	public void startRefreshingDates() {
		if (refreshTimer == null) {
			refreshTimer = new Timer();
			// TODO use a decreasing timer
			refreshTimer.schedule(new DateRefreshTask(), 0, 1000);
		}
	}

	public void stopRefreshingDates() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	@Override
	public void tabOpened() {
		profile.addObserver(this);
		startStreaming();
		startRefreshingDates();
	}

	@Override
	public void tabClosed() {
		profile.deleteObserver(this);
		stopStreaming();
		stopRefreshingDates();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1.equals(AK_CLIENT.STATUS)) {
			statConsoleStatus.setText(profile.get(AK_CLIENT.STATUS));
		}

	}
}
