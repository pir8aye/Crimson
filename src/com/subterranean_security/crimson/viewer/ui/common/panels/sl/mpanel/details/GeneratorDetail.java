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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.about.AboutDialog;

public class GeneratorDetail extends JPanel {

	private static final long serialVersionUID = 1L;

	private MPanel parent;

	public GeneratorDetail(MPanel mp) {
		parent = mp;

		init();
		initValues();

	}

	private void init() {
		setLayout(null);
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 39, 104, 88);
		add(panel);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 8, 104, 21);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblInterface = new JLabel("About");
		lblInterface.setHorizontalAlignment(SwingConstants.CENTER);
		lblInterface.setIcon(UIUtil.getIcon("c-16.png"));
		panel_1.add(lblInterface);

		JButton btnLogs = new JButton(UIUtil.getIcon("icons16/general/error_log.png"));
		btnLogs.setText("Logs");
		btnLogs.setFont(new Font("Dialog", Font.BOLD, 10));
		btnLogs.setLocation(8, 56);
		btnLogs.setSize(88, 20);
		btnLogs.setFocusable(false);
		panel.add(btnLogs);
		btnLogs.addActionListener(e -> {
			parent.drop();
		});
		btnLogs.setMargin(new Insets(2, 4, 2, 4));

		JButton btnSerialKey = new JButton(UIUtil.getIcon("icons16/general/barcode_2d.png"));
		btnSerialKey.setText("Serial Key");
		btnSerialKey.setFont(new Font("Dialog", Font.BOLD, 10));
		btnSerialKey.setLocation(8, 32);
		btnSerialKey.setSize(88, 20);
		btnSerialKey.setFocusable(false);
		btnSerialKey.addActionListener(e -> {
		});
		btnSerialKey.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btnSerialKey);

		JButton btnAbout = new JButton(UIUtil.getIcon("c-16.png"));
		btnAbout.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAbout.setText("About");
		btnAbout.setLocation(8, 8);
		btnAbout.setSize(88, 20);
		btnAbout.setFocusable(false);
		btnAbout.addActionListener(e -> {
			// TODO DONT allow multiple instances
			AboutDialog ad = new AboutDialog();
			ad.setVisible(true);

			parent.drop();
		});
		btnAbout.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btnAbout);
	}

	private void initValues() {

	}
}
