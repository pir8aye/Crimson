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
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.AuthPanel;

public class NetMan extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane = new JPanel();
	private HPanel hp = new HPanel(contentPane);
	public ListenerPanel lp;

	public NetMan() {
		setIconImages(UIUtil.getIconList());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(UICommon.dim_networkmanager);
		setTitle("Network Manager");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(hp);
		contentPane.setLayout(new BorderLayout());

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

		Component[] buttons = { Box.createHorizontalGlue(), hp.initBtnUP(), Box.createHorizontalGlue() };
		hp.nmenu.setButtons(buttons);

		hp.hmenu.setDesc("Manages listeners and authentication on the server.  At least one listener must be defined.");

		hp.setHMenuHeight(50);
	}

	@Override
	public void dispose() {
		super.dispose();
		UIStore.netMan = null;
	}

}
