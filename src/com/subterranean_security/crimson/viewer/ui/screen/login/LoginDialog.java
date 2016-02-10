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
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JDialog;

import com.subterranean_security.crimson.core.proto.net.Login.ServerInfoDelta_EV;
import com.subterranean_security.crimson.viewer.ui.panel.HPanel;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public final LoginPanel loginPanel = new LoginPanel(this);
	public final HPanel hp = new HPanel(loginPanel);

	private Dimension size = new Dimension(405, 320);

	public static ServerInfoDelta_EV initial = null;

	public LoginDialog(boolean localServer) {

		loginPanel.addRecents(localServer);

		setTitle("Crimson - Login");
		setSize(size);
		setPreferredSize(size);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(hp);

		Component[] buttons = { loginPanel.btn_cancel, Box.createHorizontalGlue(), hp.initBtnUP(),
				Box.createHorizontalGlue(), loginPanel.btn_login };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc(
				"Server addresses can be either DNS names or IP addresses.  Valid port numbers are between 1-25565. If the server is installed locally, select \"Local Server\" from the server selection dropdown.  For Crimson Cloud servers, use the information provided by Subterranean Security.");

		hp.refreshHeight();
	}

}
