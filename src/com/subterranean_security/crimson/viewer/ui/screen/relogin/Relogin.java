package com.subterranean_security.crimson.viewer.ui.screen.relogin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.net.ViewerConnector;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;

public class Relogin extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPasswordField fld_pass;
	private JButton btnLogin;
	private JButton btnCancel;
	private StatusLabel lbl_status;
	private JLabel lbl_user;
	private JLabel lbl_server;

	private EPanel parent;

	public Relogin(EPanel ep) {
		this.parent = ep;

		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parent.drop();
			}
		});
		btnCancel.setMargin(new Insets(1, 5, 1, 5));
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_1.add(btnCancel);

		btnLogin = new JButton("Login");
		btnLogin.setEnabled(false);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						String[] parts = lbl_server.getText().split(":");
						login(parts[0], parts[1], lbl_user.getText(), fld_pass.getPassword());
					}
				}).start();
			}
		});
		btnLogin.setMargin(new Insets(1, 8, 1, 9));
		btnLogin.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_1.add(btnLogin);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		lbl_status = new StatusLabel("Re-enter your password to reconnect to the server");
		panel_2.add(lbl_status, BorderLayout.NORTH);

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel_3.add(panel);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 3, 0, 0, 3, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 3, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblServerl = new JLabel("Server:");
		lblServerl.setHorizontalAlignment(SwingConstants.TRAILING);
		lblServerl.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblServerl = new GridBagConstraints();
		gbc_lblServerl.anchor = GridBagConstraints.EAST;
		gbc_lblServerl.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerl.gridx = 1;
		gbc_lblServerl.gridy = 0;
		panel.add(lblServerl, gbc_lblServerl);

		lbl_server = new JLabel(ViewerState.getServer() + ":" + ViewerState.getPort());
		lbl_server.setEnabled(false);
		lbl_server.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		panel.add(lbl_server, gbc_label);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 1;
		panel.add(lblUsername, gbc_lblUsername);

		lbl_user = new JLabel(ViewerStore.Profiles.vp.getUser());
		lbl_user.setEnabled(false);
		lbl_user.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblAdmin = new GridBagConstraints();
		gbc_lblAdmin.anchor = GridBagConstraints.WEST;
		gbc_lblAdmin.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdmin.gridx = 2;
		gbc_lblAdmin.gridy = 1;
		panel.add(lbl_user, gbc_lblAdmin);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 2;
		panel.add(lblPassword, gbc_lblPassword);

		fld_pass = new JPasswordField();
		fld_pass.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				btnLogin.setEnabled(testValues());
			}
		});
		fld_pass.setColumns(14);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.anchor = GridBagConstraints.WEST;
		gbc_passwordField.gridx = 2;
		gbc_passwordField.gridy = 2;
		panel.add(fld_pass, gbc_passwordField);
	}

	private void login(String server, String port, String user, char[] password) {

		startLogin();

		try {

			try {
				ViewerStore.Connections.put(0, new ViewerConnector(server, Integer.parseInt(port)));
			} catch (Throwable e) {
				lbl_status.setBad("Unable to Connect");
				return;
			}

			// test the credentials
			if (ViewerCommands.login(user, password)) {
				lbl_status.setGood("Login Successful");
				ViewerState.goOnline(server, Integer.parseInt(port));
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				parent.drop();

			} else {
				ViewerStore.Connections.closeAll();
				lbl_status.setBad("Failed to Login");
			}
		} finally {
			endLogin();
		}

	}

	private void startLogin() {
		btnLogin.setEnabled(false);
		btnCancel.setEnabled(false);
		fld_pass.setEnabled(false);

		lbl_status.setInfo("Attempting Login");
	}

	private void endLogin() {

		btnLogin.setEnabled(true);
		btnCancel.setEnabled(true);
		fld_pass.setEnabled(true);

	}

	private boolean testValues() {

		return CUtil.Validation.password(fld_pass.getPassword());

	}

}
