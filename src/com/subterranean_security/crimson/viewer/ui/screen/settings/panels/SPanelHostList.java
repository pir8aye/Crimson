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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.screen.settings.ListHeaderPopup;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SPanel;
import java.awt.Font;
import java.awt.Insets;

public class SPanelHostList extends JPanel implements SPanel {

	private static final long serialVersionUID = 1L;

	public SPanelHostList() {
		init();
	}

	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel static_panel = new JPanel();
		static_panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(static_panel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "List Headers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		
				JButton btnTest = new JButton("Modify Headers");
				btnTest.setMargin(new Insets(2, 4, 2, 4));
				btnTest.setFont(new Font("Dialog", Font.BOLD, 10));
				panel.add(btnTest);
				btnTest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ListHeaderPopup popup = new ListHeaderPopup(ViewerStore.Databases.local);
						popup.show(btnTest, 0, 0);
					}
				});

	}

	@Override
	public void setValues(LViewerDB db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveValues(LViewerDB db) {
		// TODO Auto-generated method stub

	}

}
