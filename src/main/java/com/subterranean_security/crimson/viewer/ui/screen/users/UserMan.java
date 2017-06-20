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
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.NormalMenu;

public class UserMan extends JFrame {

	private static final long serialVersionUID = 1L;
	public UsersPanel up = new UsersPanel();
	private HPanel hp;

	public UserMan() {
		setTitle("Users and Groups");
		setIconImages(UIUtil.getAppIcons());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setSize(UICommon.dim_usermanager);
		setMinimumSize(UICommon.dim_usermanager);
		getContentPane().setLayout(new BorderLayout());

		hp = new HPanel(up);
		hp.init(initNormalMenu(), initHiddenMenu());
		hp.setHMenuHeight(72);

		getContentPane().add(hp, BorderLayout.CENTER);

	}

	private NormalMenu initNormalMenu() {
		NormalMenu nmenu = new NormalMenu();
		nmenu.setButtons(Box.createHorizontalGlue(), hp.getUpBtn(), Box.createHorizontalGlue());
		return nmenu;
	}

	private HiddenMenu initHiddenMenu() {
		JButton help = new JButton("Show Help");
		help.setFont(new Font("Dialog", Font.BOLD, 9));
		help.setMargin(new Insets(0, 5, 0, 5));

		HiddenMenu hmenu = new HiddenMenu(true, help);

		return hmenu;
	}

	@Override
	public void dispose() {
		super.dispose();
		UIStore.userMan = null;
	}

}
