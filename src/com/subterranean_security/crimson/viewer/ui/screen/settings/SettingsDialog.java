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
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.subterranean_security.crimson.universal.stores.Database;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.NormalMenu;
import com.subterranean_security.crimson.viewer.ui.screen.settings.panels.SPanelGeneral;
import com.subterranean_security.crimson.viewer.ui.screen.settings.panels.SPanelGraph;
import com.subterranean_security.crimson.viewer.ui.screen.settings.panels.SPanelHostList;
import com.subterranean_security.crimson.viewer.ui.screen.settings.panels.SPanelPolicy;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTree tree = new JTree();
	private JPanel cards = new JPanel();

	private HashMap<Panels, SPanel> panels = new HashMap<Panels, SPanel>();

	private JPanel mainPanel = new JPanel(new BorderLayout());

	private HPanel hp;

	public SettingsDialog() {
		init();

		// load values from database
		for (SPanel tab : panels.values()) {
			tab.setValues(Database.getFacility());
		}

	}

	private NormalMenu initNormalMenu() {
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// save to databases
				for (SPanel tab : panels.values()) {
					tab.saveValues(Database.getFacility());
				}

				dispose();

			}
		});

		NormalMenu nmenu = new NormalMenu();
		nmenu.setButtons(cancel, Box.createHorizontalGlue(), hp.getUpBtn(), Box.createHorizontalGlue(), save);
		return nmenu;
	}

	private HiddenMenu initHiddenMenu() {
		JButton help = new JButton("Show Help");
		help.setFont(new Font("Dialog", Font.BOLD, 9));
		help.setMargin(new Insets(0, 5, 0, 5));

		HiddenMenu hmenu = new HiddenMenu(true, help);
		return hmenu;
	}

	private void init() {
		setTitle("Settings");
		setSize(UICommon.dim_settings);
		setMinimumSize(UICommon.dim_settings);
		setPreferredSize(UICommon.dim_settings);
		setLocationRelativeTo(null);
		setIconImages(UIUtil.getIconList());
		setResizable(true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		hp = new HPanel(mainPanel);
		hp.init(initNormalMenu(), initHiddenMenu());
		hp.setHMenuHeight(72);
		getContentPane().add(hp);

		cards.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		cards.setLayout(new CardLayout(0, 0));
		mainPanel.add(cards, BorderLayout.CENTER);

		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Settings") {

			private static final long serialVersionUID = 1L;

			{
				for (Panels p : Panels.values()) {
					this.add(new DefaultMutableTreeNode(p));
				}
			}
		}));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			private ImageIcon root = UIUtil.getIcon("icons16/general/setting_tools.png");

			private ImageIcon general = UIUtil.getIcon("icons16/general/mixer.png");
			private ImageIcon host_list = UIUtil.getIcon("icons16/general/view_list.png");
			private ImageIcon host_graph = UIUtil.getIcon("icons16/general/view_graph.png");
			private ImageIcon policy = UIUtil.getIcon("icons16/general/clipboard_invoice.png");

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof Panels) {

					Panels type = (Panels) userObject;

					super.getTreeCellRendererComponent(tree, type.toString(), selected, expanded, isLeaf, row, focused);
					switch (type) {
					case GENERAL:
						setIcon(general);
						break;
					case HOST_LIST:
						setIcon(host_list);
						break;
					case HOST_GRAPH:
						setIcon(host_graph);
						break;
					case POLICY:
						setIcon(policy);
						break;
					default:
						break;

					}

				} else {
					super.getTreeCellRendererComponent(tree, userObject, selected, expanded, isLeaf, row, focused);
					setIcon(root);
				}
				return this;

			}
		});
		tree.addTreeSelectionListener(new TreeListener());

		mainPanel.add(tree, BorderLayout.WEST);

		for (Panels p : Panels.values()) {
			switch (p) {
			case GENERAL:
				SPanelGeneral general = new SPanelGeneral();
				cards.add(p.toString(), general);
				panels.put(p, general);
				break;
			case HOST_LIST:
				SPanelHostList list = new SPanelHostList();
				cards.add(p.toString(), list);
				panels.put(p, list);
				break;
			case HOST_GRAPH:
				SPanelGraph graph = new SPanelGraph();
				cards.add(p.toString(), graph);
				panels.put(p, graph);
				break;
			case POLICY:
				SPanelPolicy policy = new SPanelPolicy();
				cards.add(p.toString(), policy);
				panels.put(p, policy);
				break;
			default:
				break;

			}
		}

	}

	class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null || node.getUserObject() instanceof String) {
				return;
			}

			((CardLayout) cards.getLayout()).show(cards, node.getUserObject().toString());

		}

	}

	public enum Panels {
		GENERAL, HOST_LIST, HOST_GRAPH, POLICY;

		@Override
		public String toString() {
			switch (this) {
			case GENERAL:
				return "General";
			case HOST_LIST:
				return "Host List";
			case HOST_GRAPH:
				return "Host Graph";
			case POLICY:
				return "Policy";
			default:
				return null;

			}
		}
	}

}
