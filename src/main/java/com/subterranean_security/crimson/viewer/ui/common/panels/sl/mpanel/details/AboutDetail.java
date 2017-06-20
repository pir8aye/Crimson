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

public class AboutDetail extends MDetail {

	private static final long serialVersionUID = 1L;

	public AboutDetail(MPanel mp) {
		super(mp);

		init();
		initValues();

	}

	private void init() {
		lbl_header.setText("About");
		lbl_header.setIcon(UIUtil.getIcon("c-16.png"));

		JPanel body = new JPanel(null);
		body.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body.setBounds(MConstants.PANEL_X_OFFSET, 39, MConstants.PANEL_WIDTH, 88);
		add(body);

		JButton btnLogs = getButton(32, "icons16/general/error_log.png", "Logs");
		btnLogs.addActionListener(e -> {
			parent.drop();
		});
		body.add(btnLogs);

		JButton btnAbout = getButton(8, "c-16.png", "About");
		btnAbout.addActionListener(e -> {
		});
		body.add(btnAbout);
	}

	private void initValues() {

	}
}
