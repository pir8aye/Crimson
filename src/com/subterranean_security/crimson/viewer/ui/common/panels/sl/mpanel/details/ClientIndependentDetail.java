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

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MConstants;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MDetail;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame.Type;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class ClientIndependentDetail extends MDetail {

	private static final long serialVersionUID = 1L;

	public ClientIndependentDetail(MPanel mp) {
		super(mp);

		init();
		initValues();

	}

	private void init() {
		lbl_header.setText("Independent Tools");
		lbl_header.setIcon(UIUtil.getIcon("icons16/general/dopplr.png"));

		JPanel body = new JPanel(null);
		body.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		body.setBounds(MConstants.PANEL_X_OFFSET, 39, MConstants.PANEL_WIDTH, 109);
		add(body);

		JButton btn_bitcoin = getButton(56, "icons16/general/bitcoin.png", "Bitcoin");
		btn_bitcoin.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();

		});
		body.add(btn_bitcoin);

		JButton btn_torrent = getButton(32, "icons16/general/box_down.png", "Torrents");
		btn_torrent.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();

		});
		body.add(btn_torrent);

		JButton btn_stats = getButton(80, "icons16/general/statistics.png", "Statistics");
		btn_stats.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();
		});
		body.add(btn_stats);

		JButton btn_files = getButton(8, "icons16/general/folder.png", "Files");
		btn_files.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else {
				FMFrame fmf = new FMFrame(Type.SV);
				fmf.setVisible(true);
				parent.drop();
			}

		});
		body.add(btn_files);
	}

	private void initValues() {

	}
}
