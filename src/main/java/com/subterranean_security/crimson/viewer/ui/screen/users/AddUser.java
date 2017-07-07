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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.viewer.command.UserCom;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;

public class AddUser extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private StatusLabel sl;
	private JPasswordField passwordField;
	private JCheckBox chckbxGenerator;
	private JCheckBox chckbxListenerCreation;
	private JCheckBox chckbxServerPower;
	private JCheckBox chckbxServerSettings;
	private JCheckBox chckbxServerFilesystemRead;
	private JCheckBox chckbxServerFilesystemWrite;

	public AddUser() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Add User");
		setResizable(false);
		setIconImages(UIUtil.getAppIcons());
		setBounds(100, 100, 290, 409);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "User Information",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 12, 256, 108);
		contentPanel.add(panel);
		panel.setLayout(null);

		textField = new JTextField();
		textField.setBounds(130, 17, 114, 19);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUsername.setBounds(12, 20, 70, 15);
		panel.add(lblUsername);

		JCheckBox chckbxSuperuser = new JCheckBox("Superuser");
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
			}
		});
		chckbxSuperuser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				sl.setInfo("Grants all rights on server and clients");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sl.setDefault();
			}
		});
		chckbxSuperuser.setBounds(8, 77, 240, 23);
		panel.add(chckbxSuperuser);
		chckbxSuperuser.setFont(new Font("Dialog", Font.BOLD, 10));

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPassword.setBounds(12, 45, 70, 15);
		panel.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setBounds(130, 42, 114, 19);
		panel.add(passwordField);
		{
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(
					new TitledBorder(null, "Permissions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_1.setBounds(12, 132, 256, 188);
			contentPanel.add(panel_1);
			panel_1.setLayout(null);

			chckbxGenerator = new JCheckBox("Generator");
			chckbxGenerator.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent arg0) {
					sl.setInfo("Grants rights to generate installers");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxGenerator.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxGenerator.setBounds(8, 20, 240, 23);
			panel_1.add(chckbxGenerator);

			chckbxListenerCreation = new JCheckBox("Listener Creation");
			chckbxListenerCreation.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					sl.setInfo("Grants rights create listeners on the server");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxListenerCreation.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxListenerCreation.setBounds(8, 47, 240, 23);
			panel_1.add(chckbxListenerCreation);

			chckbxServerPower = new JCheckBox("Server Power");
			chckbxServerPower.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					sl.setInfo("Grants rights to change server power states");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxServerPower.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerPower.setBounds(8, 74, 240, 23);
			panel_1.add(chckbxServerPower);

			chckbxServerSettings = new JCheckBox("Server Settings");
			chckbxServerSettings.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					sl.setInfo("Grants rights to modify server settings");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxServerSettings.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerSettings.setBounds(8, 101, 240, 23);
			panel_1.add(chckbxServerSettings);

			chckbxServerFilesystemRead = new JCheckBox("Server Filesystem Read");
			chckbxServerFilesystemRead.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					sl.setInfo("Grants read rights to server filesystem");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxServerFilesystemRead.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemRead.setBounds(8, 128, 240, 23);
			panel_1.add(chckbxServerFilesystemRead);

			chckbxServerFilesystemWrite = new JCheckBox("Server Filesystem Write");
			chckbxServerFilesystemWrite.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					sl.setInfo("Grants modification rights to server filesystem");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					sl.setDefault();
				}
			});
			chckbxServerFilesystemWrite.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemWrite.setBounds(8, 155, 240, 23);
			panel_1.add(chckbxServerFilesystemWrite);
		}

		sl = new StatusLabel("Enter credentials and select permissions");
		sl.setBounds(12, 321, 256, 15);
		contentPanel.add(sl);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();

					}
				});
				cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
				buttonPane.add(cancelButton);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new Thread(new Runnable() {
							public void run() {
								if (verify()) {
									sl.setInfo("Adding User...");
									ViewerPermissions vp = new ViewerPermissions();
									vp.setFlag(Perm.Super, chckbxSuperuser.isSelected())
											.setFlag(Perm.server.generator.generate_jar, chckbxGenerator.isSelected())
											.setFlag(Perm.server.network.create_listener,
													chckbxListenerCreation.isSelected())
											.setFlag(Perm.server.power.modify, chckbxServerPower.isSelected())
											.setFlag(Perm.server.settings.modify, chckbxServerSettings.isSelected())
											.setFlag(Perm.server.fs.read, chckbxServerFilesystemRead.isSelected())
											.setFlag(Perm.server.fs.read, chckbxServerFilesystemWrite.isSelected());
									Outcome outcome = UserCom.addUser(textField.getText(),
											UIUtil.getPassword(passwordField), vp);
									if (outcome.getResult()) {
										sl.setGood("Success!");
										try {
											Thread.sleep(700);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										dispose();
									} else {
										if (!outcome.getComment().isEmpty()) {
											sl.setBad("Failed: " + outcome.getComment());
										} else {
											sl.setBad("Failed to add user!");
										}

									}
								}
							}
						}).start();

					}
				});
				okButton.setFont(new Font("Dialog", Font.BOLD, 11));
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	private boolean verify() {
		if (!ValidationUtil.username(textField.getText())) {
			sl.setBad("Invalid Username");
			return false;
		}

		// check for username conflicts
		if (ProfileStore.getViewer(textField.getText()) != null) {
			sl.setBad("Username taken");
			return false;
		}

		if (!ValidationUtil.password(passwordField)) {
			sl.setBad("Invalid Password");
			return false;
		}

		return true;

	}

	@Override
	public void dispose() {
		super.dispose();
		UsersPanel.addDialog = null;
	}
}
