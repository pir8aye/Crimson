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
package com.subterranean_security.crimson.viewer.ui.screen.users;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class UsersPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public JButton btnRemove;
	public JButton btnAddUser;
	public JButton btnEditPermissions;
	public JButton btnChangePassword;

	public static AddUser addDialog;
	public static EditUser editDialog;
	public UserTable ut;

	public UsersPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		ut = new UserTable(this);
		panel_2.add(ut);

		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_3, BorderLayout.SOUTH);

		btnChangePassword = new JButton("Change Password");
		btnChangePassword.setEnabled(false);
		btnChangePassword.setFont(new Font("Dialog", Font.BOLD, 10));
		btnChangePassword.setMargin(new Insets(2, 4, 2, 4));
		panel_3.add(btnChangePassword);

		btnEditPermissions = new JButton("Edit User");
		btnEditPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO check permissions
				if (editDialog == null) {
					editDialog = new EditUser(ut.getSelected());
					editDialog.setVisible(true);
				} else {
					editDialog.setLocationRelativeTo(null);
				}
			}
		});
		btnEditPermissions.setEnabled(false);
		btnEditPermissions.setFont(new Font("Dialog", Font.BOLD, 10));
		btnEditPermissions.setMargin(new Insets(2, 4, 2, 4));
		panel_3.add(btnEditPermissions);

		btnAddUser = new JButton("Add User");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO check permissions
				if (addDialog == null) {
					addDialog = new AddUser();
					addDialog.setVisible(true);
				} else {
					addDialog.setLocationRelativeTo(null);
				}

			}
		});
		btnAddUser.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAddUser.setMargin(new Insets(2, 4, 2, 4));
		panel_3.add(btnAddUser);

		btnRemove = new JButton("Delete User");
		btnRemove.setEnabled(false);
		btnRemove.setMargin(new Insets(2, 4, 2, 4));
		btnRemove.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_3.add(btnRemove);

	}

}
