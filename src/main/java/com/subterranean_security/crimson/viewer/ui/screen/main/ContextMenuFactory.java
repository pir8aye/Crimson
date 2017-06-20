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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.cv.ui.remote.RDFrame;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel.Type;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.State.StateType;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.net.command.NetworkCom;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;

public final class ContextMenuFactory {
	private ContextMenuFactory() {
	}

	private static ClientProfile selected;

	// main menu
	private static JMenuItem control;
	private static JMenuItem showInGraph;
	private static JMenuItem showInList;
	private static JMenuItem showInHistory;

	// quick menu
	private static JMenu quick;
	private static JMenuItem screenshot;
	private static JMenuItem remote;

	// state menu
	private static JMenu state;
	private static JMenuItem poweroff;
	private static JMenuItem restart;
	private static JMenuItem refresh;
	private static JMenuItem uninstall;

	// network menu
	private static JMenu network;
	private static JMenuItem trace;
	private static JMenuItem establishDirect;
	private static JMenuItem removeDirect;

	public static JPopupMenu getMenu(ClientProfile cp, String view) {
		selected = cp;

		// setup network menu
		network.removeAll();
		network.add(trace);
		if (ConnectionStore.connectedDirectly(cp.getCvid())) {
			network.add(removeDirect);
		} else {
			network.add(establishDirect);
		}

		// setup state menu
		state.removeAll();
		state.add(poweroff);
		state.add(restart);
		state.add(uninstall);

		// setup quick menu
		quick.removeAll();
		quick.add(network);
		quick.add(screenshot);
		quick.add(remote);
		quick.add(state);
		quick.add(refresh);

		// setup main menu
		JPopupMenu main = new JPopupMenu();
		main.add(control);
		main.add(new JSeparator());
		if (view.equals("list")) {
			main.add(showInGraph);
			main.add(showInHistory);
		}
		if (view.equals("graph")) {
			main.add(showInList);
			main.add(showInHistory);
		}
		if (view.equals("history")) {
			main.add(showInGraph);
			main.add(showInList);
		}
		main.add(new JSeparator());
		main.add(quick);

		return main;
	}

	static {
		control = new JMenuItem("Control Panel");
		control.setIcon(UIUtil.getIcon("icons16/general/cog.png"));
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				SwingUtilities.invokeLater(() -> {
					for (ClientCPFrame frame : UIStore.clientControlPanels) {
						if (frame.profile.getCvid() == selected.getCvid()) {
							// there is already an open control panel
							frame.setLocationRelativeTo(null);
							frame.toFront();
							return;
						}
					}
					ClientCPFrame ccpf = new ClientCPFrame(selected);
					UIStore.clientControlPanels.add(ccpf);
					ccpf.setLocationRelativeTo(null);
					ccpf.setVisible(true);
				});

			}

		});

		showInGraph = new JMenuItem("Show in Graph");
		showInGraph.setIcon(UIUtil.getIcon("icons16/general/diagramm.png"));
		showInGraph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// switch to graph
				MainFrame.main.panel.switchToGraph();

				// select in graph
				MainFrame.main.panel.graph.select(selected);
			}
		});

		showInHistory = new JMenuItem("Show in History");
		showInHistory.setIcon(UIUtil.getIcon("icons16/general/clock_history_frame.png"));

		showInList = new JMenuItem("Show in List");
		showInList.setIcon(UIUtil.getIcon("icons16/general/scroll_pane_list.png"));
		showInList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// switch to list
				MainFrame.main.panel.switchToList();

				// select in list
				MainFrame.main.panel.list.select(selected);
			}
		});

		quick = new JMenu("Quick Commands");
		quick.setIcon(UIUtil.getIcon("icons16/general/bow.png"));

		screenshot = new JMenuItem("Screenshot");
		screenshot.setIcon(UIUtil.getIcon("icons16/general/picture.png"));
		screenshot.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						return ViewerCommands.quickScreenshot(selected.getCvid());
					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (outcome != null && outcome.getResult()) {
								UINotification.addConsoleGood("Saved screenshot: " + outcome.getComment());
							} else {
								UINotification.addConsoleBad("Failed to capture screenshot");
							}

						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					};

				}.execute();
			}
		});

		remote = new JMenuItem("Remote Desktop");
		remote.setIcon(UIUtil.getIcon("icons16/general/monitor_wallpaper.png"));
		remote.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				RDFrame rdf = new RDFrame(Type.INTERACT, selected.getCvid());
				rdf.setVisible(true);

			}
		});

		state = new JMenu("Change State");
		state.setIcon(UIUtil.getIcon("icons16/general/power_surge.png"));

		poweroff = new JMenuItem("Shutdown");
		poweroff.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_off.png"));
		poweroff.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						Outcome outcome = ViewerCommands.changeClientState(selected.getCvid(), StateType.SHUTDOWN);
						if (!outcome.getResult()) {
							// TODO
						}
					}
				}.start();

			}

		});

		restart = new JMenuItem("Restart");
		restart.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		restart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						Outcome outcome = ViewerCommands.changeClientState(selected.getCvid(), StateType.RESTART);
						if (!outcome.getResult()) {
							// TODO
						}
					}
				}.start();

			}

		});

		uninstall = new JMenuItem("Uninstall Crimson");
		uninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));

		refresh = new JMenuItem("Refresh");
		refresh.setIcon(UIUtil.getIcon("icons16/general/inbox_download.png"));
		refresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {

					}
				}.start();

			}

		});

		network = new JMenu("Network");
		network.setIcon(UIUtil.getIcon("icons16/general/network_ethernet.png"));

		trace = new JMenuItem("Traceroute");
		trace.setIcon(UIUtil.getIcon("icons16/general/traceroute.png"));
		trace.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {

					}
				}.start();

			}

		});
		quick.add(trace);

		establishDirect = new JMenuItem("Establish Direct Connection");
		establishDirect.setIcon(UIUtil.getIcon("icons16/general/networking_green.png"));
		establishDirect.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						NetworkCom.establishDirectConnection(selected.getCvid());
					}
				}.start();

			}

		});

		removeDirect = new JMenuItem("Remove Direct Connection");
		removeDirect.setIcon(UIUtil.getIcon("icons16/general/networking_red.png"));
		removeDirect.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {

					}
				}.start();

			}

		});
	}

}
