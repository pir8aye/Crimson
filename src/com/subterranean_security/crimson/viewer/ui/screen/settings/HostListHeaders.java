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
package com.subterranean_security.crimson.viewer.ui.screen.settings;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.profile.AbstractAttribute;
import com.subterranean_security.crimson.core.profile.ComplexAttribute;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;

public class HostListHeaders extends JPanel {

	private static final long serialVersionUID = 1L;

	public HashMap<AbstractAttribute, JCheckBox> boxes = new HashMap<AbstractAttribute, JCheckBox>();

	public HostListHeaders() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel static_panel = new JPanel();
		static_panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(static_panel);
		GridBagLayout gbl_static_panel = new GridBagLayout();
		gbl_static_panel.columnWidths = new int[] { 173, 173, 173, 0 };
		gbl_static_panel.rowHeights = new int[] { 25, 25, 25, 25, 25, 0, 0 };
		gbl_static_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_static_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		static_panel.setLayout(gbl_static_panel);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			JCheckBox jcb = new JCheckBox(sa.toString());
			jcb.setFont(new Font("Dialog", Font.BOLD, 10));
			static_panel.add(jcb);
			boxes.put(sa, jcb);
		}

		for (ComplexAttribute ca : ComplexAttribute.values()) {
			JCheckBox jcb = new JCheckBox(ca.toString());
			jcb.setFont(new Font("Dialog", Font.BOLD, 10));
			static_panel.add(jcb);
			boxes.put(ca, jcb);
		}

		//
		// GridBagConstraints gbc_chckbxCountry = new GridBagConstraints();
		// gbc_chckbxCountry.fill = GridBagConstraints.BOTH;
		// gbc_chckbxCountry.insets = new Insets(0, 0, 5, 5);
		// gbc_chckbxCountry.gridx = 0;
		// gbc_chckbxCountry.gridy = 0;
		// static_panel.add(chckbxCountry, gbc_chckbxCountry);

	}

}
