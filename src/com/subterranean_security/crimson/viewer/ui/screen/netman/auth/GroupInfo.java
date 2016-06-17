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
package com.subterranean_security.crimson.viewer.ui.screen.netman.auth;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class GroupInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	public GroupInfo(String name, String key) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblGroupName = new JLabel("Group name:");
		GridBagConstraints gbc_lblGroupName = new GridBagConstraints();
		gbc_lblGroupName.anchor = GridBagConstraints.EAST;
		gbc_lblGroupName.insets = new Insets(0, 0, 5, 5);
		gbc_lblGroupName.gridx = 1;
		gbc_lblGroupName.gridy = 0;
		panel_2.add(lblGroupName, gbc_lblGroupName);

		JLabel lblValue = new JLabel("value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblValue.gridx = 2;
		gbc_lblValue.gridy = 0;
		panel_2.add(lblValue, gbc_lblValue);

		JLabel lblCreation = new JLabel("Creation:");
		GridBagConstraints gbc_lblCreation = new GridBagConstraints();
		gbc_lblCreation.anchor = GridBagConstraints.EAST;
		gbc_lblCreation.insets = new Insets(0, 0, 0, 5);
		gbc_lblCreation.gridx = 1;
		gbc_lblCreation.gridy = 1;
		panel_2.add(lblCreation, gbc_lblCreation);

		JLabel lblValue_1 = new JLabel("value");
		GridBagConstraints gbc_lblValue_1 = new GridBagConstraints();
		gbc_lblValue_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue_1.gridx = 2;
		gbc_lblValue_1.gridy = 1;
		panel_2.add(lblValue_1, gbc_lblValue_1);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Close");
		btnClose.setMargin(new Insets(2, 4, 2, 4));
		btnClose.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_1.add(btnClose);

		JLabel lblGroupKeyfbddefca = new JLabel("Group Key: 2f9bdd30efc0a01");
		lblGroupKeyfbddefca.setFont(new Font("Dialog", Font.BOLD, 9));
		add(lblGroupKeyfbddefca, BorderLayout.NORTH);

	}

}
