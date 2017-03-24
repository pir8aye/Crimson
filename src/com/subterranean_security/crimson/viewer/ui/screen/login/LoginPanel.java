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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.ui.FieldLimiter;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.Validation;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.net.ViewerConnector;
import com.subterranean_security.crimson.viewer.net.commands.LoginCom;
import com.subterranean_security.crimson.viewer.store.ConnectionStore;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LoginPanel.class);

	private JTextField fld_user;
	public JButton btn_cancel;
	public JButton btn_login;
	private JPasswordField fld_pass;
	private JComboBox<String> fld_address;
	private JTextField fld_port;
	private JPanel panel_server;
	private JLabel lbl_address;
	private JLabel lbl_port;
	private JPanel panel_credentials;
	private LoginDialog parent;
	private StatusLabel lbl_status;
	private JLabel lbl_user;
	private JLabel lbl_pass;
	private Border fld_port_border;
	private Border fld_password_border;
	private Border fld_user_border;
	private Border fld_server_border;

	public boolean result = false;

	public LoginPanel(final LoginDialog parent) {
		this.parent = parent;
		init();

		if (Universal.isDebug) {
			fld_user.setText("admin");
			fld_pass.setText("default");
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					Thread.sleep(500);
					return null;
				}

				protected void done() {
					for (char c : "10101".toCharArray()) {
						fld_port.dispatchEvent(new KeyEvent(fld_port, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
								KeyEvent.KEY_FIRST, KeyEvent.VK_UNDEFINED, c));

					}
				};
			}.execute();

		}

	}

	private void init() {
		setPreferredSize(new Dimension(400, 248));
		setBorder(null);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel_header = new JPanel();
		panel_header.setBackground(UICommon.bg);
		panel_header.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(panel_header);
		panel_header.setLayout(null);

		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon(LoginPanel.class
				.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/crimson_logo-login.png")));
		logo.setBounds(94, 0, 210, 105);
		panel_header.add(logo);

		JPanel panel_body = new JPanel();
		panel_body.setPreferredSize(new Dimension(10, 30));
		panel_body.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(panel_body);
		panel_body.setLayout(null);

		panel_server = new JPanel();
		panel_server.setBounds(6, 6, 388, 54);
		panel_body.add(panel_server);
		panel_server.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Server", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panel_server.setLayout(null);

		lbl_address = new JLabel("Address");
		lbl_address.setBounds(20, 12, 280, 15);
		lbl_address.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_server.add(lbl_address);

		fld_address = new JComboBox<String>();
		fld_address.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fld_address.getSelectedIndex() == -1) {
					return;
				}

				String[] parts = ((String) fld_address.getSelectedItem()).split(":");

				String server = null;
				String port = null;
				switch (parts[0]) {
				case "Local Server": {
					server = "127.0.0.1";
					port = "10101";
					break;
				}
				case "Live Example Server": {
					server = "example.subterranean-security.com";
					port = "10101";
					fld_user.setText("testuser");
					fld_pass.setText("this-password-does-not-matter");
					break;
				}
				default: {
					if (parts.length == 2) {
						server = parts[0];
						port = parts[1];
					} else {
						return;
					}
				}
				}

				// insert info
				fld_address.setSelectedItem(server);
				// insert port
				try {
					fld_port.getDocument().remove(0, fld_port.getDocument().getLength());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// TODO find better way to add text to field
				for (char c : port.toCharArray()) {
					fld_port.dispatchEvent(new KeyEvent(fld_port, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
							KeyEvent.KEY_FIRST, KeyEvent.VK_UNDEFINED, c));

				}

			}
		});
		fld_address.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_server_border = fld_address.getBorder();
		fld_address.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("enter an ip address, hostname, or DNS name");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		fld_address.setBounds(20, 28, 280, 17);
		fld_address.setMaximumSize(new Dimension(59, 19));
		panel_server.add(fld_address);
		fld_address.setEditable(true);

		lbl_port = new JLabel("Port");
		lbl_port.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_port.setBounds(312, 12, 56, 15);
		lbl_port.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_server.add(lbl_port);

		fld_port = new JTextField();
		fld_port.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_port_border = fld_port.getBorder();
		fld_port.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("corresponding port on the server");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		fld_port.setDocument(new FieldLimiter(5, "\\d"));
		fld_port.setBounds(312, 28, 56, 17);
		fld_port.setPreferredSize(new Dimension(25, 19));
		fld_port.setMinimumSize(new Dimension(25, 19));
		fld_port.setHorizontalAlignment(SwingConstants.CENTER);

		fld_port.setMaximumSize(new Dimension(25, 19));
		panel_server.add(fld_port);
		fld_port.setColumns(10);

		panel_credentials = new JPanel();
		panel_credentials.setBounds(6, 60, 388, 55);
		panel_body.add(panel_credentials);
		panel_credentials.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Credentials",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel_credentials.setLayout(null);

		fld_user = new JTextField();
		fld_user.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_user_border = fld_user.getBorder();
		fld_user.setDocument(new FieldLimiter(60));
		fld_user.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("username to an account on the server");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		fld_user.setHorizontalAlignment(SwingConstants.CENTER);
		fld_user.setBounds(20, 28, 140, 19);
		fld_user.setMaximumSize(new Dimension(100, 19));
		panel_credentials.add(fld_user);
		fld_user.setColumns(10);

		fld_pass = new JPasswordField();
		fld_pass.setFont(new Font("Dialog", Font.PLAIN, 8));
		fld_password_border = fld_pass.getBorder();
		fld_pass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("corresponding case-sensitive password");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		fld_pass.setDocument(new FieldLimiter(64));
		fld_pass.setHorizontalAlignment(SwingConstants.CENTER);
		fld_pass.setBounds(228, 28, 140, 19);
		fld_pass.setMaximumSize(new Dimension(100, 19));
		panel_credentials.add(fld_pass);

		lbl_user = new JLabel("Username");
		lbl_user.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_user.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_user.setBounds(20, 12, 140, 15);
		panel_credentials.add(lbl_user);

		lbl_pass = new JLabel("Password");
		lbl_pass.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_pass.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_pass.setBounds(228, 12, 140, 15);
		panel_credentials.add(lbl_pass);

		lbl_status = new StatusLabel("Enter details to login to a crimson server");
		lbl_status.setVisible(true);
		lbl_status.setBounds(12, 120, 376, 15);
		panel_body.add(lbl_status);

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

				login((String) fld_address.getSelectedItem(), fld_port.getText(), fld_user.getText());

			}
		});
		btn_login.setPreferredSize(new Dimension(60, 25));
		btn_login.setMaximumSize(new Dimension(60, 25));
	}

	private void login(String server, String port, String user) {

		if (!testValues(server, port, user)) {
			return;
		}
		startLogin();

		new SwingWorker<Outcome, Void>() {

			@Override
			protected Outcome doInBackground() throws Exception {
				long t1 = System.currentTimeMillis();
				Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

				try {
					ConnectionStore.put(0, new ViewerConnector(server, Integer.parseInt(port)));
				} catch (Exception e) {
					try {
						outcome.setComment(
								"Unable to connect to server: " + InetAddress.getByName(server).getHostAddress());
					} catch (UnknownHostException e1) {
						outcome.setComment("Unable to resolve server: " + server);
					}

					return outcome.build();
				}

				// set local viewer profile name
				ProfileStore.setLocalUser(user);

				// request login from server
				Outcome loginOutcome = LoginCom.login(user, UIUtil.getPassword(fld_pass));
				result = loginOutcome.getResult();
				if (result) {
					outcome.setResult(true);
					ViewerState.goOnline(server, Integer.parseInt(port));

					// TODO relocate
					if (!server.equals("127.0.0.1")) {
						// update recents
						try {
							String successfulLogin = server + ":" + port;
							ArrayList<String> recents = (ArrayList<String>) DatabaseStore.getDatabase()
									.getObject("login.recents");
							recents.remove(successfulLogin);
							recents.add(successfulLogin);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
					ConnectionStore.closeAll();
					if (loginOutcome.hasComment())
						outcome.setComment(loginOutcome.getComment());

				}

				return outcome.setTime(System.currentTimeMillis() - t1).build();
			}

			protected void done() {
				try {

					Outcome outcome = get();

					lbl_status.unfreeze();
					if (outcome.getResult()) {
						lbl_status.setGood("Login completed in: " + outcome.getTime() + "ms");
						new SwingWorker<Void, Void>() {

							@Override
							protected Void doInBackground() throws Exception {
								Thread.sleep(650);
								return null;
							}

							protected void done() {
								synchronized (parent) {
									parent.notifyAll();
								}
								parent.dispose();
							}

						}.execute();

					} else {
						lbl_status.setBad(outcome.getComment());
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					endLogin();
				}

			};
		}.execute();

	}

	private boolean testValues(String server, String port, String user) {
		removeErrorBorders();
		if (!Validation.port(port)) {
			setPortError();
			return false;
		}
		if (!Validation.password(fld_pass)) {
			setPassError();
			return false;
		}
		if (!Validation.username(user)) {
			setUserError();
			return false;
		}
		if (!Validation.dns(server) && !Validation.ip(server)) {
			setServerError();
			return false;
		}
		return true;
	}

	private void startLogin() {
		btn_login.setEnabled(false);
		fld_pass.setEnabled(false);
		fld_port.setEnabled(false);
		fld_address.setEnabled(false);
		fld_user.setEnabled(false);
		lbl_address.setEnabled(false);
		lbl_port.setEnabled(false);
		lbl_pass.setEnabled(false);
		lbl_user.setEnabled(false);

		lbl_status.setInfo("Attempting Connection");
		lbl_status.freeze();
	}

	private void endLogin() {

		btn_login.setEnabled(true);
		fld_pass.setEnabled(true);
		fld_port.setEnabled(true);
		fld_address.setEnabled(true);
		fld_user.setEnabled(true);
		lbl_address.setEnabled(true);
		lbl_port.setEnabled(true);
		lbl_pass.setEnabled(true);
		lbl_user.setEnabled(true);

		lbl_status.unfreeze();

	}

	private void removeErrorBorders() {
		fld_address.setBorder(fld_server_border);
		fld_port.setBorder(fld_port_border);
		fld_user.setBorder(fld_user_border);
		fld_pass.setBorder(fld_password_border);
	}

	private void setServerError() {
		fld_address.setBorder(new LineBorder(Color.RED));
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
		fld_pass.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid password");
	}

	public void addRecents(boolean localServer) {
		ArrayList<String> r = new ArrayList<String>();
		try {
			r.addAll((ArrayList<String>) DatabaseStore.getDatabase().getObject("login.recents"));
		} catch (Exception e) {
			log.error("Failed to load recent connections");
			e.printStackTrace();
			return;
		}

		r.add(0, "Live Example Server");
		if (localServer) {
			r.add(0, "Local Server");
		}

		String[] recent = new String[r.size()];

		for (int i = 0; i < r.size(); i++) {
			recent[i] = r.get(i);
		}

		fld_address.setModel(new DefaultComboBoxModel<String>(recent));
		fld_address.setSelectedIndex(-1);
	}
}
