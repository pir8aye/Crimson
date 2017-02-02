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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.users.ep.EditUser;

public class UsersPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public JButton btnRemove;
	public JButton btnAddUser;
	public JButton btnEditPermissions;

	public static AddUser addDialog;
	public UserTable ut;

	private EPanel ep;

	public UsersPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));

		ep = new EPanel(panel);
		add(ep, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		ut = new UserTable(this);
		panel_2.add(ut);

		JMenuBar panel_3 = new JMenuBar();
		panel.add(panel_3, BorderLayout.NORTH);

		btnEditPermissions = new JButton("Edit User");
		btnEditPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO check permissions
				ep.raise(new EditUser(ep, ut.getSelected()), 245);

			}
		});

		btnAddUser = new JButton("Add User");
		btnAddUser.setEnabled(ProfileStore.getLocalViewer().getPermissions().getFlag(Perm.server.users.create));
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (addDialog == null) {
					addDialog = new AddUser();
					addDialog.setVisible(true);
					addDialog.setLocationRelativeTo(null);
				} else {
					addDialog.toFront();
				}

			}
		});
		btnAddUser.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAddUser.setMargin(new Insets(2, 4, 2, 4));
		panel_3.add(btnAddUser);
		btnEditPermissions.setEnabled(false);
		btnEditPermissions.setFont(new Font("Dialog", Font.BOLD, 10));
		btnEditPermissions.setMargin(new Insets(2, 4, 2, 4));
		panel_3.add(btnEditPermissions);

		btnRemove = new JButton("Delete User");
		btnRemove.setEnabled(false);
		btnRemove.setMargin(new Insets(2, 4, 2, 4));
		btnRemove.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_3.add(btnRemove);

	}

}
