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
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.util.EH;
import com.subterranean_security.crimson.sv.keylogger.LogCallback;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger.ep.Settings;

import aurelienribon.slidinglayout.SLSide;

public class Keylogger extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	private JPanel selection_panel;
	private JPanel logs_panel;

	public KeyLogPane content;

	private JMenuBar menuBar;

	private JPanel blank;

	private LogTree logTree;

	private EPanel ep;
	private JPanel content_panel;
	private JPanel loading;
	private JButton btnNewButton;

	private JButton btnNewButton_1;
	private JButton btnNewButton_2;

	private boolean flatView;

	public Keylogger(ClientProfile profile, Console console) {
		init(profile, console);
		try {
			flatView = ViewerStore.Databases.local.getBoolean("keylog.treeview");
		} catch (Exception e) {
			EH.handle(e);
		}
	}

	public void init(ClientProfile profile, Console console) {

		setLayout(new BorderLayout(0, 0));

		logs_panel = new JPanel();
		logs_panel.setLayout(new BorderLayout(0, 0));
		add(logs_panel);

		selection_panel = new JPanel();
		selection_panel.setBorder(null);
		logs_panel.add(selection_panel, BorderLayout.WEST);
		selection_panel.setLayout(new BorderLayout(0, 0));

		logTree = new LogTree(this, profile);
		selection_panel.add(logTree);

		content_panel = new JPanel();
		ep = new EPanel(content_panel, SLSide.TOP);
		logs_panel.add(ep);
		content_panel.setLayout(new CardLayout(0, 0));

		blank = new JPanel();
		content_panel.add(blank, "BLANK");

		content = new KeyLogPane();
		content_panel.add(content, "KEYLOG");

		loading = new JPanel();
		content_panel.add(loading, "LOADING");

		menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		btnNewButton_2 = new JButton(UIUtil.getIcon("icons16/general/tree_hierarchy.png"));
		btnNewButton_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				logTree.refreshing = true;
				flatView = !flatView;
				ViewerStore.Databases.local.storeObject("keylog.treeview", flatView);
				if (flatView) {
					btnNewButton_2.setIcon(UIUtil.getIcon("icons16/general/tree_list.png"));
					btnNewButton_2.setToolTipText("Switch to flat view");
				} else {
					btnNewButton_2.setIcon(UIUtil.getIcon("icons16/general/tree_hierarchy.png"));
					btnNewButton_2.setToolTipText("Switch to hierarchical view");
				}

				logTree.resetTree();
				logTree.setFormatters();
				logTree.refreshTree();

			}
		});
		btnNewButton_2.setToolTipText(flatView ? "Switch to hierarchical view" : "Switch to flat view");
		btnNewButton_2.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnNewButton_2);

		menuBar.add(Box.createHorizontalGlue());

		btnNewButton_1 = new JButton(UIUtil.getIcon("icons16/general/statistics.png"));
		btnNewButton_1.setToolTipText("Keylogger Statistics");
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnNewButton_1);

		btnNewButton = new JButton(UIUtil.getIcon("icons16/general/cog.png"));
		btnNewButton.setToolTipText("Keylogger Settings");
		btnNewButton.setFocusable(false);
		btnNewButton.setMargin(new Insets(2, 4, 2, 4));
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ep.isOpen() && ep.getEP() != null && ep.getEP() instanceof Settings) {
					ep.drop();
				} else {
					ep.raise(new Settings(ep, profile.getCvid(), profile.getKeyloggerState(), profile.getFlushMethod(),
							profile.getFlushValue()), 160);
				}

			}
		});
		menuBar.add(btnNewButton);

		profile.getKeylog().addCallback(new LogCallback(this));

	}

	public void addKEvent(EV_KEvent k) {
		logTree.refreshTree();
		content.updateContent(k);
	}

	public void showKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "KEYLOG");
	}

	public void loadKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "LOADING");
	}

	public void hideKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "BLANK");
	}

}
