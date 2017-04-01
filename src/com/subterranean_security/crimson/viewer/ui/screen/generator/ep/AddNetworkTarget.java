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
package com.subterranean_security.crimson.viewer.ui.screen.generator.ep;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.Validation;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.generator.tabs.NTab;

import javax.swing.border.BevelBorder;

public class AddNetworkTarget extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;

	private EPanel ep;
	private NTab parent;
	private StatusLabel lbl_status;

	public AddNetworkTarget(EPanel ep, NTab parent) {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.ep = ep;
		this.parent = parent;
		init();
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		add(panel_1);

		JPanel panel = new JPanel();
		panel_1.add(panel);
		panel.setPreferredSize(new Dimension(340, 60));
		panel.setLayout(null);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(72, 9, 246, 17);
		panel.add(textField);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(72, 32, 47, 17);
		panel.add(textField_1);

		JLabel label = new JLabel("Port:");
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setBounds(12, 34, 53, 15);
		panel.add(label);

		JLabel label_1 = new JLabel("Server:");
		label_1.setFont(new Font("Dialog", Font.BOLD, 10));
		label_1.setBounds(12, 12, 53, 15);
		panel.add(label_1);

		lbl_status = new StatusLabel("Add new network target");
		add(lbl_status, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ep.drop();
			}
		});
		btnClose.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(btnClose);

		JButton btnAddTarget = new JButton("Add Target");
		btnAddTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (verify()) {
					parent.table.add(NetworkTarget.newBuilder().setServer(textField.getText())
							.setPort(Integer.parseInt(textField_1.getText())).build());
					ep.drop();
				}
			}
		});
		btnAddTarget.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(btnAddTarget);

	}

	private boolean verify() {
		if (!Validation.dns(textField.getText()) && !Validation.ip(textField.getText())) {
			lbl_status.setBad("Invalid server address");
			return false;
		}
		if (!Validation.port(textField_1.getText())) {
			lbl_status.setBad("Invalid port");
			return false;
		}

		return true;
	}

}
