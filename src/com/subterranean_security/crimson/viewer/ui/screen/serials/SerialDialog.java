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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SerialDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	public SerialDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 286, 172);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new CardLayout(0, 0));
		{
			JPanel trial_panel = new JPanel();
			contentPanel.add(trial_panel, "name_5026108607769");
			trial_panel.setLayout(null);
			
			JLabel lblCrimsonTrialEdition = new JLabel("Crimson Trial Edition");
			lblCrimsonTrialEdition.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			lblCrimsonTrialEdition.setHorizontalAlignment(SwingConstants.CENTER);
			lblCrimsonTrialEdition.setBounds(12, 12, 242, 17);
			trial_panel.add(lblCrimsonTrialEdition);
			
			textField = new JTextField();
			textField.setBounds(79, 49, 175, 21);
			trial_panel.add(textField);
			textField.setColumns(10);
			
			JLabel lblSerial = new JLabel("Serial:");
			lblSerial.setBounds(12, 51, 55, 17);
			trial_panel.add(lblSerial);
		}
		{
			JPanel full_panel = new JPanel();
			contentPanel.add(full_panel, "name_5030240542140");
			full_panel.setLayout(null);
			
			JButton btnDeactivate = new JButton("Deactivate");
			btnDeactivate.setFont(new Font("Dialog", Font.BOLD, 11));
			btnDeactivate.setBounds(110, 70, 98, 20);
			full_panel.add(btnDeactivate);
			
			JLabel lblCrimsonEssentialEdition = new JLabel("Crimson Essential Edition");
			lblCrimsonEssentialEdition.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			lblCrimsonEssentialEdition.setHorizontalAlignment(SwingConstants.CENTER);
			lblCrimsonEssentialEdition.setBounds(12, 12, 242, 17);
			full_panel.add(lblCrimsonEssentialEdition);
			
			JLabel lblSerialCodeJkssdjkl = new JLabel("Serial Code: JKS2-SD89-4J22-KL64");
			lblSerialCodeJkssdjkl.setBounds(12, 41, 242, 17);
			full_panel.add(lblSerialCodeJkssdjkl);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}

		}
	}
}
