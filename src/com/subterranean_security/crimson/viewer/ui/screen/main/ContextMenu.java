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

import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.cv.ui.remote.RDFrame;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel.Type;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;

public enum ContextMenu {
	;
	private static ClientProfile selected = null;

	private static JMenuItem control;
	private static JMenuItem screenshot;
	private static JMenuItem remote;
	private static JMenuItem showInGraph;
	private static JMenuItem showInList;
	private static JMenuItem showInHistory;

	private static JMenu quick;
	private static JMenuItem poweroff;
	private static JMenuItem restart;
	private static JMenuItem refresh;

	private static JMenuItem uninstall;

	public static JPopupMenu getMenu(ClientProfile cp, String view) {
		selected = cp;
		JPopupMenu popup = new JPopupMenu();
		popup.add(control);
		popup.add(new JSeparator());
		if (view.equals("list")) {
			popup.add(showInGraph);
			popup.add(showInHistory);
		}
		if (view.equals("graph")) {
			popup.add(showInList);
			popup.add(showInHistory);
		}
		if (view.equals("history")) {
			popup.add(showInGraph);
			popup.add(showInList);
		}
		popup.add(new JSeparator());

		popup.add(quick);

		return popup;
	}

	static {
		control = new JMenuItem("Control Panel");
		control.setIcon(UIUtil.getIcon("icons16/general/cog.png"));
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
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

					}
				});

			}

		});

		showInGraph = new JMenuItem("Show in Graph");
		showInGraph.setIcon(UIUtil.getIcon("icons16/general/diagramm.png"));

		showInHistory = new JMenuItem("Show in History");
		showInHistory.setIcon(UIUtil.getIcon("icons16/general/clock_history_frame.png"));

		showInList = new JMenuItem("Show in List");
		showInList.setIcon(UIUtil.getIcon("icons16/general/scroll_pane_list.png"));

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
							if (outcome.getResult()) {
								MainFrame.main.panel.console.addLine("Saved screenshot: " + outcome.getComment(),
										LineType.GREEN);
							} else {
								MainFrame.main.panel.console.addLine("Failed to capture screen", LineType.ORANGE);
							}

						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					};

				}.execute();
			}
		});
		quick.add(screenshot);

		remote = new JMenuItem("Remote Desktop");
		remote.setIcon(UIUtil.getIcon("icons16/general/monitor_wallpaper.png"));
		remote.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				RDFrame rdf = new RDFrame(Type.INTERACT, selected.getCvid());
				rdf.setVisible(true);

			}
		});
		quick.add(remote);

		JMenu state = new JMenu("Change State");
		state.setIcon(UIUtil.getIcon("icons16/general/power_surge.png"));
		quick.add(state);

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
		state.add(poweroff);

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
		state.add(restart);

		uninstall = new JMenuItem("Uninstall Crimson");
		uninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));
		state.add(uninstall);

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
		quick.add(refresh);
	}

}
