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
package com.subterranean_security.crimson.viewer.ui.screen.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.subterranean_security.crimson.core.profile.AbstractAttribute;
import com.subterranean_security.crimson.core.storage.LViewerDB;
import com.subterranean_security.crimson.viewer.ui.screen.main.HostList;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTree tree;
	private JPanel cards;
	private LocalGeneral lg = new LocalGeneral();
	private HostListHeaders hlh = new HostListHeaders();

	public SettingsPanel(SettingsDialog settingsDialog, boolean b) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 10));
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Settings") {
			{
				DefaultMutableTreeNode node_1;
				node_1 = new DefaultMutableTreeNode("Local");
				node_1.add(new DefaultMutableTreeNode("General"));
				node_1.add(new DefaultMutableTreeNode("Host List Headers"));
				add(node_1);
				node_1 = new DefaultMutableTreeNode("Server");
				node_1.add(new DefaultMutableTreeNode("General"));
				add(node_1);
			}
		}));
		tree.addTreeSelectionListener(new TreeListener());
		panel.add(tree, BorderLayout.NORTH);

		cards = new JPanel();
		add(cards, BorderLayout.CENTER);
		cards.setLayout(new CardLayout(0, 0));
		lg.neverShowLicense.setFont(new Font("Dialog", Font.BOLD, 10));
		lg.neverShowHelp.setFont(new Font("Dialog", Font.BOLD, 10));
		lg.runInTray.setFont(new Font("Dialog", Font.BOLD, 10));
		cards.add("SettingsLocalGeneral", lg);

		cards.add("SettingsLocalHost List Headers", hlh);

	}

	public void save(LViewerDB db) {
		db.storeObject("close_on_tray", lg.runInTray.isSelected());
		db.storeObject("show_eula", lg.neverShowLicense.isSelected());
		db.storeObject("show_helps", lg.neverShowHelp.isSelected());

		ArrayList<AbstractAttribute> h = new ArrayList<AbstractAttribute>();

		for (AbstractAttribute aa : hlh.boxes.keySet()) {
			if (hlh.boxes.get(aa).isSelected()) {
				h.add(aa);
			}
		}

		AbstractAttribute[] headers = new AbstractAttribute[h.size()];
		for (int i = 0; i < h.size(); i++) {
			headers[i] = h.get(i);
		}

		db.storeObject("hostlist.headers", headers);

		// refresh list headers
		MainFrame.main.panel.list.refreshHeaders();
	}

	public void setValues(LViewerDB db) {
		try {
			lg.runInTray.setSelected(db.getBoolean("close_on_tray"));
			lg.neverShowLicense.setSelected(db.getBoolean("show_eula"));
			lg.neverShowHelp.setSelected(db.getBoolean("show_helps"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AbstractAttribute[] headers = null;

		try {
			headers = (AbstractAttribute[]) db.getObject("hostlist.headers");
		} catch (Exception e) {
			headers = HostList.defaultHeaders;
		}

		for (AbstractAttribute h : headers) {
			hlh.boxes.get(h).setSelected(true);
		}

	}

	class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}

			String p = "";
			Object[] path = node.getUserObjectPath();
			for (int i = 0; i < path.length; i++) {
				p += (String) path[i];
			}

			((CardLayout) cards.getLayout()).show(cards, p);

		}

	}

}
