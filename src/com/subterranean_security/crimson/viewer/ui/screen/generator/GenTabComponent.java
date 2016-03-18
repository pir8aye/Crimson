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
package com.subterranean_security.crimson.viewer.ui.screen.generator;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class GenTabComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	public GenTabComponent(String icon, String label) {

		setPreferredSize(new Dimension(70, 16));
		setOpaque(false);
		setLayout(null);
		JLabel tabLabel = new JLabel(label);
		tabLabel.setBounds(20, 0, 50, 16);
		tabLabel.setHorizontalAlignment(JLabel.CENTER);
		add(tabLabel);

		JLabel label_1 = new JLabel(UUtil.getIcon("icons16/general/" + icon + ".png"));
		label_1.setBounds(0, 0, 19, 16);
		add(label_1);

	}

}
