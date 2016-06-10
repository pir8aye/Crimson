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
import javax.swing.JProgressBar;
import java.awt.Dimension;

public class PathPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel label;
	private JLabel lblPath;
	private JPanel panel_2;
	private JProgressBar progressBar;

	public PathPanel() {
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));

		panel_2 = new JPanel();
		panel.add(panel_2, "VIEW");
		panel_2.setLayout(new BorderLayout(0, 0));

		lblPath = new JLabel("");
		panel_2.add(lblPath);

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(148, 4));
		panel_2.add(progressBar, BorderLayout.SOUTH);
		lblPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				openEdit();
			}
		});

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

	private String path;

	public void setPwd(String path) {
		this.path = path;
		textField.setText(path);
		lblPath.setText(path);
	}

	public String getPwd() {
		return path;
	}

	private boolean loading = false;

	public void beginLoading() {
		loading = true;
		progressBar.setIndeterminate(true);
	}

	public void stopLoading() {
		loading = false;
		progressBar.setIndeterminate(false);
	}

	public void openEdit() {
		if (!loading) {
			((CardLayout) panel.getLayout()).show(panel, "EDIT");
			textField.selectAll();
		}

	}

	public void openView() {
		((CardLayout) panel.getLayout()).show(panel, "VIEW");
	}

}
