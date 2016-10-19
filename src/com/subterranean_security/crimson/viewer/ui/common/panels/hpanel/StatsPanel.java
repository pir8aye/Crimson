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
package com.subterranean_security.crimson.viewer.ui.common.panels.hpanel;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.components.piestat.PieStat;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblCpu;
	private JLabel lblMem;
	private JLabel lblConnection;

	private PieStat ps;

	public StatsPanel() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(null);

		ps = new PieStat(17);
		ps.setBounds(2, 2, 17, 17);
		add(ps);

		lblMem = new JLabel("MEM:");
		lblMem.setToolTipText("Total memory usage of the Crimson process");
		lblMem.setFont(new Font("Dialog", Font.BOLD, 9));
		lblMem.setBounds(84, 3, 77, 15);
		add(lblMem);

		lblConnection = new JLabel("Connections: ");
		lblConnection.setToolTipText("Total established network connections");
		lblConnection.setFont(new Font("Dialog", Font.BOLD, 9));
		lblConnection.setBounds(163, 3, 89, 15);
		add(lblConnection);

		lblCpu = new JLabel("CPU:");
		lblCpu.setToolTipText("Total CPU utilization");
		lblCpu.setFont(new Font("Dialog", Font.BOLD, 9));
		lblCpu.setBounds(23, 3, 66, 15);
		add(lblCpu);

	}

	Timer timer = null;

	public void start() {
		stop();
		timer = new Timer(150, (e) -> {
			double r = Platform.Advanced.getCPUUsage();

			lblCpu.setText(String.format("CPU: %03.1f%%", (r * 100)));
			ps.addPoint(r);

			lblMem.setText(
					"MEM: " + CUtil.Misc.familiarize(Platform.Advanced.getCrimsonMemoryUsage(), CUtil.Misc.BYTES));
			lblConnection.setText("Connections: " + ViewerStore.Connections.getSize());

		});
		timer.setInitialDelay(0);
		timer.start();
		ps.startCPUStat();
	}

	public void stop() {
		if (timer != null) {
			timer.stop();
			ps.stop();
			timer = null;
		}
	}

}
