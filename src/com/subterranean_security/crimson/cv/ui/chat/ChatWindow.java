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
package com.subterranean_security.crimson.cv.ui.chat;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

import javax.swing.JMenuBar;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.Insets;

public class ChatWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private EPanel ep;

	public ChatWindow() {
		init();
	}

	public void init() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImages(UIUtil.getIconList());
		setTitle("Remote Chat");
		setBounds(100, 100, 287, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		ChatPanel chatPanel = new ChatPanel();
		panel.add(chatPanel);

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar, BorderLayout.NORTH);

		JButton btnE = new JButton("E");
		btnE.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnE);

		menuBar.add(Box.createHorizontalGlue());
		JButton btnS = new JButton(UIUtil.getIcon("icons16/general/cog.png"));
		btnS.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnS);

		ep = new EPanel(new JPanel());
		contentPane.add(ep, BorderLayout.EAST);

	}

}
