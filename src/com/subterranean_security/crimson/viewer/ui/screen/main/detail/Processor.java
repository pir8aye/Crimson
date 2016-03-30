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
package com.subterranean_security.crimson.viewer.ui.screen.main.detail;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.ClientProfile;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

public class Processor extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private ITrace2D trace = new Trace2DLtd(60);
	private boolean speed = true;// TODO configurable
	private boolean usage = true;

	public Processor() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Processor", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowHeights = new int[] { 57, 0, 0, 0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		trace.setColor(Color.RED);

		Chart2D chart = new Chart2D();
		chart.setUseAntialiasing(true);
		chart.setBackground(Color.WHITE);
		chart.setGridColor(Color.black);
		chart.getAxisX().setVisible(false);
		chart.getAxisY().setRangePolicy(new RangePolicyFixedViewport(new Range(0, 100)));
		chart.getAxisX().setPixelXLeft(0);
		chart.getAxisX().setPaintGrid(false);
		chart.getAxisX().setPaintScale(false);
		chart.getAxisY().setVisible(false);
		chart.getAxisY().setPaintGrid(true);
		chart.getAxisY().setPaintScale(false);
		chart.addTrace(trace);
		chart.setUseAntialiasing(true);
		chart.setFont(new Font("Dialog", Font.PLAIN, 8));
		chart.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		chart.setPaintLabels(false);
		chart.setAutoscrolls(true);
		GridBagConstraints gbc_lblGraph = new GridBagConstraints();
		gbc_lblGraph.insets = new Insets(0, 0, 5, 0);
		gbc_lblGraph.fill = GridBagConstraints.BOTH;
		gbc_lblGraph.gridx = 0;
		gbc_lblGraph.gridy = 0;
		panel.add(chart, gbc_lblGraph);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.anchor = GridBagConstraints.NORTH;
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 65, 65, 0 };
		gbl_panel_2.rowHeights = new int[] { 15, 15, 15, 15, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblTotalCpuUsage = new JLabel("Model:");
		lblTotalCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblTotalCpuUsage = new GridBagConstraints();
		gbc_lblTotalCpuUsage.fill = GridBagConstraints.BOTH;
		gbc_lblTotalCpuUsage.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalCpuUsage.gridx = 0;
		gbc_lblTotalCpuUsage.gridy = 0;
		panel_2.add(lblTotalCpuUsage, gbc_lblTotalCpuUsage);

		lblCpuModel = new JLabel("Loading...");
		lblCpuModel.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCpuModel.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblLoading = new GridBagConstraints();
		gbc_lblLoading.fill = GridBagConstraints.BOTH;
		gbc_lblLoading.insets = new Insets(0, 0, 5, 0);
		gbc_lblLoading.gridx = 1;
		gbc_lblLoading.gridy = 0;
		panel_2.add(lblCpuModel, gbc_lblLoading);

		JLabel lblNewLabel = new JLabel("Max Clock Speed:");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Loading...");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Temperature:");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Loading...");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 2;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Current Utilization:");
		lblNewLabel_4.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 3;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

		lblCpuUsage = new JLabel("Loading...");
		lblCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 3;
		panel_2.add(lblCpuUsage, gbc_lblNewLabel_5);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		panel.add(panel_1, gbc_panel_1);

	}

	private ClientProfile profile;
	private InfoMaster im;
	private Timer updateTimer = new Timer();

	private JLabel lblCpuModel;

	private JLabel lblCpuUsage;

	private long updatePeriod;

	@Override
	public void setTarget(ClientProfile p) {
		updateTimer.cancel();
		
		// clear chart only if the new profile differs from the old
		if (p.getCvid() != profile.getCvid()) {
			trace.removeAllPoints();
		}
		if (im != null) {
			StreamStore.removeStream(im.getStreamID());
		}
		if (p != null) {
			profile = p;
			lblCpuModel.setText(profile.getCpuModel());

			im = new InfoMaster(InfoParam.newBuilder().setCpuSpeed(speed).setCpuUsage(usage).build(),
					profile.getCvid()); // TODO set period in param
			StreamStore.addStream(im);
			im.start();

			updateTimer.schedule(new TimerTask() {

				private long m_starttime = System.currentTimeMillis();

				@Override
				public void run() {
					// TODO get average cpu usage from profile
					trace.addPoint(((double) System.currentTimeMillis() - this.m_starttime), 0);
				}

			}, 0, updatePeriod);

		}

	}

	@Override
	public void setShowing(boolean showing) {
		if (showing) {
			im.start();
		} else {
			im.stop();
		}

	}

	@Override
	public void updateGraphics() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(DModule arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDWidth() {
		return 0;
	}

}
