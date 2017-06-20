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
package com.subterranean_security.crimson.viewer.ui.screen.netman.auth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.viewer.ui.common.components.DataViewer;

public class GroupInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	public GroupInfo(AuthMethod am) {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(400, 100));
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		DataViewer dv = new DataViewer();
		dv.addRow(new String[] { "Authentication Type", "GROUP" });
		dv.addRow(new String[] { "ID", "" + am.getId() });
		dv.addRow(new String[] { "Name", am.getName() });
		dv.addRow(new String[] { "Key", am.getGroupSeedPrefix() });
		dv.addRow(new String[] { "Creation Date", new Date(am.getCreation()).toString() });

		panel_2.add(dv, BorderLayout.CENTER);

		JLabel lblGroupKeyfbddefca = new JLabel("Group Key: " + am.getGroupSeedPrefix());
		lblGroupKeyfbddefca.setFont(new Font("Dialog", Font.BOLD, 9));
		add(lblGroupKeyfbddefca, BorderLayout.NORTH);

	}

}
