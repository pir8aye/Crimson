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
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Users.ViewerPermissions;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.sv.ViewerProfile;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class EditUser extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private StatusLabel sl;
	private JCheckBox chckbxGenerator;
	private JCheckBox chckbxListenerCreation;
	private JCheckBox chckbxServerPower;
	private JCheckBox chckbxServerSettings;
	private JCheckBox chckbxServerFilesystemRead;
	private JCheckBox chckbxServerFilesystemWrite;

	public EditUser(ViewerProfile original) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Edit User: " + original.getUser());
		setResizable(false);
		setIconImages(UUtil.getIconList());
		setBounds(100, 100, 290, 365);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "User Information",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 12, 256, 55);
		contentPanel.add(panel);
		panel.setLayout(null);

		JCheckBox chckbxSuperuser = new JCheckBox("Superuser");
		chckbxSuperuser.setSelected(original.getPermissions().getSuper());
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
		chckbxSuperuser.setBounds(8, 20, 240, 23);
		panel.add(chckbxSuperuser);
		chckbxSuperuser.setFont(new Font("Dialog", Font.BOLD, 10));
		{
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(
					new TitledBorder(null, "Permissions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_1.setBounds(12, 79, 256, 188);
			contentPanel.add(panel_1);
			panel_1.setLayout(null);

			chckbxGenerator = new JCheckBox("Generator");
			chckbxGenerator.setSelected(original.getPermissions().getGenerate());
			chckbxGenerator.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxGenerator.setBounds(8, 20, 240, 23);
			panel_1.add(chckbxGenerator);

			chckbxListenerCreation = new JCheckBox("Listener Creation");
			chckbxListenerCreation.setSelected(original.getPermissions().getCreateListener());
			chckbxListenerCreation.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxListenerCreation.setBounds(8, 47, 240, 23);
			panel_1.add(chckbxListenerCreation);

			chckbxServerPower = new JCheckBox("Server Power");
			chckbxServerPower.setSelected(original.getPermissions().getServerPower());
			chckbxServerPower.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerPower.setBounds(8, 74, 240, 23);
			panel_1.add(chckbxServerPower);

			chckbxServerSettings = new JCheckBox("Server Settings");
			chckbxServerSettings.setSelected(original.getPermissions().getServerSettings());
			chckbxServerSettings.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerSettings.setBounds(8, 101, 240, 23);
			panel_1.add(chckbxServerSettings);

			chckbxServerFilesystemRead = new JCheckBox("Server Filesystem Read");
			chckbxServerFilesystemRead.setSelected(original.getPermissions().getServerFsRead());
			chckbxServerFilesystemRead.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemRead.setBounds(8, 128, 240, 23);
			panel_1.add(chckbxServerFilesystemRead);

			chckbxServerFilesystemWrite = new JCheckBox("Server Filesystem Write");
			chckbxServerFilesystemWrite.setSelected(original.getPermissions().getServerFsWrite());
			chckbxServerFilesystemWrite.setFont(new Font("Dialog", Font.BOLD, 10));
			chckbxServerFilesystemWrite.setBounds(8, 155, 240, 23);
			panel_1.add(chckbxServerFilesystemWrite);
		}

		sl = new StatusLabel();
		sl.setBounds(12, 279, 256, 15);
		contentPanel.add(sl);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
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
									sl.setInfo("Applying changes...");
									StringBuffer error = new StringBuffer();
									ViewerPermissions vp = ViewerPermissions.newBuilder()
											.setSuper(chckbxSuperuser.isSelected())
											.setGenerate(chckbxGenerator.isSelected())
											.setCreateListener(chckbxListenerCreation.isSelected())
											.setServerPower(chckbxServerPower.isSelected())
											.setServerSettings(chckbxServerSettings.isSelected())
											.setServerFsRead(chckbxServerFilesystemRead.isSelected())
											.setServerFsWrite(chckbxServerFilesystemWrite.isSelected()).build();
									if (ViewerCommands.editUser(error, original.getUser(), null, vp)) {
										sl.setGood("Success!");
										try {
											Thread.sleep(700);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										dispose();
									} else {
										if (error.length() != 0) {
											sl.setBad("Failed: " + error.toString());
										} else {
											sl.setBad("Failed to edit user!");
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

		return true;

	}

	@Override
	public void dispose() {
		super.dispose();
		UsersPanel.addDialog = null;
	}
}
