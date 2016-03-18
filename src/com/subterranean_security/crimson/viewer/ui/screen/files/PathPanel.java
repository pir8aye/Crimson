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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PathPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	public PathPanel() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblIcon = new JLabel("icon");
		add(lblIcon, BorderLayout.WEST);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));

		textField = new JTextField();
		panel.add(textField, "name_5219408239145");
		textField.setColumns(10);

		JLabel lblPath = new JLabel("");
		panel.add(lblPath, "name_5406152265339");

	}

}
