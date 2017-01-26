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
package com.subterranean_security.crimson.viewer.ui.screen.settings.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.storage.StorageFacility;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SPanel;

public class SPanelPolicy extends JPanel implements SPanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox chckbxShowHelpMenus;
	private JCheckBox chckbxAlwaysShowLicense;

	public SPanelPolicy() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 75));
		panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(null);

		chckbxShowHelpMenus = new JCheckBox("Show help menus");
		chckbxShowHelpMenus.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxShowHelpMenus.setBounds(8, 20, 248, 23);
		panel.add(chckbxShowHelpMenus);

		chckbxAlwaysShowLicense = new JCheckBox("Always show license on start-up");
		chckbxAlwaysShowLicense.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxAlwaysShowLicense.setBounds(8, 40, 248, 23);
		panel.add(chckbxAlwaysShowLicense);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Notification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_2, BorderLayout.NORTH);

	}

	@Override
	public void setValues(StorageFacility db) {
		try {
			chckbxAlwaysShowLicense.setSelected(db.getBoolean("show_eula"));
			chckbxShowHelpMenus.setSelected(db.getBoolean("show_helps"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void saveValues(StorageFacility db) {
		db.store("show_eula", chckbxAlwaysShowLicense.isSelected());
		db.store("show_helps", chckbxShowHelpMenus.isSelected());

	}
}
