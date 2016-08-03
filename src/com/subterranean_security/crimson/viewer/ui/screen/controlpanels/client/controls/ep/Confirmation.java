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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls.ep;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.controls.ControlsTab;

public class Confirmation extends JPanel {

	private static final long serialVersionUID = 1L;

	private boolean result = false;

	public boolean getResult() {
		return result;
	}

	public Confirmation(EPanel ep, ControlsTab parent, String message) {

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setPreferredSize(new Dimension(300, 67));
		add(panel);
		panel.setLayout(null);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = false;
				parent.notifyConfirmation();
				ep.drop();
			}
		});
		btnCancel.setMargin(new Insets(2, 4, 2, 4));
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		btnCancel.setBounds(48, 40, 68, 21);
		panel.add(btnCancel);

		JButton btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = true;
				parent.notifyConfirmation();
				ep.drop();
			}
		});
		btnContinue.setMargin(new Insets(2, 4, 2, 4));
		btnContinue.setFont(new Font("Dialog", Font.BOLD, 10));
		btnContinue.setBounds(184, 40, 68, 21);
		panel.add(btnContinue);

		JLabel lblAreYouSure = new JLabel("** ARE YOU SURE? **");
		lblAreYouSure.setHorizontalAlignment(SwingConstants.CENTER);
		lblAreYouSure.setFont(new Font("Dialog", Font.BOLD, 9));
		lblAreYouSure.setBounds(76, 0, 148, 15);
		panel.add(lblAreYouSure);

		JLabel lblNewLabel = new JLabel(message);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewLabel.setBounds(12, 17, 276, 15);
		panel.add(lblNewLabel);

	}
}
