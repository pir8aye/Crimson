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
package com.subterranean_security.crimson.core.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChatWindow extends JFrame {

	private static final long	serialVersionUID	= 1L;
	private JPanel				contentPane;
	private JTextArea			textArea2;
	private JPanel				button_panel;
	private JTextArea			textArea;
	private JButton				btnNewButton;
	private JPanel				chat_panel;
	private JPanel				screen_panel;
	private JPanel				panel_1;

	public ChatWindow() {
		setMinimumSize(new Dimension(450, 300));
		setTitle("Remote Chat");
		setBounds(100, 100, 782, 522);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		chat_panel = new JPanel();
		chat_panel.setPreferredSize(new Dimension(250, 10));
		contentPane.add(chat_panel);
		chat_panel.setLayout(null);

		panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 250, 482);
		chat_panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel_1.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (textArea.getText().length() > 0) {
					btnNewButton.setEnabled(true);
				} else {
					btnNewButton.setEnabled(false);
				}
			}
		});
		textArea.setRows(3);
		panel.add(textArea, BorderLayout.CENTER);

		button_panel = new JPanel(new FlowLayout(SwingConstants.RIGHT));
		panel.add(button_panel, BorderLayout.SOUTH);

		btnNewButton = new JButton("Send");
		btnNewButton.setEnabled(false);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (textArea.getText().length() < 1) {
					return;
				}
				String message = textArea.getText();
				addMessage(message, "You");
				textArea.setText("");

			}
		});
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 10));
		button_panel.add(btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));

		textArea2 = new JTextArea();
		textArea2.setEditable(false);
		scrollPane.setViewportView(textArea2);

		screen_panel = new JPanel();
		screen_panel.setPreferredSize(new Dimension(300, 10));
		contentPane.add(screen_panel);
		screen_panel.setLayout(new BorderLayout(0, 0));
	}

	public void addMessage(String message, String user) {
		textArea2.setText(textArea2.getText() + "\n[" + new Date().toString() + "] (" + user + "): " + message);

	}
}
