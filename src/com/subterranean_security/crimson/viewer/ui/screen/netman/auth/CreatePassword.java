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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;

public class CreatePassword extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;

	private StatusLabel sl = new StatusLabel("Enter a new password");

	public CreatePassword(EPanel ep) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIStore.createPassword = null;
				ep.drop();
			}
		});
		btnCancel.setMargin(new Insets(2, 4, 2, 4));
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnCancel);

		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnCreate.setMargin(new Insets(2, 4, 2, 4));
		btnCreate.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnCreate);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		add(sl, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 3, 0, 0, 3, 0 };
		gbl_panel_2.rowHeights = new int[] { 3, 0, 0, 3, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 1;
		panel_2.add(lblPassword, gbc_lblPassword);

		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 2;
		gbc_passwordField.gridy = 1;
		panel_2.add(passwordField, gbc_passwordField);

		JLabel lblRetype = new JLabel("Retype:");
		lblRetype.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblRetype = new GridBagConstraints();
		gbc_lblRetype.anchor = GridBagConstraints.EAST;
		gbc_lblRetype.insets = new Insets(0, 0, 5, 5);
		gbc_lblRetype.gridx = 1;
		gbc_lblRetype.gridy = 2;
		panel_2.add(lblRetype, gbc_lblRetype);

		passwordField_1 = new JPasswordField();
		GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
		gbc_passwordField_1.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_1.gridx = 2;
		gbc_passwordField_1.gridy = 2;
		panel_2.add(passwordField_1, gbc_passwordField_1);

	}
}
