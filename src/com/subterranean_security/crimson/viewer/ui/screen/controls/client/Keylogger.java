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
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.panel.Console;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class Keylogger extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	private ClientProfile profile;

	private JPanel selection_panel;
	private JPanel logs_panel;
	private JTree keylog_tree;
	private DefaultMutableTreeNode tree_root;
	private KeyLogPane content;
	private DefaultTreeModel treeModel;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnView;

	private SimpleDateFormat dateViewFormat = new SimpleDateFormat("MM dd yyyy");

	public Keylogger(ClientProfile profile, Console console) {
		this.profile = profile;

		// create the root node
		tree_root = new DefaultMutableTreeNode("Logs");

		for (Date d : profile.getKeylog().pages.keyset()) {

			// populate jtree
			tree_root.add(new DefaultMutableTreeNode(dateViewFormat.format(d)));

		}
		treeModel = new DefaultTreeModel(tree_root);
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

		JScrollPane scrollPane_7 = new JScrollPane();
		selection_panel.add(scrollPane_7);
		keylog_tree = new JTree(treeModel);
		keylog_tree.setPreferredSize(new Dimension(160, 20));
		keylog_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		keylog_tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) keylog_tree
						.getLastSelectedPathComponent();
				// open the date in the content pane
				String s = null;
				try {
					s = selectedNode.toString();
				} catch (NullPointerException e1) {
					return;
				}
				Date d = null;
				try {
					if (s.equals("Logs")) {
						// this is the root
						content.clear();
						return;
					}
					d = dateViewFormat.parse(s);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// clear the content box
				content.clear();

				try {
					content.loadData(profile.getKeylog().pages.get(d));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		scrollPane_7.setViewportView(keylog_tree);

		JPanel content_panel = new JPanel();
		logs_panel.add(content_panel);
		content_panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_8 = new JScrollPane();
		content_panel.add(scrollPane_8);

		content = new KeyLogPane();
		content.setEditable(false);
		scrollPane_8.setViewportView(content);

		menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mnView = new JMenu("View");
		menuBar.add(mnView);

	}

	public void updateKeylogger() {
		// Logger.debug("Running Keylogger update. KLog has: " +
		// c.profile.log.keydays.size() + " days");
		SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy");
		for (Date d : profile.getKeylog().pages.keyset()) {
			// add it to the jtree

			String name = df.format(d);

			// Logger.add("Testing if: " + name + " is already in the tree");
			// check if its already in the tree
			DefaultMutableTreeNode node = null;
			Enumeration e = tree_root.breadthFirstEnumeration();
			boolean add = true;
			while (e.hasMoreElements()) {
				node = (DefaultMutableTreeNode) e.nextElement();
				if (name.equals(node.getUserObject().toString())) {
					// node is already in the tree
					// Logger.add("Node already in tree");
					add = false;
					break;
				}
			}

			if (add) {
				// add node to the tree
				// Logger.add("Adding node to tree");
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
				treeModel.insertNodeInto(childNode, tree_root, tree_root.getChildCount());
			}

		}
		treeModel.reload();

		String s = null;
		try {
			// update contentpane
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) keylog_tree.getLastSelectedPathComponent();
			// open the date in the content pane

			s = selectedNode.toString();
		} catch (NullPointerException e) {
			return;
		}

		Date d = null;
		try {
			if (s.equals("Logs")) {
				// this is the root; return
				return;
			}
			d = df.parse(s);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// clear the content box
		content.setText("");

		// content.loadData(keys);
	}

}
