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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.ui.FieldLimiter;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.Viewer;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.services.Services;

public class AddSerial extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField txtQ1;
	private JTextField txtQ2;
	private JTextField txtQ3;
	private JTextField txtQ4;
	private JButton btnApply;
	private JButton btnLookup;
	private JButton btnBuy;
	private StatusLabel sl;
	private JButton btnClose;

	private EPanel ep;

	public AddSerial(EPanel ep) {
		this.ep = ep;
		init();
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		add(panel_1);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.add(panel);
		panel.setPreferredSize(new Dimension(500, 70));
		panel.setLayout(null);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ep.drop();
			}
		});
		btnClose.setMargin(new Insets(2, 4, 2, 4));
		btnClose.setFont(new Font("Dialog", Font.BOLD, 10));
		btnClose.setBounds(220, 37, 60, 20);
		panel.add(btnClose);

		txtQ1 = new JTextField();
		txtQ1.setDocument(new FieldLimiter(4, true));
		txtQ1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				refreshKey();

				// move to next box
				if (txtQ1.getText().length() == 4) {
					txtQ2.requestFocus();
				}
			}
		});
		txtQ1.setHorizontalAlignment(SwingConstants.CENTER);
		txtQ1.setFont(new Font("Dialog", Font.BOLD, 10));
		txtQ1.setBounds(135, 11, 50, 19);
		panel.add(txtQ1);
		txtQ1.setColumns(10);

		txtQ2 = new JTextField();
		txtQ2.setDocument(new FieldLimiter(4, true));
		txtQ2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshKey();

				// move to next box
				if (txtQ2.getText().length() == 4) {
					txtQ3.requestFocus();
				}
			}
		});
		txtQ2.setHorizontalAlignment(SwingConstants.CENTER);
		txtQ2.setFont(new Font("Dialog", Font.BOLD, 10));
		txtQ2.setBounds(195, 11, 50, 19);
		panel.add(txtQ2);
		txtQ2.setColumns(10);

		txtQ3 = new JTextField();
		txtQ3.setDocument(new FieldLimiter(4, true));
		txtQ3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshKey();

				// move to next box
				if (txtQ3.getText().length() == 4) {
					txtQ4.requestFocus();
				}
			}
		});
		txtQ3.setFont(new Font("Dialog", Font.BOLD, 10));
		txtQ3.setHorizontalAlignment(SwingConstants.CENTER);
		txtQ3.setBounds(255, 11, 50, 19);
		panel.add(txtQ3);
		txtQ3.setColumns(10);

		txtQ4 = new JTextField();
		txtQ4.setDocument(new FieldLimiter(4, true));
		txtQ4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshKey();
			}
		});
		txtQ4.setHorizontalAlignment(SwingConstants.CENTER);
		txtQ4.setFont(new Font("Dialog", Font.BOLD, 10));
		txtQ4.setBounds(315, 11, 50, 19);
		panel.add(txtQ4);
		txtQ4.setColumns(10);

		JLabel label = new JLabel("-");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setBounds(185, 11, 10, 19);
		panel.add(label);

		JLabel label_1 = new JLabel("-");
		label_1.setFont(new Font("Dialog", Font.BOLD, 10));
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(245, 11, 10, 19);
		panel.add(label_1);

		JLabel label_2 = new JLabel("-");
		label_2.setFont(new Font("Dialog", Font.BOLD, 10));
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(305, 11, 10, 19);
		panel.add(label_2);

		btnLookup = new JButton("Lookup");
		btnLookup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				sl.setInfo("Lookup your key using account credentials");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sl.setDefault();
			}
		});
		btnLookup.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				btnLookup.setEnabled(false);
				btnClose.setEnabled(false);
				btnBuy.setEnabled(false);
				btnApply.setEnabled(false);
				txtQ1.setEnabled(false);
				txtQ2.setEnabled(false);
				txtQ3.setEnabled(false);
				txtQ4.setEnabled(false);

				KeyLookup kv = new KeyLookup();
				kv.setLocationRelativeTo(null);
				kv.setVisible(true);

				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						synchronized (kv) {
							kv.wait();
						}

						return null;
					}

					@Override
					protected void done() {
						if (kv.key != null && kv.key.length() == 16) {
							txtQ1.setText(kv.key.substring(0, 4));
							txtQ2.setText(kv.key.substring(4, 8));
							txtQ3.setText(kv.key.substring(8, 12));
							txtQ4.setText(kv.key.substring(12, 16));
						}

						refreshKey();
						btnLookup.setEnabled(true);
						btnClose.setEnabled(true);
						btnBuy.setEnabled(true);
						btnApply.setEnabled(true);

						txtQ1.setEnabled(true);
						txtQ2.setEnabled(true);
						txtQ3.setEnabled(true);
						txtQ4.setEnabled(true);
					};

				}.execute();
			}
		});
		btnLookup.setMargin(new Insets(2, 4, 2, 4));
		btnLookup.setFont(new Font("Dialog", Font.BOLD, 10));
		btnLookup.setBounds(375, 11, 55, 19);
		panel.add(btnLookup);

		JLabel lblCode = new JLabel("Code:");
		lblCode.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCode.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCode.setBounds(75, 11, 50, 19);
		panel.add(lblCode);

		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				sl.setInfo("Activating key");
				sl.freeze();
				btnBuy.setEnabled(false);
				btnApply.setEnabled(false);
				btnClose.setText("Cancel");

				txtQ1.setEnabled(false);
				txtQ2.setEnabled(false);
				txtQ3.setEnabled(false);
				txtQ4.setEnabled(false);

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						if (!CUtil.Validation.serial(getKey())) {
							return Outcome.newBuilder().setResult(false).setComment("Invalid key").build();
						}
						return Services.activate(getKey());
					}

					@Override
					protected void done() {
						sl.unfreeze();
						try {
							Outcome outcome = get();
							if (outcome.getResult()) {
								sl.setGood("Success!");
								new SwingWorker<Void, Void>() {

									@Override
									protected Void doInBackground() throws Exception {
										ViewerStore.Databases.local.storeObject("serial", getKey());
										Viewer.loadState();
										Thread.sleep(1000);
										return null;
									}

									@Override
									protected void done() {
										ep.drop();
									}

								}.execute();
							} else {
								sl.setBad("Activation failed: " + outcome.getComment());
							}
						} catch (ExecutionException | InterruptedException e) {
							sl.setBad("Error: " + e.getMessage());
						}

						btnBuy.setEnabled(true);
						btnApply.setEnabled(true);
						btnClose.setText("Close");

						txtQ1.setEnabled(true);
						txtQ2.setEnabled(true);
						txtQ3.setEnabled(true);
						txtQ4.setEnabled(true);

					};

				}.execute();
			}
		});
		btnApply.setEnabled(false);
		btnApply.setMargin(new Insets(2, 4, 2, 4));
		btnApply.setFont(new Font("Dialog", Font.BOLD, 10));
		btnApply.setBounds(292, 37, 60, 20);
		panel.add(btnApply);

		btnBuy = new JButton("Buy");
		btnBuy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				sl.setInfo("Open the website to buy a key");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sl.setDefault();
			}
		});
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						Desktop.getDesktop().browse(new URI(
								"https://store.subterranean-security.com/index.php?route=product/product&path=17_59&product_id=50"));
						return null;
					}

				}.execute();
			}
		});
		btnBuy.setMargin(new Insets(2, 4, 2, 4));
		btnBuy.setFont(new Font("Dialog", Font.BOLD, 10));
		btnBuy.setBounds(148, 37, 60, 20);
		panel.add(btnBuy);

		JPanel top = new JPanel();
		top.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		top.setLayout(new BorderLayout());
		add(top, BorderLayout.NORTH);

		sl = new StatusLabel("Enter your serial code to activate this Crimson instance");
		top.add(sl, BorderLayout.CENTER);

	}

	private String getKey() {
		return txtQ1.getText() + txtQ2.getText() + txtQ3.getText() + txtQ4.getText();
	}

	private void refreshKey() {
		btnApply.setEnabled(CUtil.Validation.serial(getKey()));
	}
}
