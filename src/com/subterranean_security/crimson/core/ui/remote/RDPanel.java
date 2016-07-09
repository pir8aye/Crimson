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
package com.subterranean_security.crimson.core.ui.remote;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.proto.Stream.RemoteParam;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam.RMethod;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import javax.swing.border.EtchedBorder;

public class RDPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private RemoteMaster stream;

	private RDArea rdArea = new RDArea();
	private boolean running;
	private int cvid;

	public enum Type {
		VIEW_ONLY, INTERACT;
	}

	public RDPanel(Type type, int cvid) {
		this.cvid = cvid;
		init();

	}

	private JProgressBar barScreenshot;
	private JProgressBar barToggle;
	private JProgressBar barKeyToggle;
	private JProgressBar barMouseToggle;

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JPanel toggle = new JPanel();
		toggle.setMaximumSize(new Dimension(26, 30));
		toggle.setPreferredSize(new Dimension(26, 30));
		toggle.setLayout(new BorderLayout());
		menuBar.add(toggle);

		barToggle = new JProgressBar();
		barToggle.setPreferredSize(new Dimension(148, 4));
		toggle.add(barToggle, BorderLayout.SOUTH);

		JButton btnToggle = new JButton(UIUtil.getIcon("icons16/general/map_go.png"));
		btnToggle.setFocusable(false);
		btnToggle.setToolTipText("Start");
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				barToggle.setIndeterminate(true);
				btnToggle.setEnabled(false);
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						if (running) {
							running = false;
							btnToggle.setIcon(UIUtil.getIcon("icons16/general/map_go.png"));
							btnToggle.setToolTipText("Start");
							StreamStore.removeStream(stream.getStreamID());
						} else {
							running = true;
							btnToggle.setIcon(UIUtil.getIcon("icons16/general/map_delete.png"));
							btnToggle.setToolTipText("Stop");
							stream = new RemoteMaster(RemoteParam.newBuilder().setRmethod(RMethod.POLL).build(), cvid,
									rdArea);
							StreamStore.addStream(stream);
							stream.start();
							rdArea.start(stream);
						}

						return null;
					}

					protected void done() {
						barToggle.setIndeterminate(false);
						btnToggle.setEnabled(true);
					};
				}.execute();
			}
		});
		btnToggle.setMargin(new Insets(2, 2, 2, 2));
		toggle.add(btnToggle, BorderLayout.CENTER);

		JPanel screenshot = new JPanel();
		screenshot.setMaximumSize(new Dimension(26, 30));
		screenshot.setPreferredSize(new Dimension(26, 30));
		screenshot.setLayout(new BorderLayout());
		menuBar.add(screenshot);

		JButton btnScreenshot = new JButton(UIUtil.getIcon("icons16/general/picture.png"));
		btnScreenshot.setFocusable(false);
		btnScreenshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				barScreenshot.setIndeterminate(true);
				btnScreenshot.setEnabled(false);
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						File file = new File(
								System.getProperty("user.home") + "/Crimson/" + new Date().getTime() + ".jpg");
						file.getParentFile().mkdirs();

						ImageIO.write(rdArea.screenshot(), "jpg", file);

						return null;
					}

					protected void done() {
						barScreenshot.setIndeterminate(false);
						btnScreenshot.setEnabled(true);
					};
				}.execute();
			}
		});
		btnScreenshot.setToolTipText("Screenshot");
		btnScreenshot.setMargin(new Insets(2, 2, 2, 2));
		screenshot.add(btnScreenshot, BorderLayout.CENTER);

		barScreenshot = new JProgressBar();
		barScreenshot.setPreferredSize(new Dimension(148, 4));
		screenshot.add(barScreenshot, BorderLayout.SOUTH);

		JPanel keyToggle = new JPanel();
		keyToggle.setMaximumSize(new Dimension(26, 30));
		keyToggle.setPreferredSize(new Dimension(26, 30));
		keyToggle.setLayout(new BorderLayout());
		menuBar.add(keyToggle);

		barKeyToggle = new JProgressBar();
		barKeyToggle.setPreferredSize(new Dimension(148, 4));
		keyToggle.add(barKeyToggle, BorderLayout.SOUTH);

		JButton btnKeyToggle = new JButton(UIUtil.getIcon("icons16/general/keyboard.png"));
		btnKeyToggle.setToolTipText("Keyboard capture is disabled");
		btnKeyToggle.setFocusable(false);
		btnKeyToggle.addActionListener(new ActionListener() {
			private boolean installed = false;

			public void actionPerformed(ActionEvent arg0) {
				if (installed) {
					installed = false;
					btnKeyToggle.setToolTipText("Keyboard capture is disabled");
					btnKeyToggle.setSelected(false);
					rdArea.uninstallKeyAdapters();
				} else {
					installed = true;
					btnKeyToggle.setToolTipText("Keyboard capture is enabled");
					btnKeyToggle.setSelected(true);
					rdArea.installKeyAdapters();
				}
			}
		});
		btnKeyToggle.setMargin(new Insets(2, 2, 2, 2));
		keyToggle.add(btnKeyToggle);

		JPanel mouseToggle = new JPanel();
		mouseToggle.setMaximumSize(new Dimension(26, 30));
		mouseToggle.setPreferredSize(new Dimension(26, 30));
		mouseToggle.setLayout(new BorderLayout());
		menuBar.add(mouseToggle);

		barMouseToggle = new JProgressBar();
		barMouseToggle.setPreferredSize(new Dimension(148, 4));
		mouseToggle.add(barMouseToggle, BorderLayout.SOUTH);

		JButton btnMouseToggle = new JButton(UIUtil.getIcon("icons16/general/mouse.png"));
		btnMouseToggle.setToolTipText("Mouse capture is disabled");
		btnMouseToggle.setFocusable(false);
		btnMouseToggle.addActionListener(new ActionListener() {
			private boolean installed = false;

			public void actionPerformed(ActionEvent e) {
				if (installed) {
					installed = false;
					btnMouseToggle.setToolTipText("Mouse capture is disabled");
					btnMouseToggle.setSelected(false);
					rdArea.uninstallMouseAdapters();
				} else {
					installed = true;
					btnMouseToggle.setToolTipText("Mouse capture is enabled");
					btnMouseToggle.setSelected(true);
					rdArea.installMouseAdapters();
				}
			}
		});
		btnMouseToggle.setMargin(new Insets(2, 2, 2, 2));
		mouseToggle.add(btnMouseToggle);

		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Dialog", Font.BOLD, 10));
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "MONITOR 1" }));
		menuBar.add(comboBox);

		rdArea = new RDArea();
		rdArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(rdArea, BorderLayout.CENTER);

	}

}
