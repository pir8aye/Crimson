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

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.Tray;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MConstants;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class InterfaceDetail extends MDetail {

	private static final long serialVersionUID = 1L;

	private JToggleButton tglbtnList;
	private JToggleButton tglbtnGraph;

	private JToggleButton tglbtnHistory;

	public InterfaceDetail(MPanel mp) {
		super(mp);

		init();
		initValues();
	}

	private void init() {
		lbl_header.setText("Interface");
		lbl_header.setIcon(UIUtil.getIcon("icons16/general/application.png"));

		JPanel body1 = new JPanel(null);
		body1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body1.setBounds(MConstants.PANEL_X_OFFSET, 39, MConstants.PANEL_WIDTH, 88);
		add(body1);

		tglbtnList = new JToggleButton("Host List");
		tglbtnList.setIcon(UIUtil.getIcon("icons16/general/view_list.png"));
		tglbtnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.main.panel.switchToList();
			}
		});

		tglbtnList.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnList.setMargin(new Insets(2, 2, 2, 2));
		tglbtnList.setBounds(MConstants.BUTTON_X_OFFSET, 8, MConstants.BUTTON_WIDTH, 20);
		body1.add(tglbtnList);

		tglbtnGraph = new JToggleButton("Host Graph");
		tglbtnGraph.setIcon(UIUtil.getIcon("icons16/general/view_graph.png"));
		tglbtnGraph.setMargin(new Insets(2, 2, 2, 2));
		tglbtnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.main.panel.switchToGraph();
			}
		});

		tglbtnGraph.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnGraph.setBounds(MConstants.BUTTON_X_OFFSET, 32, MConstants.BUTTON_WIDTH, 20);
		body1.add(tglbtnGraph);

		tglbtnHistory = new JToggleButton("History");
		tglbtnHistory.setIcon(UIUtil.getIcon("icons16/general/view_history.png"));
		tglbtnHistory.setEnabled(false);
		tglbtnHistory.setMargin(new Insets(2, 4, 2, 4));
		tglbtnHistory.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnHistory.setBounds(MConstants.BUTTON_X_OFFSET, 56, MConstants.BUTTON_WIDTH, 20);
		body1.add(tglbtnHistory);

		ButtonGroup bg = new ButtonGroup();
		bg.add(tglbtnList);
		bg.add(tglbtnGraph);
		bg.add(tglbtnHistory);

		JPanel body2 = new JPanel(null);
		body2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body2.setBounds(MConstants.PANEL_X_OFFSET, 134, MConstants.PANEL_WIDTH, 60);
		add(body2);

		JButton btnViewConsole = getButton(8, "icons16/general/view_console.png", "Console");
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
		body2.add(btnViewConsole);

		JButton btnSidebar = getButton(32, "icons16/general/sidebar.png", "Sidebar");
		body2.add(btnSidebar);

		JPanel body3 = new JPanel(null);
		body3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body3.setBounds(MConstants.PANEL_X_OFFSET, 204, MConstants.PANEL_WIDTH, 60);
		add(body3);

		JButton btnCloseToTray = getButton(8, "icons16/general/server.png", "Run in Tray");
		btnCloseToTray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tray.addTray();
			}
		});
		body3.add(btnCloseToTray);

		JButton btnLogOff = getButton(32, "icons16/general/door.png", "Log Off");
		body3.add(btnLogOff);
	}

	private void initValues() {
		tglbtnList.setSelected(PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST).equals("list"));
		tglbtnGraph.setSelected(PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST).equals("graph"));
	}
}
