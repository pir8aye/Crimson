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
package com.subterranean_security.crimson.viewer.ui.screen.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.ui.FieldLimiter;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.utility.CUtil;
import com.subterranean_security.crimson.viewer.network.ViewerCommands;
import com.subterranean_security.crimson.viewer.network.ViewerConnector;
import com.subterranean_security.crimson.viewer.ui.UICommon;

import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField fld_user;
	public JButton btn_cancel;
	public JButton btn_login;
	private JPasswordField fld_password;
	private JComboBox<String> fld_server;
	private JTextField fld_port;
	private JPanel panel_4;
	private JLabel lblIpdns;
	private JLabel lblPort;
	private JPanel panel_5;
	private LoginDialog parent;
	private StatusLabel lbl_status;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private Border fld_port_border;
	private Border fld_password_border;
	private Border fld_user_border;
	private Border fld_server_border;

	public LoginPanel(final LoginDialog parent, boolean local) {
		this.parent = parent;

		setPreferredSize(new Dimension(400, 248));
		setBorder(null);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(UICommon.bg);
		panel_1.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(panel_1);
		panel_1.setLayout(null);

		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(LoginPanel.class
				.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/crimson_logo-login.png")));
		label.setBounds(94, 0, 210, 105);
		panel_1.add(label);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 30));
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(panel);
		panel.setLayout(null);

		panel_4 = new JPanel();
		panel_4.setBounds(6, 6, 388, 54);
		panel.add(panel_4);
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Server", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panel_4.setLayout(null);

		lblIpdns = new JLabel("Address");
		lblIpdns.setBounds(20, 12, 280, 15);
		lblIpdns.setFont(new Font("Dialog", Font.PLAIN, 10));
		panel_4.add(lblIpdns);

		fld_server = new JComboBox<String>();
		fld_server.setFont(new Font("Dialog", Font.BOLD, 11));
		fld_server_border = fld_server.getBorder();
		fld_server.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("enter an ip address, hostname, or DNS name");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setVisible(false);
			}
		});
		fld_server.setBounds(20, 28, 280, 17);
		fld_server.setModel(new DefaultComboBoxModel<String>(new String[] { "127.0.0.1" }));
		fld_server.setMaximumSize(new Dimension(59, 19));
		panel_4.add(fld_server);
		fld_server.setEditable(true);

		lblPort = new JLabel("Port");
		lblPort.setHorizontalAlignment(SwingConstants.CENTER);
		lblPort.setBounds(312, 12, 56, 15);
		lblPort.setFont(new Font("Dialog", Font.PLAIN, 10));
		panel_4.add(lblPort);

		fld_port = new JTextField();
		fld_port.setFont(new Font("Dialog", Font.PLAIN, 11));
		fld_port_border = fld_port.getBorder();
		fld_port.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("port on the server to connect");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setVisible(false);
			}
		});
		fld_port.setDocument(new FieldLimiter(5, "\\d"));
		fld_port.setBounds(312, 28, 56, 17);
		fld_port.setPreferredSize(new Dimension(25, 19));
		fld_port.setMinimumSize(new Dimension(25, 19));
		fld_port.setHorizontalAlignment(SwingConstants.CENTER);
		if (Common.debug) {
			fld_port.setText("2031");
		}

		fld_port.setMaximumSize(new Dimension(25, 19));
		panel_4.add(fld_port);
		fld_port.setColumns(10);

		panel_5 = new JPanel();
		panel_5.setBounds(6, 60, 388, 55);
		panel.add(panel_5);
		panel_5.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Credentials", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panel_5.setLayout(null);

		fld_user = new JTextField();
		fld_user_border = fld_user.getBorder();
		fld_user.setDocument(new FieldLimiter(20));
		fld_user.setText("admin");
		fld_user.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("username to an account on the server");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setVisible(false);
			}
		});
		fld_user.setHorizontalAlignment(SwingConstants.CENTER);
		fld_user.setBounds(20, 28, 140, 19);
		fld_user.setMaximumSize(new Dimension(100, 19));
		panel_5.add(fld_user);
		fld_user.setColumns(10);

		fld_password = new JPasswordField();
		fld_password_border = fld_password.getBorder();
		fld_password.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("corresponding case-sensitive password");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setVisible(false);
			}
		});
		fld_password.setDocument(new FieldLimiter(32));
		fld_password.setText("casio");
		fld_password.setHorizontalAlignment(SwingConstants.CENTER);
		fld_password.setBounds(228, 28, 140, 19);
		fld_password.setMaximumSize(new Dimension(100, 19));
		panel_5.add(fld_password);

		lblUsername = new JLabel("Username");
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setFont(new Font("Dialog", Font.PLAIN, 10));
		lblUsername.setBounds(20, 12, 140, 15);
		panel_5.add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setFont(new Font("Dialog", Font.PLAIN, 10));
		lblPassword.setBounds(228, 12, 140, 15);
		panel_5.add(lblPassword);

		lbl_status = new StatusLabel();
		lbl_status.setVisible(false);
		lbl_status.setBounds(12, 120, 376, 15);
		panel.add(lbl_status);

		btn_cancel = new JButton("Exit");
		btn_cancel.setMargin(new Insets(0, 5, 0, 5));
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btn_cancel.setPreferredSize(new Dimension(60, 25));
		btn_cancel.setMaximumSize(new Dimension(60, 25));

		btn_login = new JButton("Login");
		btn_login.setMargin(new Insets(0, 5, 0, 5));
		btn_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {
					public void run() {
						login(parent, (String) fld_server.getSelectedItem(), fld_port.getText(), fld_user.getText(),
								fld_password.getPassword());
					}
				}).start();

			}
		});
		btn_login.setPreferredSize(new Dimension(60, 25));
		btn_login.setMaximumSize(new Dimension(60, 25));
	}

	private void login(LoginDialog dialog, String server, String port, String user, char[] password) {

		if (!testValues(server, port, user, password)) {
			return;
		}
		startLogin();

		try {

			try {
				ViewerConnector.connector = new ViewerConnector(server, Integer.parseInt(port));
				Logger.debug("Connection made");
			} catch (Throwable e) {
				Logger.debug("Connection failed");
				btn_login.setEnabled(true);
				lbl_status.unfreeze();
				lbl_status.setBad("Unable to Connect");
				return;
			}

			// test the credentials
			if (ViewerCommands.login(user, password)) {
				lbl_status.unfreeze();
				lbl_status.setGood("Login Successful");
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (parent) {
					parent.notifyAll();
				}
				parent.dispose();
			} else {
				ViewerConnector.connector = null;
				lbl_status.unfreeze();
				lbl_status.setBad("Failed to Login");
				btn_login.setEnabled(true);
			}
		} finally {
			endLogin();
		}

	}

	private boolean testValues(String server, String port, String user, char[] password) {
		removeErrorBorders();
		if (!CUtil.Validation.port(port)) {
			setPortError();
			return false;
		}
		if (!CUtil.Validation.password(password)) {
			setPassError();
			return false;
		}
		if (!CUtil.Validation.username(user)) {
			setUserError();
			return false;
		}
		if (!CUtil.Validation.dns(server) && !CUtil.Validation.ip(server)) {
			setServerError();
			return false;
		}
		return true;
	}

	private void startLogin() {
		btn_login.setEnabled(false);
		fld_password.setEnabled(false);
		fld_port.setEnabled(false);
		fld_server.setEnabled(false);
		fld_user.setEnabled(false);
		lblIpdns.setEnabled(false);
		lblPort.setEnabled(false);
		lblPassword.setEnabled(false);
		lblUsername.setEnabled(false);

		lbl_status.setInfo("Attempting Login");
		lbl_status.freeze();
	}

	private void endLogin() {

		btn_login.setEnabled(true);
		fld_password.setEnabled(true);
		fld_port.setEnabled(true);
		fld_server.setEnabled(true);
		fld_user.setEnabled(true);
		lblIpdns.setEnabled(true);
		lblPort.setEnabled(true);
		lblPassword.setEnabled(true);
		lblUsername.setEnabled(true);

		lbl_status.unfreeze();

	}

	private void removeErrorBorders() {
		fld_server.setBorder(fld_server_border);
		fld_port.setBorder(fld_port_border);
		fld_user.setBorder(fld_user_border);
		fld_password.setBorder(fld_password_border);
	}

	private void setServerError() {
		fld_server.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid server address");
	}

	private void setPortError() {
		fld_port.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid server port");
	}

	private void setUserError() {
		fld_user.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid username");
	}

	private void setPassError() {
		fld_password.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid password");
	}
}
