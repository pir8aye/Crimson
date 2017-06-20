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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class WideMenuItem extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lbl_title;

	public WideMenuItem(String text, String desc) {

		setBorder(new LineBorder(new Color(0, 0, 0)));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(UIManager.getColor("MenuItem.selectionBackground"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				resetBG();
			}
		});
		setLayout(new BorderLayout(0, 0));

		lbl_title = new JLabel(text);
		lbl_title.setFont(new Font("Dialog", Font.PLAIN, 11));
		add(lbl_title, BorderLayout.CENTER);

		JLabel lbl_desc = new JLabel(desc);
		lbl_desc.setForeground(new Color(105, 105, 105));
		lbl_desc.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_desc.setFont(new Font("Dialog", Font.BOLD, 9));
		add(lbl_desc, BorderLayout.SOUTH);

		add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		add(Box.createHorizontalStrut(15), BorderLayout.EAST);

	}

	/**
	 * @wbp.parser.constructor
	 */
	public WideMenuItem(ImageIcon ico, String title, String desc) {
		this(title, desc);
		lbl_title.setIcon(ico);
	}

	public void resetBG() {
		setBackground(new Color(238, 238, 238));
	}
}
