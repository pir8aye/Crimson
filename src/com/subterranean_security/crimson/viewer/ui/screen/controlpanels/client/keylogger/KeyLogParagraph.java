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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

public class KeyLogParagraph extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextPane txtpnTest;

	private String title;

	public KeyLogParagraph(String title) {
		this.title = title;
		setBackground(Color.WHITE);
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		txtpnTest = new JTextPane();
		txtpnTest.setFont(new Font("Dialog", Font.PLAIN, 11));
		txtpnTest.setEditable(false);
		txtpnTest.setContentType("");
		add(txtpnTest, BorderLayout.NORTH);
	}

	public String getTitle() {
		return title;
	}

	public void append(String event) {
		txtpnTest.setText(txtpnTest.getText() + event);

	}

}
