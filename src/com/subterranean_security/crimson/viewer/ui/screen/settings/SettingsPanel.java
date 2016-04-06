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
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.subterranean_security.crimson.core.storage.Headers;
import com.subterranean_security.crimson.core.storage.LViewerDB;
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
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Settings") {
			{
				DefaultMutableTreeNode node_1;
				DefaultMutableTreeNode node_2;
				DefaultMutableTreeNode node_3;
				node_1 = new DefaultMutableTreeNode("Local");
				node_1.add(new DefaultMutableTreeNode("General"));
				node_2 = new DefaultMutableTreeNode("Views");
				node_3 = new DefaultMutableTreeNode("Host List");
				node_3.add(new DefaultMutableTreeNode("Headers"));
				node_2.add(node_3);
				node_2.add(new DefaultMutableTreeNode("Host Graph"));
				node_1.add(node_2);
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
		cards.add("SettingsLocalGeneral", lg);
		cards.add("SettingsLocalViewsHost ListHeaders", hlh);

	}

	public void save(LViewerDB db) {
		db.storeObject("close_on_tray", lg.runInTray.isSelected());
		db.storeObject("show_eula", lg.neverShowLicense.isSelected());
		db.storeObject("show_helps", lg.neverShowHelp.isSelected());

		ArrayList<Headers> h = new ArrayList<Headers>();
		if (hlh.chckbxActiveWindow.isSelected())
			h.add(Headers.ACTIVE_WINDOW);
		if (hlh.chckbxCountry.isSelected())
			h.add(Headers.COUNTRY);
		if (hlh.chckbxCpuModel.isSelected())
			h.add(Headers.CPU_MODEL);
		if (hlh.chckbxCpuTemp.isSelected())
			h.add(Headers.CPU_TEMP);
		if (hlh.chckbxCpuUsage.isSelected())
			h.add(Headers.CPU_USAGE);
		if (hlh.chckbxCrimsonVersion.isSelected())
			h.add(Headers.CRIMSON_VERSION);
		if (hlh.chckbxExternalIp.isSelected())
			h.add(Headers.EXTERNAL_IP);
		if (hlh.chckbxHostname.isSelected())
			h.add(Headers.HOSTNAME);
		if (hlh.chckbxInternalIp.isSelected())
			h.add(Headers.INTERNAL_IP);
		if (hlh.chckbxJavaVersion.isSelected())
			h.add(Headers.JAVA_VERSION);
		if (hlh.chckbxLanguage.isSelected())
			h.add(Headers.LANGUAGE);
		if (hlh.chckbxMessagePing.isSelected())
			h.add(Headers.MESSAGE_PING);
		if (hlh.chckbxMonitorCount.isSelected())
			h.add(Headers.MONITOR_COUNT);
		if (hlh.chckbxOSArch.isSelected())
			h.add(Headers.OS_ARCH);
		if (hlh.chckbxOSFamily.isSelected())
			h.add(Headers.OS_FAMILY);
		if (hlh.chckbxRamCapacity.isSelected())
			h.add(Headers.RAM_CAPACITY);
		if (hlh.chckbxRamUsage.isSelected())
			h.add(Headers.RAM_USAGE);
		if (hlh.chckbxScreenPreview.isSelected())
			h.add(Headers.SCREEN_PREVIEW);
		if (hlh.chckbxTimezone.isSelected())
			h.add(Headers.TIMEZONE);
		if (hlh.chckbxUsername.isSelected())
			h.add(Headers.USERNAME);
		if (hlh.chckbxUserStatus.isSelected())
			h.add(Headers.USER_STATUS);
		if (hlh.chckbxVirtualization.isSelected())
			h.add(Headers.VIRTUALIZATION);

		Headers[] headers = new Headers[h.size()];
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

		try {
			Headers[] headers = (Headers[]) db.getObject("hostlist.headers");
			for (Headers h : headers) {
				switch (h) {
				case ACTIVE_WINDOW:
					hlh.chckbxActiveWindow.setSelected(true);
					break;
				case COUNTRY:
					hlh.chckbxCountry.setSelected(true);
					break;
				case CPU_MODEL:
					break;
				case CPU_TEMP:
					hlh.chckbxCpuTemp.setSelected(true);
					break;
				case CPU_USAGE:
					hlh.chckbxCpuUsage.setSelected(true);
					break;
				case CRIMSON_VERSION:
					hlh.chckbxCrimsonVersion.setSelected(true);
					break;
				case EXTERNAL_IP:
					hlh.chckbxExternalIp.setSelected(true);
					break;
				case HOSTNAME:
					hlh.chckbxHostname.setSelected(true);
					break;
				case INTERNAL_IP:
					hlh.chckbxInternalIp.setSelected(true);
					break;
				case JAVA_VERSION:
					hlh.chckbxJavaVersion.setSelected(true);
					break;
				case LANGUAGE:
					hlh.chckbxLanguage.setSelected(true);
					break;
				case MESSAGE_PING:
					hlh.chckbxMessagePing.setSelected(true);
					break;
				case MONITOR_COUNT:
					hlh.chckbxMonitorCount.setSelected(true);
					break;
				case OS_ARCH:
					hlh.chckbxOSArch.setSelected(true);
					break;
				case OS_FAMILY:
					hlh.chckbxOSFamily.setSelected(true);
					break;
				case RAM_CAPACITY:
					hlh.chckbxRamCapacity.setSelected(true);
					break;
				case RAM_USAGE:
					hlh.chckbxRamUsage.setSelected(true);
					break;
				case SCREEN_PREVIEW:
					hlh.chckbxScreenPreview.setSelected(true);
					break;
				case TIMEZONE:
					hlh.chckbxTimezone.setSelected(true);
					break;
				case USERNAME:
					hlh.chckbxUsername.setSelected(true);
					break;
				case USER_STATUS:
					hlh.chckbxUserStatus.setSelected(true);
					break;
				case VIRTUALIZATION:
					hlh.chckbxVirtualization.setSelected(true);
					break;
				default:
					break;

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
