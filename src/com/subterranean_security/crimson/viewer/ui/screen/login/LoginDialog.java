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
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.NormalMenu;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public final LoginPanel loginPanel;
	private HPanel hp;

	public LoginDialog(boolean localServer) {

		loginPanel = new LoginPanel(this, localServer);

		setTitle("Crimson - Login");
		setSize(UICommon.dim_login);
		setPreferredSize(UICommon.dim_login);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		hp = new HPanel(loginPanel);
		hp.init(initNormalMenu(), initHiddenMenu());
		hp.setHMenuHeight(72);
		getContentPane().add(hp);

	}

	private NormalMenu initNormalMenu() {

		NormalMenu nmenu = new NormalMenu();
		nmenu.setButtons(loginPanel.btn_cancel, Box.createHorizontalGlue(), hp.getUpBtn(), Box.createHorizontalGlue(),
				loginPanel.btn_login);
		return nmenu;
	}

	private HiddenMenu initHiddenMenu() {
		JButton login = new JButton("Skip Login");
		login.setToolTipText("Continue without logging into a server");
		login.setFont(new Font("Dialog", Font.BOLD, 9));
		login.setMargin(new Insets(0, 5, 0, 5));

		JButton help = new JButton("Show Help");
		help.setToolTipText("Show interface help");
		help.setFont(new Font("Dialog", Font.BOLD, 9));
		help.setMargin(new Insets(0, 5, 0, 5));

		JButton website = new JButton("Website");
		website.setToolTipText("Open the website");
		website.setFont(new Font("Dialog", Font.BOLD, 9));
		website.setMargin(new Insets(0, 5, 0, 5));

		HiddenMenu hmenu = new HiddenMenu(true, login, help, website);
		return hmenu;
	}

	@Override
	public void dispose() {
		if (loginPanel.result) {
			hp.hmenu.nowClosed();
			super.dispose();
		} else {
			System.exit(0);
		}

	}

}
