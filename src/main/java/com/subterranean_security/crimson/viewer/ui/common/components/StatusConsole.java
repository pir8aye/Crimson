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
package com.subterranean_security.crimson.viewer.ui.common.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.lpanel.LPanel;

public class StatusConsole extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color blue = new Color(0, 204, 204);
	private static final Color green = new Color(0, 215, 123);
	private static final Color orange = new Color(255, 191, 0);

	private static final Color bg = Color.DARK_GRAY;

	private LPanel list;

	public StatusConsole() {
		super(new BorderLayout());
		init();
		UIUtil.disableMouse(this);
	}

	private void init() {

		setForeground(bg);
		setBackground(bg);

		list = new LPanel(bg);
		list.setBackground(bg);
		list.setForeground(bg);

		add(Box.createHorizontalStrut(4), BorderLayout.WEST);
		add(list, BorderLayout.CENTER);
		add(Box.createHorizontalStrut(4), BorderLayout.EAST);

	}

	public JLabel addRow(String header) {
		JPanel panel = new JPanel();
		panel.setBackground(bg);
		panel.setLayout(new BorderLayout(0, 0));
		JLabel property = new JLabel(header);
		property.setFont(new Font("Monospaced", Font.PLAIN, 10));
		property.setForeground(blue);
		panel.add(property, BorderLayout.WEST);
		JLabel value = new JLabel();
		value.setFont(new Font("Monospaced", Font.PLAIN, 10));
		value.setForeground(blue);
		value.setHorizontalAlignment(SwingConstants.RIGHT);

		panel.add(Box.createHorizontalStrut(4), BorderLayout.CENTER);
		panel.add(value, BorderLayout.EAST);

		list.addPanel(panel);
		return value;
	}

	public enum LineType {
		ORANGE, BLUE, GREEN;

	}

}
