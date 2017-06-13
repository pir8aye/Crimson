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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.details;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MConstants;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;

public class GeneratorDetail extends MDetail {

	private static final long serialVersionUID = 1L;

	public GeneratorDetail(MPanel mp) {
		super(mp);

		init();
		initValues();

	}

	private void init() {
		lbl_header.setText("Generator");
		lbl_header.setIcon(UIUtil.getIcon("icons16/general/compile.png"));

		JPanel panel = new JPanel(null);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(MConstants.PANEL_X_OFFSET, 39, MConstants.PANEL_WIDTH, 108);
		add(panel);

		JButton btnJar = getButton(8, "icons16/general/java.png", "Jar");
		btnJar.addActionListener(e -> {
			parent.drop();
		});
		panel.add(btnJar);

		JButton btnExe = getButton(32, "icons16/files/file_extension_exe.png", "Exe");
		btnExe.addActionListener(e -> {
		});
		panel.add(btnExe);

		JButton btnSh = getButton(56, "icons16/general/java.png", "Sh Script");
		btnSh.addActionListener(e -> {
			parent.drop();
		});
		panel.add(btnSh);

		JButton btnQr = getButton(80, "icons16/general/barcode_2d.png", "QR Code");
		btnQr.addActionListener(e -> {
			parent.drop();
		});
		panel.add(btnQr);

	}

	private void initValues() {

	}
}
