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

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTree tree;
	private JPanel cards;

	public SettingsPanel(SettingsDialog settingsDialog, boolean b) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Settings") {
			{
				DefaultMutableTreeNode node_1;
				node_1 = new DefaultMutableTreeNode("Local");
				node_1.add(new DefaultMutableTreeNode("General"));
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
		cards.add("SettingsLocalGeneral", new LocalGeneral());

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
			System.out.println("Showing card: " + p);

			((CardLayout) cards.getLayout()).show(cards, p);

		}

	}

}
