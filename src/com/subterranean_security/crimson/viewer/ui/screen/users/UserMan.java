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
package com.subterranean_security.crimson.viewer.ui.screen.users;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JFrame;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;

public class UserMan extends JFrame {

	private static final long serialVersionUID = 1L;
	public UsersPanel up = new UsersPanel();
	private HPanel hp = new HPanel(up);

	public UserMan() {
		setTitle("Users and Groups");
		setIconImages(UIUtil.getIconList());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setSize(UICommon.dim_min_users);
		setMinimumSize(UICommon.dim_min_users);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(hp, BorderLayout.CENTER);

		Component[] buttons = { Box.createHorizontalGlue(), hp.initBtnUP(), Box.createHorizontalGlue() };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc("Manages users on the server. At least one user must have super permissions");

		hp.setHMenuHeight(50);
	}

	@Override
	public void dispose() {
		super.dispose();
		UIStore.userMan = null;
	}

}
