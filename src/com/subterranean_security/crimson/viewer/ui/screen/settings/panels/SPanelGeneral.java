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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SPanel;
import java.awt.Dimension;

public class SPanelGeneral extends JPanel implements SPanel {

	private static final long serialVersionUID = 1L;
	public JComboBox language;
	public JCheckBox runInTray;

	public SPanelGeneral() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 120));
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "General", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(null);
		
				JLabel lblLanguage = new JLabel("Language:");
				lblLanguage.setFont(new Font("Dialog", Font.BOLD, 10));
				lblLanguage.setBounds(12, 20, 73, 15);
				panel.add(lblLanguage);
				lblLanguage.setEnabled(false);
				
						language = new JComboBox();
						language.setFont(new Font("Dialog", Font.BOLD, 10));
						language.setBounds(241, 14, 100, 23);
						panel.add(language);
						language.setModel(new DefaultComboBoxModel(new String[] { "English" }));
						language.setEnabled(false);
						
								runInTray = new JCheckBox("Run in system tray");
								runInTray.setBounds(8, 39, 220, 20);
								panel.add(runInTray);
								runInTray.setFont(new Font("Dialog", Font.BOLD, 10));

	}

	@Override
	public void setValues(LViewerDB db) {
		try {
			runInTray.setSelected(db.getBoolean("close_on_tray"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void saveValues(LViewerDB db) {
		db.storeObject("close_on_tray", runInTray.isSelected());

	}

}
