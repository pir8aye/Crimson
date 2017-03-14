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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel.MPanel;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame.Type;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class ClientIndependentDetail extends JPanel {

	private static final long serialVersionUID = 1L;

	private MPanel parent;

	public ClientIndependentDetail(MPanel mp) {
		parent = mp;

		init();
		initValues();

	}

	private void init() {
		setLayout(null);
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 39, 104, 108);
		add(panel);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 8, 104, 21);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblInterface = new JLabel("Client Independent");
		lblInterface.setFont(new Font("Dialog", Font.BOLD, 10));
		lblInterface.setHorizontalAlignment(SwingConstants.CENTER);
		lblInterface.setIcon(UIUtil.getIcon("icons16/general/dopplr.png"));
		panel_1.add(lblInterface);

		JButton btn_bitcoin = new JButton(UIUtil.getIcon("icons16/general/bitcoin.png"));
		btn_bitcoin.setText("Bitcoin");
		btn_bitcoin.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_bitcoin.setLocation(8, 56);
		btn_bitcoin.setSize(88, 20);
		btn_bitcoin.setFocusable(false);
		panel.add(btn_bitcoin);
		btn_bitcoin.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();

		});
		btn_bitcoin.setMargin(new Insets(2, 4, 2, 4));

		JButton btn_torrent = new JButton(UIUtil.getIcon("icons16/general/box_down.png"));
		btn_torrent.setText("Torrents");
		btn_torrent.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_torrent.setLocation(8, 32);
		btn_torrent.setSize(88, 20);
		btn_torrent.setFocusable(false);
		btn_torrent.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();

		});
		btn_torrent.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_torrent);

		JButton btn_stats = new JButton(UIUtil.getIcon("icons16/general/statistics.png"));
		btn_stats.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_stats.setText("Statistics");
		btn_stats.setLocation(8, 80);
		btn_stats.setSize(88, 20);
		btn_stats.setFocusable(false);
		btn_stats.addActionListener(e -> {
			MainFrame.main.np.addNote("note", "Coming Soon");
			// parent.drop();
		});
		btn_stats.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_stats);

		JButton btn_files = new JButton(UIUtil.getIcon("icons16/general/folder.png"));
		btn_files.setFont(new Font("Dialog", Font.BOLD, 10));
		btn_files.setText("Files");
		btn_files.setLocation(8, 8);
		btn_files.setSize(88, 20);
		btn_files.setFocusable(false);
		btn_files.addActionListener(e -> {
			if (!ViewerState.isOnline()) {
				MainFrame.main.np.addNote("error", "Offline mode is enabled!");
			} else {
				FMFrame fmf = new FMFrame(Type.SV);
				fmf.setVisible(true);
				parent.drop();
			}

		});
		btn_files.setMargin(new Insets(2, 4, 2, 4));
		panel.add(btn_files);
	}

	private void initValues() {

	}
}
