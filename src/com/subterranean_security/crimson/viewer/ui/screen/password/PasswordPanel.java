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
package com.subterranean_security.crimson.viewer.ui.screen.password;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.subterranean_security.crimson.viewer.ui.component.StatusLights;

public class PasswordPanel extends JPanel {

	private static final long	serialVersionUID	= 1L;
	private JPasswordField		fld_old;
	private JPasswordField		fld_new;
	private JPasswordField		fld_retype;
	private StatusLights		stl_strength;
	public JButton				btn_ok;
	public JButton				btn_cancel;

	private PasswordDialog		parent;

	public PasswordPanel(boolean change, PasswordDialog pd) {
		parent = pd;
		btn_cancel = new JButton("Cancel");
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parent.dispose();
			}
		});

		btn_ok = new JButton(change ? "Change" : "Create");
		btn_ok.setEnabled(false);

		JPanel main = new JPanel();
		GridBagLayout gbl_main = new GridBagLayout();
		gbl_main.columnWidths = new int[] { 300, 0 };
		gbl_main.rowHeights = new int[] { 25, 25, 25, 170, 0 };
		gbl_main.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_main.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		main.setLayout(gbl_main);

		JPanel oldPanel = new JPanel();
		oldPanel.setVisible(change);
		GridBagConstraints gbc_oldPanel = new GridBagConstraints();
		gbc_oldPanel.fill = GridBagConstraints.BOTH;
		gbc_oldPanel.insets = new Insets(0, 0, 0, 0);
		gbc_oldPanel.gridx = 0;
		gbc_oldPanel.gridy = 0;
		main.add(oldPanel, gbc_oldPanel);

		JLabel lblOldPassword = new JLabel("Old Password:");
		lblOldPassword.setPreferredSize(new Dimension(120, 15));
		oldPanel.add(lblOldPassword);
		lblOldPassword.setHorizontalAlignment(SwingConstants.TRAILING);

		fld_old = new JPasswordField();
		fld_old.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refresh();
			}
		});
		fld_old.setPreferredSize(new Dimension(100, 19));
		oldPanel.add(fld_old);

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(7, 0));
		oldPanel.add(separator);

		JPanel newPanel = new JPanel();
		GridBagConstraints gbc_newPanel = new GridBagConstraints();
		gbc_newPanel.fill = GridBagConstraints.BOTH;
		gbc_newPanel.insets = new Insets(0, 0, 0, 0);
		gbc_newPanel.gridx = 0;
		gbc_newPanel.gridy = 1;
		main.add(newPanel, gbc_newPanel);

		JLabel lblNewPassword = new JLabel("New Password:");
		lblNewPassword.setPreferredSize(new Dimension(120, 15));
		newPanel.add(lblNewPassword);
		lblNewPassword.setHorizontalAlignment(SwingConstants.TRAILING);

		fld_new = new JPasswordField();
		fld_new.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refresh();
			}
		});
		fld_new.setPreferredSize(new Dimension(100, 19));
		newPanel.add(fld_new);

		stl_strength = new StatusLights();
		stl_strength.setPreferredSize(new Dimension(7, 20));
		newPanel.add(stl_strength);

		JPanel retypePanel = new JPanel();
		GridBagConstraints gbc_retypePanel = new GridBagConstraints();
		gbc_retypePanel.fill = GridBagConstraints.BOTH;
		gbc_retypePanel.insets = new Insets(0, 0, 0, 0);
		gbc_retypePanel.gridx = 0;
		gbc_retypePanel.gridy = 2;
		main.add(retypePanel, gbc_retypePanel);

		JLabel lblRetype = new JLabel("Retype:");
		lblRetype.setPreferredSize(new Dimension(120, 15));
		retypePanel.add(lblRetype);
		lblRetype.setHorizontalAlignment(SwingConstants.TRAILING);

		fld_retype = new JPasswordField();
		fld_retype.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refresh();
			}
		});
		fld_retype.setPreferredSize(new Dimension(100, 19));
		retypePanel.add(fld_retype);

		JSeparator separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(7, 0));
		retypePanel.add(separator_1);

		JPanel entropyPanel = new JPanel();
		GridBagConstraints gbc_entropyPanel = new GridBagConstraints();
		gbc_entropyPanel.fill = GridBagConstraints.BOTH;
		gbc_entropyPanel.gridx = 0;
		gbc_entropyPanel.gridy = 3;
		main.add(entropyPanel, gbc_entropyPanel);
		entropyPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		entropyPanel.add(panel, BorderLayout.NORTH);

		EntropyHarvester EP = new EntropyHarvester();
		panel.add(EP);
		EP.setPreferredSize(new Dimension(230, 159));

		add(main);
		setVisible(true);

	}

	public void refresh() {
		//set status lights based on pass strength
		char[] pass = fld_new.getPassword();
		stl_strength.clear();
		if (pass.length == 0) {

		} else if (pass.length <= 4) {
			stl_strength.addLight(Color.RED, 3);
		} else if (pass.length <= 10) {
			stl_strength.setLight(Color.YELLOW, 2);
			stl_strength.setLight(Color.YELLOW, 3);
		} else {
			stl_strength.setLight(Color.GREEN, 1);
			stl_strength.setLight(Color.GREEN, 2);
			stl_strength.setLight(Color.GREEN, 3);
		}
		if (pass.length >= 4 && Arrays.equals(pass, fld_retype.getPassword())) {
			btn_ok.setEnabled(true);
		} else {
			btn_ok.setEnabled(false);
		}

	}

}
