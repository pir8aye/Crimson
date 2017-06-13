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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;

public abstract class MDetail extends JPanel {

	private static final long serialVersionUID = 1L;

	protected MPanel parent;

	protected JLabel lbl_header;

	protected MDetail(MPanel parent) {
		this.parent = parent;

		setLayout(null);
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));

		JPanel header = new JPanel(new BorderLayout(0, 0));
		header.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		header.setBounds(MConstants.PANEL_X_OFFSET, 8, MConstants.PANEL_WIDTH, 21);
		add(header);

		lbl_header = new JLabel();
		lbl_header.setFont(new Font("Dialog", Font.BOLD, 10));
		lbl_header.setHorizontalAlignment(SwingConstants.CENTER);

		header.add(lbl_header, BorderLayout.CENTER);
	}

	protected JButton getButton(int y, String icon, String text) {
		JButton btn = new JButton(UIUtil.getIcon(icon));
		btn.setBounds(MConstants.BUTTON_X_OFFSET, y, MConstants.BUTTON_WIDTH, 20);
		btn.setFont(new Font("Dialog", Font.BOLD, 10));
		btn.setText(text);
		btn.setFocusable(false);
		btn.setMargin(new Insets(2, 4, 2, 4));
		return btn;
	}

}
