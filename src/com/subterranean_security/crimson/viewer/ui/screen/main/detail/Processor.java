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
import com.subterranean_security.crimson.sv.ClientProfile;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyHighestValues;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;
import javax.swing.border.BevelBorder;

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

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 57, 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		Chart2D chart = new Chart2D();
		GridBagConstraints gbc_chart = new GridBagConstraints();
		gbc_chart.insets = new Insets(0, 0, 5, 0);
		gbc_chart.fill = GridBagConstraints.BOTH;
		gbc_chart.gridx = 0;
		gbc_chart.gridy = 0;
		panel_3.add(chart, gbc_chart);
		chart.setUseAntialiasing(true);
		chart.setBackground(Color.WHITE);
		chart.getAxisX().setVisible(false);
		chart.getAxisY().setRangePolicy(new RangePolicyFixedViewport(new Range(0, 100)));
		chart.getAxisX().setPaintGrid(false);
		chart.getAxisX().setPaintScale(false);
		chart.getAxisY().setVisible(false);
		chart.getAxisY().setPaintGrid(true);
		chart.getAxisY().setPaintScale(false);
		chart.addTrace(trace);
		chart.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		chart.setPaintLabels(false);
		chart.setAutoscrolls(true);

		val_usage = new JLabel("Loading...");
		val_usage.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 1;
		panel_3.add(val_usage, gbc_lblNewLabel_5);

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
		lblCpuModel.setFont(new Font("Dialog", Font.BOLD, 9));
		lblCpuModel.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblLoading = new GridBagConstraints();
		gbc_lblLoading.fill = GridBagConstraints.BOTH;
		gbc_lblLoading.insets = new Insets(0, 0, 5, 0);
		gbc_lblLoading.gridx = 1;
		gbc_lblLoading.gridy = 0;
		panel_2.add(lblCpuModel, gbc_lblLoading);

		JLabel lblNewLabel = new JLabel("Clock Speed:");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Loading...");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 9));
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
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 2;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Physical Cores:");
		lblNewLabel_4.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 3;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

		lblCpuUsage = new JLabel("Loading...");
		lblCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCpuUsage.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblNewLabel_50 = new GridBagConstraints();
		gbc_lblNewLabel_50.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_50.gridx = 1;
		gbc_lblNewLabel_50.gridy = 3;
		panel_2.add(lblCpuUsage, gbc_lblNewLabel_50);

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

	private long updatePeriod = 1000;

	@Override
	public void setTarget(ClientProfile p) {
		System.out.println("Setting target: " + (p == null ? "null" : p.getCvid()));

		// clear chart only if the new profile differs from the old
		if ((p != null) && (profile != null) && p.getCvid() != profile.getCvid()) {
			System.out.println("Clearing chart");
			updateTimer.cancel();
			trace.removeAllPoints();
		}

		if (p != null) {
			profile = p;
			lblCpuModel.setText(profile.getCpuModel());

			updateTimer = new Timer();
			updateTimer.schedule(new TimerTask() {
				double i = 59;

				@Override
				public void run() {
					if (profile.getCpuUsage() != null && isDetailOpen()) {
						double usage = Double.parseDouble(profile.getCpuUsage());
						val_usage.setText(String.format("CPU Utilization: %5.2f%%", usage));
						trace.addPoint(i++, usage);
					}

				}

			}, 0, updatePeriod);

		}

	}

	@Override
	public void setShowing(boolean showing) {
		this.showing = showing;
		if (showing) {
			im = new InfoMaster(InfoParam.newBuilder().setCpuSpeed(speed).setCpuUsage(usage).build(), profile.getCvid(),
					(int) updatePeriod);
			StreamStore.addStream(im);
		} else {
			if (im != null) {
				System.out.println("Closing stream");
				StreamStore.removeStream(im.getStreamID());
			}
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

	private boolean showing = false;

	private JLabel val_usage;

	@Override
	public boolean isDetailOpen() {
		return showing;
	}

}
