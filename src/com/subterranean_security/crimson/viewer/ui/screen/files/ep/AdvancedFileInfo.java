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
package com.subterranean_security.crimson.viewer.ui.screen.files.ep;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.exception.InvalidObjectException;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.ObjectTransfer;
import com.subterranean_security.crimson.viewer.ui.common.components.DataViewer;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import javax.swing.SwingConstants;

public class AdvancedFileInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	public AdvancedFileInfo(RS_AdvancedFileInfo rs, EPanel ep) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		DataViewer dv = new DataViewer();
		dv.addRow(new String[] { "Filename", rs.getName() });
		dv.addRow(new String[] { "Path", rs.getPath() });
		dv.addRow(new String[] { "Size",
				CUtil.Misc.familiarize(rs.getSize(), CUtil.Misc.BYTES) + " (" + rs.getSize() + " bytes)" });
		if (rs.hasCtime())
			dv.addRow(new String[] { "Creation Time", new Date(rs.getCtime()).toString() });
		if (rs.hasMtime())
			dv.addRow(new String[] { "Last Modification Time", new Date(rs.getMtime()).toString() });
		if (rs.hasAtime())
			dv.addRow(new String[] { "Last Access Time", new Date(rs.getAtime()).toString() });

		panel.add(dv, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		panel_1.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
		panel_1.add(Box.createVerticalStrut(5), BorderLayout.NORTH);

		JLabel lblIcon = new JLabel();
		lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblIcon);
		try {
			lblIcon.setIcon((Icon) ObjectTransfer.Default.deserialize(B64.decode(rs.getLocalIcon())));
		} catch (InvalidObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblIcon.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Closing info");
				ep.drop();
			}
		});
		btnClose.setMargin(new Insets(2, 4, 2, 4));
		btnClose.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(btnClose);
	}

}
