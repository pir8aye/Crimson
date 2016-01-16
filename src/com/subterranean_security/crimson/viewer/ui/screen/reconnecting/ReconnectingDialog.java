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
package com.subterranean_security.crimson.viewer.ui.screen.reconnecting;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class ReconnectingDialog extends JDialog {

	private static final long	serialVersionUID	= 1L;
	private final JPanel		contentPanel		= new JPanel();

	public ReconnectingDialog() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 182);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 440, 152);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(12, 55, 416, 14);
		contentPanel.add(progressBar);

		JLabel lblCrimsonLostConnection = new JLabel("Connection to server lost");
		lblCrimsonLostConnection.setHorizontalAlignment(SwingConstants.CENTER);
		lblCrimsonLostConnection.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCrimsonLostConnection.setBounds(12, 12, 416, 15);
		contentPanel.add(lblCrimsonLostConnection);

		JLabel lblStatus = new JLabel("status");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Dialog", Font.BOLD, 10));
		lblStatus.setBounds(12, 81, 416, 15);
		contentPanel.add(lblStatus);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 117, 440, 35);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			{
				JButton okButton = new JButton("Quit");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
