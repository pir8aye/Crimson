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
package com.subterranean_security.crimson.viewer.ui.screen.serials;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.SMSG.RS_RetrieveKeys;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.services.Services;

public class KeyLookup extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public String key = "";
	private StatusLabel sl;
	private JButton btnActivate;
	private JButton btnBack;
	private JLabel remainingAlpha;
	private JLabel remainingAdvanced;
	private JRadioButton rdbtnAlpha;
	private JRadioButton rdbtnAdvanced;

	private RS_RetrieveKeys rs = null;
	private JTextField fld_email;
	private JPasswordField fld_pass;

	public KeyLookup() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Serial Lookup");
		setResizable(false);
		setBounds(100, 100, 236, 298);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		ButtonGroup bg = new ButtonGroup();

		sl = new StatusLabel("enter account details to lookup key");

		sl.setBounds(8, 210, 206, 16);
		contentPanel.add(sl);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UICommon.basic, "Account Details", TitledBorder.LEADING, TitledBorder.TOP,
				null, new Color(51, 51, 51)));
		panel.setBounds(8, 12, 206, 68);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Dialog", Font.BOLD, 10));
		lblEmail.setBounds(12, 20, 35, 17);
		panel.add(lblEmail);

		fld_email = new JTextField();
		fld_email.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_email.setBounds(46, 19, 150, 17);
		panel.add(fld_email);
		fld_email.setColumns(10);

		JLabel lblPassword = new JLabel("Pass:");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPassword.setBounds(12, 39, 35, 17);
		panel.add(lblPassword);

		fld_pass = new JPasswordField();
		fld_pass.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_pass.setBounds(46, 39, 97, 17);
		panel.add(fld_pass);

		JButton btnLookup = new JButton("Lookup");
		btnLookup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String user = fld_email.getText();
				if (!ValidationUtil.email(user)) {
					sl.setBad("INVALID EMAIL");
					return;
				}
				if (!ValidationUtil.password(fld_pass)) {
					sl.setBad("INVALID PASSWORD");
					return;
				}

				sl.setInfo("Querying server");

				fld_email.setEnabled(false);
				fld_pass.setEnabled(false);
				btnLookup.setEnabled(false);

				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						rs = Services.getKeys(user, UIUtil.getPassword(fld_pass));
						return null;
					}

					protected void done() {
						if (rs == null || !rs.getResult()) {
							remainingAlpha.setText("Login Failed!");
							remainingAdvanced.setText("Login Failed!");
							sl.setBad(rs == null ? "Connection Failed"
									: (rs.hasComment() ? rs.getComment() : "Unknown Error"));
							btnActivate.setEnabled(false);
							fld_email.setEnabled(true);
							fld_pass.setEnabled(true);
							btnLookup.setEnabled(true);
							return;
						} else {
							remainingAlpha.setText(rs.getAlphaKeyCount() + " left");
						}

						if (rs.getAlphaKeyCount() != 0) {
							rdbtnAlpha.setEnabled(true);
							remainingAlpha.setEnabled(true);
							btnActivate.setEnabled(true);
							sl.setGood("Retrieved: " + rs.getAlphaKeyCount() + " keys for user");
						} else {
							btnActivate.setEnabled(false);
							sl.setBad("No keys found!");
						}

					};

				}.execute();

			}
		});
		btnLookup.setMargin(new Insets(2, 4, 2, 4));
		btnLookup.setFont(new Font("Dialog", Font.BOLD, 9));
		btnLookup.setBounds(146, 39, 50, 17);
		panel.add(btnLookup);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(UICommon.basic, "Select Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(8, 92, 206, 106);
		contentPanel.add(panel_1);
		panel_1.setLayout(null);

		rdbtnAlpha = new JRadioButton("ALPHA Edition");
		rdbtnAlpha.setFont(new Font("Dialog", Font.BOLD, 11));
		rdbtnAlpha.setBounds(7, 19, 139, 24);
		panel_1.add(rdbtnAlpha);
		rdbtnAlpha.setEnabled(false);
		bg.add(rdbtnAlpha);

		remainingAlpha = new JLabel("Unlocks Crimson ALPHA");
		remainingAlpha.setBounds(29, 41, 177, 16);
		panel_1.add(remainingAlpha);
		remainingAlpha.setForeground(Color.GRAY);
		remainingAlpha.setFont(new Font("Dialog", Font.BOLD, 10));

		rdbtnAdvanced = new JRadioButton("Advanced Edition");
		rdbtnAdvanced.setFont(new Font("Dialog", Font.BOLD, 11));
		rdbtnAdvanced.setBounds(7, 60, 175, 24);
		panel_1.add(rdbtnAdvanced);
		rdbtnAdvanced.setEnabled(false);
		bg.add(rdbtnAdvanced);

		remainingAdvanced = new JLabel("This edition is not available");
		remainingAdvanced.setBounds(29, 82, 177, 16);
		panel_1.add(remainingAdvanced);
		remainingAdvanced.setForeground(Color.GRAY);
		remainingAdvanced.setFont(new Font("Dialog", Font.BOLD, 10));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			btnBack = new JButton("Back");
			btnBack.setFont(new Font("Dialog", Font.BOLD, 10));
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					key = "";
					dispose();
				}
			});
			btnBack.setMargin(new Insets(2, 6, 2, 6));
			buttonPane.add(btnBack);
			{
				btnActivate = new JButton("Use Key");
				btnActivate.setFont(new Font("Dialog", Font.BOLD, 10));
				btnActivate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (rdbtnAlpha.isSelected()) {
							key = rs.getAlphaKey();
						} else if (rdbtnAdvanced.isSelected()) {
							// key = rs.getBusinessKeys(0);// just take first
							// key
						} else {
							key = "";
						}
						dispose();
					}
				});
				btnActivate.setEnabled(false);
				btnActivate.setMargin(new Insets(2, 6, 2, 6));
				buttonPane.add(btnActivate);
				getRootPane().setDefaultButton(btnActivate);
			}
		}

	}

	@Override
	public void dispose() {
		synchronized (this) {
			this.notifyAll();
		}
		super.dispose();
	}
}
