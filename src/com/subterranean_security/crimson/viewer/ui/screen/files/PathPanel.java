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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.subterranean_security.crimson.viewer.ui.UIUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.UIManager;

public class PathPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel label;
	private JLabel lblPath;

	public PathPanel() {
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));

		lblPath = new JLabel("");
		lblPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				openEdit();
			}
		});
		panel.add(lblPath, "VIEW");

		panel_1 = new JPanel();
		panel_1.setBackground(UIManager.getColor("TextField.background"));
		panel.add(panel_1, "EDIT");
		panel_1.setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					openView();
				}
			}
		});
		panel_1.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);

		label = new JLabel();

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openView();
			}
		});
		label.setIcon(UIUtil.getIcon("icons16/general/folder_go.png"));
		panel_1.add(label, BorderLayout.EAST);

	}

	public void setPwd(String path) {
		textField.setText(path);
		lblPath.setText(path);
	}

	public void openEdit() {
		((CardLayout) panel.getLayout()).show(panel, "EDIT");
		textField.selectAll();
	}

	public void openView() {
		((CardLayout) panel.getLayout()).show(panel, "VIEW");
	}

}
