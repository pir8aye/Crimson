/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.util.CertUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.cv.net.command.CvidCom;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RQ_Ping;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_ServerInfo;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.command.LoginCom;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.FieldLimiter;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;

public class UserSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// parent dialog
	private LoginDialog parent;

	private JPanel panel_credentials;

	private JLabel lblCertificate;
	private JLabel lbl_user;
	private JLabel lbl_pass;

	private JTextField fld_user;
	private JPasswordField fld_pass;

	private StatusLabel lbl_status;

	// the final result of the login
	public boolean result = false;
	private boolean mouseOverStatus = true;

	private Connector connection;
	private RS_ServerInfo serverInfo;

	// value labels
	private JLabel lbl_version;
	private JLabel lbl_banner;
	private JLabel lbl_ping;
	private JLabel lbl_server_ip;
	private JLabel lbl_certificate;

	// true if the banner image was changed as this panel was loading
	private boolean bannerChanged;

	private Timer pinger;

	public UserSelectionPanel(LoginDialog parent) {
		this.parent = parent;
		init();
		initPreFill();

	}

	private void init() {
		setLayout(null);

		lbl_status = new StatusLabel("Enter user credentials to login");
		lbl_status.setVisible(true);
		lbl_status.setBounds(12, 125, 376, 15);
		add(lbl_status);

		panel_credentials = new JPanel();
		panel_credentials.setBounds(6, 65, 388, 55);
		add(panel_credentials);
		panel_credentials.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Credentials",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel_credentials.setLayout(null);

		fld_user = new JTextField();
		fld_user.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_user.setDocument(new FieldLimiter(60));
		fld_user.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setInfo("username to an account on the server");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setDefault();
			}
		});
		fld_user.setHorizontalAlignment(SwingConstants.CENTER);
		fld_user.setBounds(20, 28, 140, 19);
		panel_credentials.add(fld_user);
		fld_user.setColumns(10);

		fld_pass = new JPasswordField();
		fld_pass.setFont(new Font("Dialog", Font.PLAIN, 8));
		fld_pass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setInfo("corresponding case-sensitive password");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setDefault();
			}
		});
		fld_pass.setDocument(new FieldLimiter(64));
		fld_pass.setHorizontalAlignment(SwingConstants.CENTER);
		fld_pass.setBounds(228, 28, 140, 19);
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

		JLabel lblBanner = new JLabel("Banner:");
		lblBanner.setIcon(UIUtil.getIcon("icons16/general/flag_blue.png"));
		lblBanner.setFont(new Font("Dialog", Font.BOLD, 10));
		lblBanner.setBounds(12, 40, 83, 15);
		add(lblBanner);

		JLabel lblServerIp = new JLabel("Server IP:");
		lblServerIp.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		lblServerIp.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerIp.setBounds(12, 6, 83, 15);
		add(lblServerIp);

		JLabel lblPing = new JLabel("Message Ping:");
		lblPing.setIcon(UIUtil.getIcon("icons16/general/speedometer.png"));
		lblPing.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPing.setBounds(226, 6, 114, 15);
		add(lblPing);

		lblCertificate = new JLabel("Certificate:");
		lblCertificate.setIcon(UIUtil.getIcon("icons16/general/ssl_certificate.png"));
		lblCertificate.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCertificate.setBounds(12, 23, 83, 15);
		add(lblCertificate);

		JLabel lblVersion = new JLabel("Server Version:");
		lblVersion.setIcon(UIUtil.getIcon("c-16.png"));
		lblVersion.setFont(new Font("Dialog", Font.BOLD, 10));
		lblVersion.setBounds(226, 23, 114, 15);
		add(lblVersion);

		lbl_server_ip = new JLabel();
		lbl_server_ip.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_server_ip.setBounds(107, 6, 101, 15);
		add(lbl_server_ip);

		lbl_certificate = new JLabel();
		lbl_certificate.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_certificate.setBounds(107, 23, 66, 15);
		add(lbl_certificate);

		lbl_ping = new JLabel();
		lbl_ping.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_ping.setBounds(344, 6, 50, 15);
		add(lbl_ping);

		lbl_version = new JLabel();
		lbl_version.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_version.setBounds(344, 23, 50, 15);
		add(lbl_version);

		lbl_banner = new JLabel();
		lbl_banner.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_banner.setBounds(107, 40, 292, 15);
		add(lbl_banner);
	}

	public void initButtons() {
		parent.resetButtons();

		parent.getBack().setText("Back");
		parent.getBack().addActionListener(e -> {
			stopPinger();
			parent.getServerSelectionPanel().reset();

			// close the connection
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					ConnectionStore.closeAll();
					return null;
				}

				protected void done() {
				};
			}.execute();

			if (bannerChanged) {
				parent.resetLogo();
				bannerChanged = false;
			}

			parent.showServerSelectionPanel();
			parent.getServerSelectionPanel().initButtons();
		});

		parent.getNext().setText("Login");
		parent.getNext().addActionListener(e -> {
			disableControls();

			if (!testValues()) {
				enableControls();
				return;
			}

			new SwingWorker<Outcome, Void>() {

				@Override
				protected Outcome doInBackground() throws Exception {
					Outcome.Builder outcome = Outcome.newBuilder().setTime(System.currentTimeMillis()).setResult(false);

					CvidCom.getCvid(connection);
					ProfileStore.initialize(LcvidStore.lcvid);

					// request login from server
					Outcome loginOutcome = LoginCom.login(fld_user.getText(), UIUtil.getPassword(fld_pass));
					result = loginOutcome.getResult();
					if (result) {
						outcome.setResult(true);
						ViewerState.goOnline(parent.getServerSelectionPanel().getServer(),
								parent.getServerSelectionPanel().getPort());

					} else {
						if (!loginOutcome.getComment().isEmpty())
							outcome.setComment(loginOutcome.getComment());

					}

					return outcome.setTime(System.currentTimeMillis() - outcome.getTime()).build();
				}

				protected void done() {
					try {
						Outcome outcome = get();

						if (outcome.getResult()) {
							lbl_status.setGood("Login completed in: " + outcome.getTime() + "ms");
							new SwingWorker<Void, Void>() {

								@Override
								protected Void doInBackground() throws Exception {
									stopPinger();
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
						enableControls();
					}

				};
			}.execute();

		});
	}

	private void setUserError() {
		fld_user.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid username");
	}

	private void setPassError() {
		fld_pass.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid password");
	}

	private void removeErrorBorders() {
		fld_user.setBorder(new JTextField().getBorder());
		fld_pass.setBorder(new JPasswordField().getBorder());
	}

	private boolean testValues() {
		removeErrorBorders();
		if (!ValidationUtil.password(fld_pass)) {
			setPassError();
			return false;
		}
		if (!ValidationUtil.username(fld_user.getText())) {
			setUserError();
			return false;
		}
		return true;
	}

	/**
	 * Disable menu controls
	 */
	private void disableControls() {
		mouseOverStatus = false;

		parent.getBack().setEnabled(false);
		parent.getNext().setEnabled(false);
		fld_pass.setEnabled(false);
		fld_user.setEnabled(false);
		lbl_pass.setEnabled(false);
		lbl_user.setEnabled(false);

	}

	/**
	 * Enable menu controls
	 */
	private void enableControls() {
		mouseOverStatus = true;

		parent.getBack().setEnabled(true);
		parent.getNext().setEnabled(true);
		fld_pass.setEnabled(true);
		fld_user.setEnabled(true);
		lbl_pass.setEnabled(true);
		lbl_user.setEnabled(true);

	}

	public void updateCertificate() {
		String tooltip = "";

		switch (connection.getCertState()) {
		case REFUSED:
			break;
		case INVALID:
			tooltip = "The server supplied an invalid certificate.\nThe identity of this server cannot be verified!";
			lbl_certificate.setText("INVALID");
			lbl_certificate.setForeground(StatusLabel.bad);
			break;
		case VALID:
			try {
				tooltip = CertUtil.certificateToHtml(connection.getPeerCertificate());
			} catch (IOException e) {
				tooltip = "Failed to parse certificate!";
			}

			lbl_certificate.setText("VALID");
			lbl_certificate.setForeground(StatusLabel.good);
			break;
		default:
			break;

		}

		lbl_certificate.setToolTipText(tooltip);
		lblCertificate.setToolTipText(tooltip);
	}

	/**
	 * Launch a thread which pings the server. The thread is self-terminated
	 * after 1000 seconds to reduce network load.
	 */
	private void startPinger() {
		Message rq = Message.newBuilder().setRqPing(RQ_Ping.newBuilder()).build();

		pinger = new Timer(2000, new ActionListener() {
			private int iterations = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (iterations++ > 500) {
					lbl_ping.setText("N/A");
					stopPinger();
					return;
				}
				long p1 = System.currentTimeMillis();
				connection.writeAndGetResponse(rq);
				long p2 = System.currentTimeMillis();
				lbl_ping.setText(((p2 - p1) / 2) + " ms");
			}
		});
		pinger.setRepeats(true);
		pinger.start();

	}

	/**
	 * Kill the pinging thread
	 */
	private void stopPinger() {
		if (pinger != null) {
			pinger.stop();
			pinger = null;
		}
	}

	public void setServerInfo(Connector connection, RS_ServerInfo serverInfo) {
		if (serverInfo == null)
			throw new IllegalArgumentException();
		if (connection == null)
			throw new IllegalArgumentException();

		// save the connection for the upcoming login attempt
		this.connection = connection;
		this.serverInfo = serverInfo;

		// set server ip
		lbl_server_ip.setText(connection.getRemoteIP());

		// set server version
		if (!serverInfo.getVersion().isEmpty()) {
			lbl_version.setText(serverInfo.getVersion());
		}

		// set banner text
		if (!serverInfo.getBanner().isEmpty()) {
			lbl_banner.setText(serverInfo.getBanner());
		}

		// set banner image
		if (!serverInfo.getBannerImage().isEmpty()) {
			try (InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(serverInfo.getBannerImage()))) {
				parent.getLogo().fadeImage(new ImageIcon(ImageIO.read(in)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bannerChanged = true;
		}

		// set certificate details
		updateCertificate();

		// start updating message ping
		startPinger();

	}

	/**
	 * Pre-fill fields to speed up testing
	 */
	private void initPreFill() {
		fld_user.setText(System.getProperty("debug.prefill.username", ""));
		fld_pass.setText(System.getProperty("debug.prefill.password", ""));
	}
}
