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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public final SettingsPanel settingsPanel = new SettingsPanel(this, false);
	public final HPanel hp = new HPanel(settingsPanel);

	private Dimension size = new Dimension(550, 260);

	public SettingsDialog() {
		setTitle("Settings");
		setSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
		setResizable(true);
		setLocationRelativeTo(null);
		setIconImages(UIUtil.getIconList());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(hp);

		// load values from databases
		settingsPanel.setValues(ViewerStore.Databases.local);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

			}
		});
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// save to databases
				settingsPanel.save(ViewerStore.Databases.local);
				dispose();

			}
		});

		Component[] buttons = { cancel, Box.createHorizontalGlue(), hp.initBtnUP(), Box.createHorizontalGlue(), save };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc("Change both local and server settings");

		hp.refreshHeight();
	}

}
