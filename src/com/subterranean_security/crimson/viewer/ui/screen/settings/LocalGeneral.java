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

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import javax.swing.JComboBox;

public class LocalGeneral extends JPanel {

	private static final long serialVersionUID = 1L;

	public LocalGeneral() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Language", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JComboBox comboBox = new JComboBox();
		panel.add(comboBox, BorderLayout.CENTER);

	}

}
