/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.logs;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.proto.Log.LogFile;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;

public class Logs extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private JButton btnExport;
	private JButton btnClose;
	private JButton btnRefresh;

	public Logs(ClientProfile profile, Console console) {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		btnRefresh = new JButton(UIUtil.getIcon("icons16/general/arrow_refresh.png"));
		btnRefresh.setToolTipText("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Iterable<LogFile>, Void>() {

					@Override
					protected Iterable<LogFile> doInBackground() throws Exception {
						List<LogFile> logs = new ArrayList<LogFile>();

						try {

							logs.add(ViewerCommands.getLog(profile.getCvid(),
									((LogPane) tabbedPane.getSelectedComponent()).getLogType()));
						} catch (Throwable e) {
							logs.addAll(ViewerCommands.getLogs(profile.getCvid()));
						}

						return logs;
					}

					protected void done() {
						try {
							for (LogFile lf : get()) {
								if (lf == null) {
									continue;
								}
								boolean mod = false;
								for (int i = 0; i < tabbedPane.getTabCount(); i++) {
									LogPane pane = (LogPane) tabbedPane.getComponentAt(i);
									if (pane == null) {
										System.out.println("pane is null");
									}

									if (pane.getLogType() == lf.getName()) {
										pane.setLog(lf.getLog());
										mod = true;
										break;
									}

								}
								if (!mod) {
									LogPane lp = new LogPane(lf.getName(), lf.getLog());
									tabbedPane.add(lf.getName().toString(), lp);
									btnExport.setEnabled(true);
									btnClose.setEnabled(true);
								}

							}
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.execute();
			}
		});
		btnRefresh.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnRefresh);

		btnExport = new JButton(UIUtil.getIcon("icons16/general/export_log.png"));
		btnExport.setToolTipText("Export log to filesystem");
		btnExport.setMargin(new Insets(2, 2, 2, 2));
		btnExport.setEnabled(false);
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnExport.setEnabled(false);

				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Export log");

				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						if (jfc.showDialog(null, "Export") == JFileChooser.APPROVE_OPTION) {
							File file = jfc.getSelectedFile();
							PrintWriter pw = new PrintWriter(file);
							pw.print(((LogPane) tabbedPane.getSelectedComponent()).getLog());
							pw.close();
						}
						return null;
					}

					protected void done() {
						btnExport.setEnabled(true);
					};

				}.execute();
			}
		});
		menuBar.add(btnExport);

		btnClose = new JButton(UIUtil.getIcon("icons16/general/close_log.png"));
		btnClose.setToolTipText("Close tab");
		btnClose.setMargin(new Insets(2, 2, 2, 2));
		btnClose.setEnabled(false);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
				if (tabbedPane.getTabCount() == 0) {
					btnClose.setEnabled(false);
					btnExport.setEnabled(false);
				}
			}
		});
		menuBar.add(btnClose);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(tabbedPane, BorderLayout.CENTER);

		JProgressBar progressBar = new JProgressBar();
		add(progressBar, BorderLayout.SOUTH);
	}

	@Override
	public void clientOffline() {
		btnRefresh.setEnabled(false);

	}

	@Override
	public void serverOffline() {
		clientOffline();

	}

	@Override
	public void clientOnline() {
		btnRefresh.setEnabled(true);

	}

	@Override
	public void serverOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabClosed() {
		// TODO Auto-generated method stub

	}

}
