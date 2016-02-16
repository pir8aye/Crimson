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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.component.Tray;

public class MenuControls extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int width = 200;
	private static final int length = 265;

	public MenuControls() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setSize(new Dimension(200, 265));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Server Status",
				TitledBorder.CENTER, TitledBorder.TOP, null, UICommon.controlTitledBorder));
		panel_1.setBounds(0, 0, 198, 70);
		panel.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblConnections = new JLabel("Connections:");
		lblConnections.setFont(new Font("Dialog", Font.BOLD, 10));
		lblConnections.setBounds(12, 17, 99, 17);
		panel_1.add(lblConnections);

		JLabel label_1 = new JLabel("0");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setBounds(131, 17, 55, 17);
		panel_1.add(label_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Local Throttling",
				TitledBorder.CENTER, TitledBorder.TOP, null, UICommon.controlTitledBorder));
		panel_2.setBounds(0, 67, 198, 70);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JSpinner spinner = new JSpinner();
		spinner.setRequestFocusEnabled(false);
		spinner.setBounds(107, 24, 50, 20);
		panel_2.add(spinner);

		JSpinner spinner_1 = new JSpinner();
		spinner_1.setRequestFocusEnabled(false);
		spinner_1.setBounds(107, 46, 50, 20);
		panel_2.add(spinner_1);

		JLabel lblKibs = new JLabel("kib/s");
		lblKibs.setFont(new Font("Dialog", Font.BOLD, 10));
		lblKibs.setBounds(164, 27, 34, 15);
		panel_2.add(lblKibs);

		JLabel label = new JLabel("kib/s");
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setBounds(164, 48, 34, 15);
		panel_2.add(label);

		JLabel lblUpstreamLimit = new JLabel("UPSTREAM LIMIT");
		lblUpstreamLimit.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUpstreamLimit.setBounds(10, 26, 98, 16);
		panel_2.add(lblUpstreamLimit);

		JLabel lblDownstreamLimit = new JLabel("DNSTREAM LIMIT");
		lblDownstreamLimit.setFont(new Font("Dialog", Font.BOLD, 10));
		lblDownstreamLimit.setBounds(10, 48, 98, 16);
		panel_2.add(lblDownstreamLimit);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "State Controls",
				TitledBorder.CENTER, TitledBorder.TOP, null, UICommon.controlTitledBorder));
		panel_3.setBounds(0, 186, 198, 77);
		panel.add(panel_3);
		panel_3.setLayout(null);

		JButton btnCloseToTray = new JButton("Run in Tray");
		btnCloseToTray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tray.addTray();
			}
		});
		btnCloseToTray.setMargin(new Insets(2, 4, 2, 4));
		btnCloseToTray.setBounds(12, 25, 88, 20);
		btnCloseToTray.setEnabled(SystemTray.isSupported());

		panel_3.add(btnCloseToTray);
		btnCloseToTray.setFont(new Font("Dialog", Font.BOLD, 10));

		JButton btnShutdown = new JButton("Shutdown");
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnShutdown.setBounds(100, 25, 88, 20);
		panel_3.add(btnShutdown);
		btnShutdown.setMargin(new Insets(2, 4, 2, 4));
		btnShutdown.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "View Controls",
				TitledBorder.CENTER, TitledBorder.TOP, null, UICommon.controlTitledBorder));
		panel_4.setBounds(0, 135, 198, 53);
		panel.add(panel_4);
		panel_4.setLayout(null);

		final JToggleButton tglbtnList = new JToggleButton("LIST");
		tglbtnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.main.panel.switchToList();
			}
		});
		String view = null;
		try {
			view = ViewerStore.Databases.local.getString("view.last");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		tglbtnList.setSelected(view.equals("list"));
		tglbtnList.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnList.setMargin(new Insets(2, 4, 2, 4));
		tglbtnList.setBounds(12, 20, 88, 20);
		panel_4.add(tglbtnList);

		final JToggleButton tglbtnGraph = new JToggleButton("GRAPH");
		tglbtnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.main.panel.switchToGraph();
			}
		});
		tglbtnGraph.setSelected(view.equals("graph"));
		tglbtnGraph.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnGraph.setBounds(100, 20, 88, 20);
		panel_4.add(tglbtnGraph);

		ButtonGroup bg = new ButtonGroup();
		bg.add(tglbtnList);
		bg.add(tglbtnGraph);

		add(Box.createHorizontalStrut(width), BorderLayout.SOUTH);
		add(Box.createVerticalStrut(length), BorderLayout.EAST);

	}
}
