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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.sv.keylogger.LogCallback;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Keylogger extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	private JPanel selection_panel;
	private JPanel logs_panel;

	public KeyLogPane content;

	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnView;

	private JPanel blank;

	private LogTree logTree;

	private JPanel content_panel;
	private JPanel loading;
	private JRadioButton rdbtnHierarchical;
	private JRadioButton rdbtnFlat;

	public Keylogger(ClientProfile profile, Console console) {

		setLayout(new BorderLayout(0, 0));

		logs_panel = new JPanel();
		logs_panel.setLayout(new BorderLayout(0, 0));
		logs_panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		add(logs_panel);

		selection_panel = new JPanel();
		selection_panel.setBorder(null);
		logs_panel.add(selection_panel, BorderLayout.WEST);
		selection_panel.setLayout(new BorderLayout(0, 0));

		logTree = new LogTree(this, profile);
		selection_panel.add(logTree);

		content_panel = new JPanel();
		logs_panel.add(content_panel);
		content_panel.setLayout(new CardLayout(0, 0));

		blank = new JPanel();
		content_panel.add(blank, "BLANK");

		content = new KeyLogPane();
		content_panel.add(content, "KEYLOG");

		loading = new JPanel();
		content_panel.add(loading, "LOADING");

		menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mnView = new JMenu("View");
		menuBar.add(mnView);

		rdbtnHierarchical = new JRadioButton("Hierarchical");
		rdbtnHierarchical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewerStore.Databases.local.storeObject("keylog.treeview", false);
				logTree.resetTree();
				logTree.setFormatters();
				logTree.refreshTree();
			}
		});
		mnView.add(rdbtnHierarchical);

		rdbtnFlat = new JRadioButton("Flat");
		rdbtnFlat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewerStore.Databases.local.storeObject("keylog.treeview", true);
				logTree.resetTree();
				logTree.setFormatters();
				logTree.refreshTree();
			}
		});
		mnView.add(rdbtnFlat);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnHierarchical);
		bg.add(rdbtnFlat);

		try {
			if (ViewerStore.Databases.local.getBoolean("keylog.treeview")) {
				rdbtnFlat.setSelected(true);
			} else {
				rdbtnHierarchical.setSelected(true);
			}

		} catch (Exception e1) {
			rdbtnHierarchical.setSelected(true);
		}

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
