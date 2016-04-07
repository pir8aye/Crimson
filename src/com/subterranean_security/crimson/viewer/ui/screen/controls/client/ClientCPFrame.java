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
package com.subterranean_security.crimson.viewer.ui.screen.controls.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class ClientCPFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public ClientProfile profile;

	private JTree tree = new JTree();
	private JPanel cards = new JPanel();

	private HashMap<Panels, CPPanel> panels = new HashMap<Panels, CPPanel>();

	public ClientCPFrame(ClientProfile cp) {
		profile = cp;

		setTitle("Control Panel: " + profile.getHostname());
		setIconImages(UUtil.getIconList());
		setResizable(true);
		setMinimumSize(UICommon.min_ccp);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(Box.createHorizontalStrut(50), BorderLayout.SOUTH);
		getContentPane().add(panel, BorderLayout.WEST);
		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(profile.getHostname()) {

			private static final long serialVersionUID = 1L;

			{
				// TODO add based on platform
				this.add(new DefaultMutableTreeNode("Clipboard"));
				this.add(new DefaultMutableTreeNode("Website Filter"));
			}
		}));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			// TODO platform specific
			private ImageIcon host = UUtil.getIcon("icons16/general/viewer.png");

			private ImageIcon clipboard = UUtil.getIcon("icons16/general/paste_plain.png");
			private ImageIcon webfilter = UUtil.getIcon("icons16/general/www_page.png");

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				switch ((Panels) value) {
				case CLIPBOARD:
					setIcon(clipboard);
					break;
				case WEBFILTER:
					setIcon(webfilter);
					break;
				default:
					setIcon(host);
					break;

				}

				return super.getTreeCellRendererComponent(tree, value.toString(), selected, expanded, isLeaf, row,
						focused);
			}
		});
		tree.addTreeSelectionListener(new TreeListener());

		panel.add(tree);
		cards.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		getContentPane().add(cards, BorderLayout.CENTER);
		cards.setLayout(new CardLayout(0, 0));

	}

	class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}

			((CardLayout) cards.getLayout()).show(cards, node.getUserObject().toString());

		}

	}

	public enum Panels {
		CLIPBOARD, WEBFILTER;

		@Override
		public String toString() {
			switch (this) {
			case CLIPBOARD:
				return "Clipboard";
			case WEBFILTER:
				return "Web Filter";
			default:
				return null;

			}
		}
	}

}
