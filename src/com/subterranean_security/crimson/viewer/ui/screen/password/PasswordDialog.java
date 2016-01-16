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
package com.subterranean_security.crimson.viewer.ui.screen.password;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JDialog;

import com.subterranean_security.crimson.viewer.ui.panel.HPanel;

public class PasswordDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private PasswordPanel pp = new PasswordPanel(true, this);
	private HPanel hp = new HPanel(pp);

	public PasswordDialog(boolean change) {

		setTitle(change ? "Change password" : "Create password");
		setResizable(false);
		setBounds(100, 100, 310, 323);
		getContentPane().setLayout(new BorderLayout(0, 0));

		Component[] buttons = { pp.btn_cancel, Box.createHorizontalGlue(), hp.initBtnUP(), Box.createHorizontalGlue(),
				pp.btn_ok };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.addWebsiteButton();
		hp.hmenu.setDesc(
				"This dialog allows the user password to be changed.  Entropy (randomness) can be collected by moving the mouse randomly in the box.  This improves the strength of the cryptography.");
		getContentPane().add(hp);

	}

}
