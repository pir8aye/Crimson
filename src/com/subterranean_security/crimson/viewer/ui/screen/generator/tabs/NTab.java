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
package com.subterranean_security.crimson.viewer.ui.screen.generator.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.generator.NetworkTargetTable;
import com.subterranean_security.crimson.viewer.ui.screen.generator.ep.AddNetworkTarget;
import com.subterranean_security.crimson.viewer.ui.screen.generator.ep.ViewNetworktarget;

public class NTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private EPanel ep;

	public NetworkTargetTable table;
	public JSpinner fld_connect_period;
	public JButton btnRemove;
	public JButton btnTest;
	public JCheckBox chckbxDontMiscConnections;
	public JCheckBox chckbxIgnoreServerCertificate;

	public NTab(EPanel ep) {
		this.ep = ep;
		init();
	}

	public void init() {

		JPanel panel_19 = new JPanel();
		panel_19.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_19.setPreferredSize(new Dimension(340, 100));
		panel_19.setLayout(new BorderLayout(0, 0));
		add(panel_19);

		table = new NetworkTargetTable(this);
		panel_19.add(table);

		JMenuBar menuBar = new JMenuBar();
		panel_19.add(menuBar, BorderLayout.NORTH);

		JButton btnAdd_1 = new JButton(UIUtil.getIcon("icons16/general/server_add.png"));
		btnAdd_1.setToolTipText("Add new network target");
		btnAdd_1.setMargin(new Insets(2, 2, 2, 2));
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ep.raise(new AddNetworkTarget(ep, NTab.this), 120);
			}
		});
		menuBar.add(btnAdd_1);

		btnRemove = new JButton(UIUtil.getIcon("icons16/general/server_delete.png"));
		btnRemove.setToolTipText("Remove network target");
		btnRemove.setMargin(new Insets(2, 2, 2, 2));
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.remove();
			}
		});
		btnRemove.setEnabled(false);
		menuBar.add(btnRemove);

		btnTest = new JButton(UIUtil.getIcon("icons16/general/server_chart.png"));
		btnTest.setToolTipText("Test network target");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NetworkTarget nt = table.getSelected();
				ep.raise(new ViewNetworktarget(ep, nt.getServer(), nt.getPort()), 80);
			}
		});
		btnTest.setEnabled(false);
		btnTest.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnTest);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(340, 100));
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(UICommon.basic, "Network Options", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		add(panel);

		fld_connect_period = new JSpinner();
		fld_connect_period.setModel(new SpinnerNumberModel(new Integer(20), new Integer(2), null, new Integer(1)));
		fld_connect_period.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_connect_period.setBounds(214, 18, 55, 20);
		panel.add(fld_connect_period);

		JLabel label = new JLabel("Connection Period:");
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setBounds(12, 20, 129, 15);
		panel.add(label);

		JLabel label_1 = new JLabel("seconds");
		label_1.setFont(new Font("Dialog", Font.BOLD, 10));
		label_1.setBounds(275, 20, 51, 15);
		panel.add(label_1);

		chckbxIgnoreServerCertificate = new JCheckBox("Ignore server certificate");
		chckbxIgnoreServerCertificate.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxIgnoreServerCertificate.setBounds(8, 41, 318, 20);
		panel.add(chckbxIgnoreServerCertificate);

		chckbxDontMiscConnections = new JCheckBox("Don't make miscellanous connections");
		chckbxDontMiscConnections.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxDontMiscConnections.setBounds(8, 65, 318, 20);
		panel.add(chckbxDontMiscConnections);

	}

}
