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
package com.subterranean_security.crimson.cv.ui.remote;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_DISP;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.cv.ui.remote.ep.Settings;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.RemoteParam;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

import aurelienribon.slidinglayout.SLSide;

public class RDPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public RemoteMaster stream;

	public RDArea rdArea;
	public boolean running;
	public int cvid;

	private boolean fullSettings;
	public Settings settings;

	public enum Type {
		VIEW_ONLY, INTERACT;
	}

	public RDPanel(Type type, int cvid, boolean fullSettings) {
		this.fullSettings = fullSettings;
		this.cvid = cvid;

		settings = new Settings(ViewerProfileStore.getClient(cvid).getGroupList(AttributeKey.Type.DISP), this,
				fullSettings);
		init();

	}

	private JProgressBar barScreenshot;
	private JProgressBar barToggle;
	private JProgressBar barKeyToggle;
	private JProgressBar barMouseToggle;

	private JButton btnToggle;

	public EPanel ep;

	private JButton btnMouseToggle;

	private JButton btnKeyToggle;

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(this.getBackground());
		add(menuBar, BorderLayout.NORTH);

		JPanel toggle = new JPanel();
		toggle.setMaximumSize(new Dimension(26, 30));
		toggle.setPreferredSize(new Dimension(26, 30));
		toggle.setLayout(new BorderLayout());
		menuBar.add(toggle);

		barToggle = new JProgressBar();
		barToggle.setPreferredSize(new Dimension(148, 4));
		toggle.add(barToggle, BorderLayout.SOUTH);

		btnToggle = new JButton(UIUtil.getIcon("icons16/general/map_go.png"));
		btnToggle.setFocusable(false);
		btnToggle.setToolTipText("Start");
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start();
			}
		});
		btnToggle.setMargin(new Insets(2, 2, 2, 2));
		toggle.add(btnToggle, BorderLayout.CENTER);

		JPanel screenshot = new JPanel();
		screenshot.setMaximumSize(new Dimension(26, 30));
		screenshot.setPreferredSize(new Dimension(26, 30));
		screenshot.setLayout(new BorderLayout());
		menuBar.add(screenshot);

		JButton btnScreenshot = new JButton(UIUtil.getIcon("icons16/general/camera.png"));
		btnScreenshot.setFocusable(false);
		btnScreenshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				barScreenshot.setIndeterminate(true);
				btnScreenshot.setEnabled(false);
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						// TODO attempt to get high quality version using a
						// traditional screenshot if the panel resolution is
						// less than the client resolution
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

		btnKeyToggle = new JButton(UIUtil.getIcon("icons16/general/keyboard.png"));
		btnKeyToggle.setEnabled(false);
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

		btnMouseToggle = new JButton(UIUtil.getIcon("icons16/general/mouse.png"));
		btnMouseToggle.setEnabled(false);
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

		menuBar.add(Box.createHorizontalGlue());

		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(26, 30));
		menuBar.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JButton btnSettingsToggle = new JButton(UIUtil.getIcon("icons16/general/cog.png"));
		btnSettingsToggle.setToolTipText("Settings");
		btnSettingsToggle.setFocusable(false);
		btnSettingsToggle.addActionListener(new ActionListener() {
			private boolean raised = false;

			public void actionPerformed(ActionEvent arg0) {
				if (raised) {
					raised = false;
					ep.drop();
				} else {
					raised = true;
					ep.raise(settings, fullSettings ? 100 : 60);
				}

			}
		});
		btnSettingsToggle.setPreferredSize(new Dimension(26, 30));
		panel.add(btnSettingsToggle, BorderLayout.NORTH);

		JPanel jp = new JPanel();
		jp.setLayout(new GridBagLayout());

		ep = new EPanel(jp, SLSide.TOP);
		add(ep, BorderLayout.CENTER);

		rdArea = new RDArea(this);
		rdArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		jp.add(rdArea);

	}

	public boolean isRunning() {
		return running;
	}

	public void start() {
		barToggle.setIndeterminate(true);
		btnToggle.setEnabled(false);
		btnKeyToggle.setEnabled(false);
		btnMouseToggle.setEnabled(false);
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				if (running) {
					stop();
				} else {
					running = true;
					rdArea.setMonitorSize(Integer.parseInt(settings.getDisplay().get(AK_DISP.DISP_WIDTH)),
							Integer.parseInt(settings.getDisplay().get(AK_DISP.DISP_HEIGHT)));

					btnToggle.setIcon(UIUtil.getIcon("icons16/general/map_delete.png"));
					btnToggle.setToolTipText("Stop");
					stream = new RemoteMaster(
							RemoteParam.newBuilder().setRmethod(settings.getMethod()).setMonitor(settings.getMonitor())
									.setColorType(settings.getColorType()).setCompType(settings.getCompType()).build(),
							cvid, rdArea);
					StreamStore.addStream(stream);
					stream.start();
					rdArea.setStream(stream);

					// update scale
					rdArea.getPreferredSize();
				}

				return null;
			}

			protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				barToggle.setIndeterminate(false);
				btnToggle.setEnabled(true);
				btnKeyToggle.setEnabled(running);
				btnMouseToggle.setEnabled(running);
			};
		}.execute();
	}

	public void stop() {
		running = false;
		btnToggle.setIcon(UIUtil.getIcon("icons16/general/map_go.png"));
		btnToggle.setToolTipText("Start");
		StreamStore.removeStreamBySID(stream.getStreamID());
	}
}
