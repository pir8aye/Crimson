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
package com.subterranean_security.crimson.viewer.ui.screen.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;

import com.subterranean_security.crimson.viewer.ui.panel.HPanel;

public class SettingsDialog extends JDialog {

	public final SettingsPanel settingsPanel = new SettingsPanel(this, false);
	public final HPanel hp = new HPanel(settingsPanel);

	private Dimension size = new Dimension(405, 320);

	public SettingsDialog() {
		setTitle("Settings");
		setSize(size);
		setPreferredSize(size);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(hp);

		// Component[] buttons = { loginPanel.btn_cancel,
		// Box.createHorizontalGlue(), hp.initBtnUP(),
		// Box.createHorizontalGlue(), loginPanel.btn_login };
		// hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc("");

		hp.refreshHeight();
	}

}
