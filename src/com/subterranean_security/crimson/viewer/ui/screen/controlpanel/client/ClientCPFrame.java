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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanel.client;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class ClientCPFrame extends JFrame {

	private static final long	serialVersionUID	= 1L;
	private SettingsTab			settingsTab;
	private ControlsTab			controlsTab;

	public ClientCPFrame(HashMap<String, Boolean> tabs) {
		setResizable(true);
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);

		int tindex = 0;

		if (tabs.get("Settings")) {
			tabbedPane.add(settingsTab);
			tabbedPane.setTitleAt(tindex++, "Settings");
		}

		if (tabs.get("Controls")) {
			tabbedPane.add(controlsTab);
			tabbedPane.setTitleAt(tindex++, "Controls");
		}

	}
}
