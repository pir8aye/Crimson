/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.Tray;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class InterfaceDetail extends JPanel {

	private static final long serialVersionUID = 1L;

	private JToggleButton tglbtnList;
	private JToggleButton tglbtnGraph;

	public InterfaceDetail(MPanel mp) {
		init();
		initValues();
	}

	private void init() {
		setLayout(null);
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 39, 104, 85);
		add(panel);
		panel.setLayout(null);

		tglbtnList = new JToggleButton("Host List");
		tglbtnList.setIcon(UIUtil.getIcon("icons16/general/view_list.png"));
		tglbtnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.main.panel.switchToList();
			}
		});

		tglbtnList.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnList.setMargin(new Insets(2, 2, 2, 2));
		tglbtnList.setBounds(8, 8, 88, 20);
		panel.add(tglbtnList);

		tglbtnGraph = new JToggleButton("Host Graph");
		tglbtnGraph.setIcon(UIUtil.getIcon("icons16/general/view_graph.png"));
		tglbtnGraph.setMargin(new Insets(2, 2, 2, 2));
		tglbtnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.main.panel.switchToGraph();
			}
		});

		tglbtnGraph.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnGraph.setBounds(8, 32, 88, 20);
		panel.add(tglbtnGraph);

		ButtonGroup bg = new ButtonGroup();
		bg.add(tglbtnList);
		bg.add(tglbtnGraph);

		JButton btnViewHistory = new JButton("History");
		btnViewHistory.setIcon(UIUtil.getIcon("icons16/general/view_history.png"));
		btnViewHistory.setEnabled(false);
		btnViewHistory.setMargin(new Insets(2, 4, 2, 4));
		btnViewHistory.setFont(new Font("Dialog", Font.BOLD, 10));
		btnViewHistory.setBounds(8, 56, 88, 20);
		panel.add(btnViewHistory);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 8, 104, 21);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblInterface = new JLabel("Interface");
		lblInterface.setHorizontalAlignment(SwingConstants.CENTER);
		lblInterface.setIcon(UIUtil.getIcon("icons16/general/application.png"));
		panel_1.add(lblInterface);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.setBounds(10, 134, 104, 60);
		add(panel_2);
		panel_2.setLayout(null);

		JButton btnViewConsole = new JButton("Console");
		btnViewConsole.setMargin(new Insets(2, 2, 2, 2));
		btnViewConsole.setIcon(UIUtil.getIcon("icons16/general/view_console.png"));
		btnViewConsole.setSelected(true);
		btnViewConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!MainFrame.main.ep.isMoving()) {
					btnViewConsole.setSelected(!btnViewConsole.isSelected());
					if (btnViewConsole.isSelected()) {
						MainFrame.main.panel.openConsole();
					} else {
						MainFrame.main.panel.closeConsole();
					}

				}

			}
		});
		btnViewConsole.setFont(new Font("Dialog", Font.BOLD, 10));
		btnViewConsole.setBounds(8, 8, 88, 20);
		panel_2.add(btnViewConsole);

		JButton btnSidebar = new JButton("Sidebar");
		btnSidebar.setIcon(UIUtil.getIcon("icons16/general/sidebar.png"));
		btnSidebar.setMargin(new Insets(2, 2, 2, 2));
		btnSidebar.setFont(new Font("Dialog", Font.BOLD, 10));
		btnSidebar.setBounds(8, 32, 88, 20);
		panel_2.add(btnSidebar);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_3.setBounds(10, 204, 104, 60);
		add(panel_3);
		panel_3.setLayout(null);

		JButton btnCloseToTray = new JButton("Run in Tray");
		btnCloseToTray.setBounds(8, 8, 88, 20);
		panel_3.add(btnCloseToTray);
		btnCloseToTray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tray.addTray();
			}
		});
		btnCloseToTray.setMargin(new Insets(2, 4, 2, 4));
		btnCloseToTray.setEnabled(SystemTray.isSupported());
		btnCloseToTray.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnLogOff = new JButton("Log Off");
		btnLogOff.setIcon(UIUtil.getIcon("icons16/general/door.png"));
		btnLogOff.setMargin(new Insets(2, 4, 2, 4));
		btnLogOff.setFont(new Font("Dialog", Font.BOLD, 10));
		btnLogOff.setBounds(8, 32, 88, 20);
		panel_3.add(btnLogOff);
	}

	private void initValues() {
		tglbtnList.setSelected(PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST).equals("list"));
		tglbtnGraph.setSelected(PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST).equals("graph"));
	}
}
