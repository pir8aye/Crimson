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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.platform.collect.singular.CRIMSON;
import com.subterranean_security.crimson.core.platform.collect.singular.RAM;
import com.subterranean_security.crimson.core.store.ConnectionStore;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblCpu;
	private JLabel lblMem;
	private JLabel lblCon;

	private static final int statWidth = 80;
	private static final int statHeight = 12;

	public StatsPanel() {
		init();
	}

	private void init() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));

		lblMem = new JLabel("MEM:");
		lblMem.setForeground(new Color(0, 204, 204));
		lblMem.setHorizontalAlignment(SwingConstants.CENTER);
		lblMem.setToolTipText("Total memory usage of the Crimson process");
		lblMem.setFont(new Font("Monospaced", Font.PLAIN, 9));
		lblMem.setBounds(84, 3, 77, 15);

		lblCon = new JLabel("Connections: ");
		lblCon.setForeground(new Color(0, 204, 204));
		lblCon.setHorizontalAlignment(SwingConstants.CENTER);
		lblCon.setToolTipText("Total established network connections");
		lblCon.setFont(new Font("Monospaced", Font.PLAIN, 9));
		lblCon.setBounds(163, 3, 89, 15);

		lblCpu = new JLabel("CPU:");
		lblCpu.setFont(new Font("Monospaced", Font.PLAIN, 9));
		lblCpu.setForeground(new Color(0, 204, 204));
		lblCpu.setHorizontalAlignment(SwingConstants.CENTER);
		lblCpu.setToolTipText("Total CPU utilization of the Crimson process");
		lblCpu.setBounds(23, 3, 66, 15);

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setBackground(Color.DARK_GRAY);
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel.add(lblCpu, BorderLayout.CENTER);
		panel.add(Box.createVerticalStrut(statHeight), BorderLayout.WEST);
		panel.add(Box.createHorizontalStrut(statWidth), BorderLayout.NORTH);
		add(panel);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(lblMem);
		panel_1.add(Box.createVerticalStrut(statHeight), BorderLayout.WEST);
		panel_1.add(Box.createHorizontalStrut(statWidth), BorderLayout.NORTH);
		add(panel_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.DARK_GRAY);
		panel_2.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_2.add(lblCon);
		panel_2.add(Box.createVerticalStrut(statHeight), BorderLayout.WEST);
		panel_2.add(Box.createHorizontalStrut(statWidth), BorderLayout.NORTH);
		add(panel_2);

	}

	Timer timer = null;

	public void start() {
		stop();
		timer = new Timer(750, (e) -> {

			lblCpu.setText(String.format("CPU: %6.2f%%", Double.parseDouble(CRIMSON.getClientUsage())));
			lblMem.setText("MEM: " + RAM.getClientUsage());
			lblCon.setText("Connections: " + ConnectionStore.getSize());

		});
		timer.setInitialDelay(0);
		timer.start();
	}

	public void stop() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

}
