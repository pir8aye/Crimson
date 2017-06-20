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
package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.NormalMenu;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.AuthPanel;

public class NetMan extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane = new JPanel();
	private HPanel hp;
	public ListenerPanel lp;

	public NetMan() {
		init();
	}

	private void init() {
		setIconImages(UIUtil.getAppIcons());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(UICommon.dim_networkmanager);
		setTitle("Network Manager");

		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		hp = new HPanel(contentPane);
		hp.init(initNormalMenu(), initHiddenMenu());
		hp.setHMenuHeight(72);
		setContentPane(hp);

		StatsPanel sp = new StatsPanel();
		sp.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(sp, BorderLayout.NORTH);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		lp = new ListenerPanel();
		tabbedPane.add(lp);
		tabbedPane.setTitleAt(0, "Listeners");

		AuthPanel ap = new AuthPanel();
		tabbedPane.add(ap);
		tabbedPane.setTitleAt(1, "Authentication");

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
		UIStore.netMan = null;
	}

}
