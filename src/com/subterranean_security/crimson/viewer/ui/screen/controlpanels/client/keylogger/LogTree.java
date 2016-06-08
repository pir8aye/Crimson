package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class LogTree extends JPanel {

	private static final long serialVersionUID = 1L;

	private TreeOrganization organization = TreeOrganization.HIERARCHY;

	private SimpleDateFormat formatLeaf = null;
	private SimpleDateFormat formatParents = null;

	private void setFormatters() {
		if (organization == TreeOrganization.HIERARCHY) {
			formatLeaf = new SimpleDateFormat("EE dd");
			formatParents = new SimpleDateFormat("MMM yyyy");
		} else {
			formatLeaf = new SimpleDateFormat("MM/dd/yyyy");
		}
	}

	private JTree keylog_tree;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Logs");
	private DefaultTreeModel model = new DefaultTreeModel(root);

	private ClientProfile profile;
	private Keylogger parent;
	private JTextField textField;

	public LogTree(Keylogger p, ClientProfile cp) {
		parent = p;
		profile = cp;
		setFormatters();
		init();
		refreshTree();
	}

	public void init() {

		keylog_tree = new JTree(model);

		keylog_tree.setPreferredSize(new Dimension(160, 20));
		keylog_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		keylog_tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (refreshing) {
					return;
				}

				new Thread(new Runnable() {
					public void run() {

						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) keylog_tree
								.getLastSelectedPathComponent();

						if (selectedNode.isLeaf() && selectedNode.toString() != null) {
							// open the date in the content pane

							parent.loadKeylog();
							for (Date d : profile.getKeylog().pages.keyset()) {
								if (selectedNode.toString().equals(formatLeaf.format(d))) {
									try {
										parent.content.loadData(profile.getKeylog().pages.get(d));
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									break;
								}
							}
							parent.showKeylog();

						} else {
							parent.hideKeylog();
							return;
						}

					}
				}).start();

			}
		});
		keylog_tree.setCellRenderer(new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			private ImageIcon root = UIUtil.getIcon("icons16/general/server.png");
			private ImageIcon folder = UIUtil.getIcon("icons16/general/folder.png");
			private ImageIcon entry = UIUtil.getIcon("icons16/general/newspaper.png");

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				super.getTreeCellRendererComponent(tree, node.getUserObject(), selected, expanded, isLeaf, row,
						focused);

				if (node.isRoot()) {
					setIcon(root);
				} else if (node.isLeaf()) {
					setIcon(entry);
				} else {
					setIcon(folder);
				}
				return this;

			}
		});
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_7 = new JScrollPane();
		scrollPane_7.setViewportView(keylog_tree);
		add(scrollPane_7);

		textField = new JTextField();
		add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);

	}

	private boolean refreshing = false;

	public void refreshTree() {
		refreshing = true;
		for (Date d : profile.getKeylog().pages.keyset()) {

			String name = formatLeaf.format(d);

			// check if its already in the tree
			DefaultMutableTreeNode node = null;
			Enumeration e = root.breadthFirstEnumeration();
			boolean add = true;
			while (e.hasMoreElements()) {
				node = (DefaultMutableTreeNode) e.nextElement();
				if (name.equals((String) node.getUserObject())) {
					// node is already in the tree
					add = false;
					break;
				}
			}

			if (add) {
				// add node to the tree
				if (organization == TreeOrganization.FLAT) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
					model.insertNodeInto(childNode, root, root.getChildCount());
				} else {
					DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(formatParents.format(d));
					model.insertNodeInto(parentNode, root, root.getChildCount());
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
					model.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
				}

			}

		}
		TreePath old = keylog_tree.getSelectionPath();
		model.reload();
		keylog_tree.setSelectionPath(old);
		refreshing = false;
	}

	enum TreeOrganization {
		HIERARCHY, FLAT;
	}

}