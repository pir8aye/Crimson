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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.npanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class Notification extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel text = new JLabel();
	private JLabel icon = new JLabel();
	private Runnable r;
	private final JPanel panel = new JPanel();
	private final JLabel label = new JLabel(" ");
	private JLabel subtext;

	public Notification() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				new Thread(r).start();

			}
		});

		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BorderLayout(0, 0));
		add(icon, BorderLayout.WEST);

		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		text.setOpaque(true);
		panel.add(text, BorderLayout.CENTER);

		text.setFont(new Font("Dialog", Font.BOLD, 10));

		subtext = new JLabel(" ");
		subtext.setFocusable(false);
		subtext.setBackground(null);
		panel.add(subtext, BorderLayout.SOUTH);
		subtext.setForeground(Color.GRAY);
		subtext.setHorizontalAlignment(SwingConstants.CENTER);
		subtext.setFont(new Font("Dialog", Font.BOLD, 9));
		label.setFont(new Font("Dialog", Font.BOLD, 9));

		panel.add(label, BorderLayout.NORTH);
	}

	public void set(String type, String string, String subtext, Runnable r) {
		this.subtext.setText(subtext);

		text.setText(string);
		this.r = r;

		switch (type) {
		case ("error"): {
			icon.setIcon(UIUtil.getIcon("icons32/general/exclamation.png"));
			break;
		}
		case ("disconnection"): {
			icon.setIcon(UIUtil.getIcon("icons32/general/disconnect.png"));
			break;
		}
		default: {
			icon.setIcon(UIUtil.getIcon("c-32.png"));
			break;
		}
		}

	}
}
