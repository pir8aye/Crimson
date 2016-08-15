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

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.Tray;
import com.subterranean_security.crimson.viewer.ui.common.panels.dpanel.DPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.npanel.NPanel;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Main interface frame
	 */
	public static MainFrame main;

	public EPanel ep;
	public NPanel np;
	public DPanel dp;
	public MainMenu mm;
	public MainPanel panel;
	private JMenuBar sm;

	public MainFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setSize(UICommon.dim_frame_main);
		setPreferredSize(UICommon.dim_frame_main);
		setMinimumSize(UICommon.dim_frame_main);
		setIconImages(UIUtil.getIconList());

		String buildNo = null;
		try {
			buildNo = CUtil.Misc.getManifestAttr("Build-Number");
		} catch (IOException e) {

		}

		setTitle("Crimson [ALPHA PREVIEW" + (buildNo != null ? (" BUILD: " + buildNo) : "") + "]");
		initMenus();
		initContent();

	}

	private void initMenus() {

		mm = new MainMenu();
		getContentPane().add(mm, BorderLayout.NORTH);

		// account for special "system menu bar" on some platforms

	}

	private void initSMenu() {
		sm = new JMenuBar();
		setJMenuBar(sm);
	}

	private void initContent() {
		panel = new MainPanel();
		panel.setVisible(true);
		dp = new DPanel(panel);
		JPanel connector1 = new JPanel(new BorderLayout(0, 0));
		connector1.setVisible(true);
		connector1.add(dp, BorderLayout.CENTER);
		np = new NPanel(connector1);

		JPanel connector2 = new JPanel(new BorderLayout(0, 0));
		connector2.setVisible(true);
		connector2.add(np, BorderLayout.CENTER);
		ep = new EPanel(connector2);
		getContentPane().add(ep, BorderLayout.CENTER);
	}

	@Override
	public void dispose() {
		try {
			if (ViewerStore.Databases.local.getBoolean("close_on_tray")) {
				Tray.addTray();
			} else {
				System.exit(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void invokeAfterload() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame.main.panel.openConsole();
			}
		});

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			protected void done() {
				if (ViewerStore.Profiles.getLocalViewer().getLastLoginIp() != null) {
					MainFrame.main.np.addNote("info",
							"Last Login at " + ViewerStore.Profiles.getLocalViewer().getLastLoginTime().toString()
									+ " from " + ViewerStore.Profiles.getLocalViewer().getLastLoginIp());
				}
			};

		}.execute();
	}
}
