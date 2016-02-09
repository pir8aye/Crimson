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
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.component.Tray;
import com.subterranean_security.crimson.viewer.ui.panel.DPanel;
import com.subterranean_security.crimson.viewer.ui.panel.NPanel;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Main interface frame
	 */
	public static MainFrame main;

	public NPanel np;
	public DPanel dp;
	public MainMenu mm;
	public MainPanel panel;
	private JMenuBar sm;

	public static final Dimension dim_frame_main = new Dimension(620, 310);

	public MainFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setSize(dim_frame_main);
		setPreferredSize(dim_frame_main);
		setMinimumSize(dim_frame_main);
		setIconImages(UUtil.getIconList());

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
		JPanel temp = new JPanel(new BorderLayout(0, 0));
		temp.setVisible(true);
		temp.add(dp, BorderLayout.CENTER);
		np = new NPanel(temp);
		getContentPane().add(np, BorderLayout.CENTER);
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
}
