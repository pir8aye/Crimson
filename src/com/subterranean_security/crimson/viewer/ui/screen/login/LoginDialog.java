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
package com.subterranean_security.crimson.viewer.ui.screen.login;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JDialog;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public final LoginPanel loginPanel = new LoginPanel(this);
	public final HPanel hp = new HPanel(loginPanel);

	public LoginDialog(boolean localServer) {

		loginPanel.addRecents(localServer);

		setTitle("Crimson - Login");
		setSize(UICommon.dim_login);
		setPreferredSize(UICommon.dim_login);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(hp);

		Component[] buttons = { loginPanel.btn_cancel, Box.createHorizontalGlue(), hp.initBtnUP(),
				Box.createHorizontalGlue(), loginPanel.btn_login };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc(
				"Enter the IP address or DNS name of a Crimson server. If the server is installed locally, select \"Local Server\" from the server selection dropdown.");

		hp.hmenu.addStats();
		hp.setHMenuHeight(80);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (!loginPanel.result) {
			System.exit(0);
		}

	}

}
