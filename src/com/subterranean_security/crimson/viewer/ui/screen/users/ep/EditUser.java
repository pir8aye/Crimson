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
package com.subterranean_security.crimson.viewer.ui.screen.users.ep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

public class EditUser extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private StatusLabel sl;
	private JCheckBox chckbxGenerator;
	private JCheckBox chckbxListenerCreation;
	private JCheckBox chckbxServerPower;
	private JCheckBox chckbxServerSettings;
	private JCheckBox chckbxServerFilesystemRead;
	private JCheckBox chckbxServerFilesystemWrite;
	private JCheckBox chckbxSuperuser;
	private JPasswordField fld_old;
	private JPasswordField fld_new;
	private JPasswordField fld_retype;

	private ViewerProfile original;
	private EPanel ep;
	private JCheckBox chckbxAddUser;
	private JCheckBox chckbxViewUsers;

	public EditUser(EPanel ep, ViewerProfile original) {
		this.ep = ep;
		this.original = original;
		init();
	}

	public void init() {

		setBounds(100, 100, 658, 245);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setPreferredSize(new Dimension(430, 185));
		contentPanel.add(panel_1);
		panel_1.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(8, 6, 211, 102);
		panel_1.add(panel);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Password", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setLayout(null);

		fld_old = new JPasswordField();
		fld_old.setBounds(100, 24, 99, 19);
		panel.add(fld_old);

		fld_new = new JPasswordField();
		fld_new.setBounds(100, 48, 99, 19);
		panel.add(fld_new);

		fld_retype = new JPasswordField();
		fld_retype.setBounds(100, 72, 99, 19);
		panel.add(fld_retype);

		JLabel lblOldPassword = new JLabel("Old Password:");
		lblOldPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		lblOldPassword.setBounds(8, 26, 87, 15);
		panel.add(lblOldPassword);

		JLabel lblNewPassword = new JLabel("New Password:");
		lblNewPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewPassword.setBounds(8, 50, 87, 15);
		panel.add(lblNewPassword);

		JLabel lblRetype = new JLabel("Retype:");
		lblRetype.setFont(new Font("Dialog", Font.BOLD, 10));
		lblRetype.setBounds(8, 74, 87, 15);
		panel.add(lblRetype);
		{
			JPanel panel_1_1 = new JPanel();
			panel_1_1.setBounds(225, 6, 197, 170);
			panel_1.add(panel_1_1);
			panel_1_1.setBorder(
					new TitledBorder(null, "Permissions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_1_1.setLayout(null);

			chckbxGenerator = new JCheckBox("Generate Installer");
			chckbxGenerator.setSelected(original.getPermissions().getFlag(Perm.server.generator.generate));
			chckbxGenerator.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxGenerator.setBounds(8, 47, 181, 16);
			panel_1_1.add(chckbxGenerator);

			chckbxListenerCreation = new JCheckBox("Create Listener");
			chckbxListenerCreation.setSelected(original.getPermissions().getFlag(Perm.server.network.create_listener));
			chckbxListenerCreation.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxListenerCreation.setBounds(8, 63, 181, 16);
			panel_1_1.add(chckbxListenerCreation);

			chckbxServerPower = new JCheckBox("Server Power");
			chckbxServerPower.setSelected(original.getPermissions().getFlag(Perm.server.power.modify));
			chckbxServerPower.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerPower.setBounds(8, 79, 181, 16);
			panel_1_1.add(chckbxServerPower);

			chckbxServerSettings = new JCheckBox("Server Settings");
			chckbxServerSettings.setSelected(original.getPermissions().getFlag(Perm.server.settings.modify));
			chckbxServerSettings.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerSettings.setBounds(8, 95, 181, 16);
			panel_1_1.add(chckbxServerSettings);

			chckbxServerFilesystemRead = new JCheckBox("Server Filesystem Read");
			chckbxServerFilesystemRead.setSelected(original.getPermissions().getFlag(Perm.server.fs.read));
			chckbxServerFilesystemRead.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemRead.setBounds(8, 111, 181, 16);
			panel_1_1.add(chckbxServerFilesystemRead);

			chckbxServerFilesystemWrite = new JCheckBox("Server Filesystem Write");
			chckbxServerFilesystemWrite.setSelected(original.getPermissions().getFlag(Perm.server.fs.read));
			chckbxServerFilesystemWrite.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemWrite.setBounds(8, 127, 181, 16);
			panel_1_1.add(chckbxServerFilesystemWrite);

			chckbxAddUser = new JCheckBox("Add User");
			chckbxAddUser.setSelected(original.getPermissions().getFlag(Perm.server.users.create));
			chckbxAddUser.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxAddUser.setBounds(8, 15, 181, 16);
			panel_1_1.add(chckbxAddUser);

			chckbxViewUsers = new JCheckBox("View Users");
			chckbxViewUsers.setSelected(original.getPermissions().getFlag(Perm.server.users.view));
			chckbxViewUsers.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxViewUsers.setBounds(8, 31, 181, 16);
			panel_1_1.add(chckbxViewUsers);
		}

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Other", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(8, 113, 211, 63);
		panel_1.add(panel_2);
		panel_2.setLayout(null);

		JCheckBox chckbxAllowRemoteLogins = new JCheckBox("Allow remote logins");
		chckbxAllowRemoteLogins.setSelected(true);
		chckbxAllowRemoteLogins.setEnabled(false);
		chckbxAllowRemoteLogins.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxAllowRemoteLogins.setBounds(8, 16, 195, 16);
		panel_2.add(chckbxAllowRemoteLogins);

		chckbxSuperuser = new JCheckBox("Superuser");
		chckbxSuperuser.setBounds(8, 35, 181, 16);
		panel_2.add(chckbxSuperuser);
		chckbxSuperuser.setSelected(original.getPermissions().getFlag(Perm.Super));
		chckbxSuperuser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean s = chckbxSuperuser.isSelected();
				chckbxGenerator.setSelected(s);
				chckbxGenerator.setEnabled(!s);

				chckbxListenerCreation.setSelected(s);
				chckbxListenerCreation.setEnabled(!s);

				chckbxServerPower.setSelected(s);
				chckbxServerPower.setEnabled(!s);

				chckbxServerSettings.setSelected(s);
				chckbxServerSettings.setEnabled(!s);

				chckbxServerFilesystemRead.setSelected(s);
				chckbxServerFilesystemRead.setEnabled(!s);

				chckbxServerFilesystemWrite.setSelected(s);
				chckbxServerFilesystemWrite.setEnabled(!s);

				chckbxAddUser.setSelected(s);
				chckbxAddUser.setEnabled(!s);

				chckbxViewUsers.setSelected(s);
				chckbxViewUsers.setEnabled(!s);
			}
		});
		chckbxSuperuser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				sl.setInfo("Grant all privileges on server and clients");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sl.setDefault();
			}
		});
		chckbxSuperuser.setFont(new Font("Dialog", Font.BOLD, 10));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMargin(new Insets(2, 4, 2, 4));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ep.drop();
					}
				});
				cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
				buttonPane.add(cancelButton);
			}
			{
				JButton okButton = new JButton("Apply");
				okButton.setMargin(new Insets(2, 8, 2, 8));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (!verify()) {
							return;
						}
						sl.setInfo("Applying changes...");
						new SwingWorker<Outcome, Void>() {

							@Override
							protected Outcome doInBackground() throws Exception {
								ViewerPermissions vp = new ViewerPermissions();
								vp.addFlag(Perm.Super, chckbxSuperuser.isSelected())
										.addFlag(Perm.server.generator.generate, chckbxGenerator.isSelected())
										.addFlag(Perm.server.network.create_listener,
												chckbxListenerCreation.isSelected())
										.addFlag(Perm.server.power.modify, chckbxServerPower.isSelected())
										.addFlag(Perm.server.settings.modify, chckbxServerSettings.isSelected())
										.addFlag(Perm.server.fs.read, chckbxServerFilesystemRead.isSelected())
										.addFlag(Perm.server.fs.read, chckbxServerFilesystemWrite.isSelected())
										.addFlag(Perm.server.users.view, chckbxViewUsers.isSelected())
										.addFlag(Perm.server.users.create, chckbxAddUser.isSelected());
								String oldPass = UIUtil.getPassword(fld_old);
								String newPass = UIUtil.getPassword(fld_new);
								return ViewerCommands.editUser(original.get(AKeySimple.VIEWER_USER),
										oldPass.isEmpty() ? null : oldPass, oldPass.isEmpty() ? null : newPass, vp);
							}

							protected void done() {
								try {
									Outcome outcome = get();
									if (outcome.getResult()) {
										sl.setGood("Success!");
										new SwingWorker<Void, Void>() {
											@Override
											protected Void doInBackground() throws Exception {
												Thread.sleep(700);
												return null;
											}

											protected void done() {
												ep.drop();
											};

										}.execute();

									} else {
										if (outcome.hasComment()) {
											sl.setBad("Failed: " + outcome.getComment());
										} else {
											sl.setBad("Failed to edit user!");
										}

									}
								} catch (InterruptedException | ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							};
						}.execute();

					}
				});
				okButton.setFont(new Font("Dialog", Font.BOLD, 10));
				buttonPane.add(okButton);
			}
		}

		sl = new StatusLabel("Editing user: " + original.get(AKeySimple.VIEWER_USER));
		add(sl, BorderLayout.NORTH);
	}

	private boolean verify() {

		return true;

	}
}
