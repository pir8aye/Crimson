package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.logs;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
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

	public Logs(ClientProfile profile, Console console) {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JButton btnNewButton = new JButton(UIUtil.getIcon("icons16/general/arrow_refresh.png"));
		btnNewButton.addActionListener(new ActionListener() {
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
								for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
									LogPane pane = (LogPane) tabbedPane.getTabComponentAt(i);
									if (pane.getLogType() == lf.getName()) {
										pane.setLog(lf.getLog());
										return;
									}

								}
								LogPane lp = new LogPane(lf.getName(), lf.getLog());
								tabbedPane.add(lf.getName().toString(), lp);

							}
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.execute();
			}
		});
		btnNewButton.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("export");
		menuBar.add(btnNewButton_1);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(tabbedPane, BorderLayout.CENTER);

		JProgressBar progressBar = new JProgressBar();
		add(progressBar, BorderLayout.SOUTH);
	}

}
