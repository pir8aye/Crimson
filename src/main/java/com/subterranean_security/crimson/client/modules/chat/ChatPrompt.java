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
package com.subterranean_security.crimson.client.modules.chat;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JWindow;

public class ChatPrompt extends JWindow {

	private static final long serialVersionUID = 1L;

	public ChatPrompt() {
		getContentPane().setLayout(null);

		JButton btnAccept = new JButton("Accept");
		btnAccept.setMargin(new Insets(2, 4, 2, 4));
		btnAccept.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAccept.setBounds(205, 143, 60, 20);
		getContentPane().add(btnAccept);

		JButton btnDecline = new JButton("Decline");
		btnDecline.setMargin(new Insets(2, 4, 2, 4));
		btnDecline.setFont(new Font("Dialog", Font.BOLD, 10));
		btnDecline.setBounds(76, 143, 60, 20);
		getContentPane().add(btnDecline);
	}
}
