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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

public class Processor extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private long updatePeriod = 900;

	private ITrace2D trace = new Trace2DLtd(60);

	private boolean showing = false;

	private JLabel val_usage;

	private JLabel lblTemperatureVar;

	private Chart2D chart;

	private JLabel lblFrequencyVar;

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

		chart = new Chart2D();
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
		chart.getAxisX().setRangePolicy(new RangePolicyFixedViewport(new Range(0, 60)));
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
		gbl_panel_2.columnWidths = new int[] { 65, 45, 0 };
		gbl_panel_2.rowHeights = new int[] { 15, 15, 15 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblModel = new JLabel("Model:");
		lblModel.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.fill = GridBagConstraints.BOTH;
		gbc_lblModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 0;
		panel_2.add(lblModel, gbc_lblModel);

		lblModelVar = new JLabel("Loading...");
		lblModelVar.setFont(new Font("Dialog", Font.BOLD, 9));
		lblModelVar.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblModelVar = new GridBagConstraints();
		gbc_lblModelVar.fill = GridBagConstraints.BOTH;
		gbc_lblModelVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblModelVar.gridx = 1;
		gbc_lblModelVar.gridy = 0;
		panel_2.add(lblModelVar, gbc_lblModelVar);

		JLabel lblFrequency = new JLabel("Frequency:");
		lblFrequency.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblFrequency = new GridBagConstraints();
		gbc_lblFrequency.fill = GridBagConstraints.BOTH;
		gbc_lblFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_lblFrequency.gridx = 0;
		gbc_lblFrequency.gridy = 1;
		panel_2.add(lblFrequency, gbc_lblFrequency);

		lblFrequencyVar = new JLabel("Loading...");
		lblFrequencyVar.setFont(new Font("Dialog", Font.BOLD, 9));
		lblFrequencyVar.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblFrequencyVar = new GridBagConstraints();
		gbc_lblFrequencyVar.fill = GridBagConstraints.BOTH;
		gbc_lblFrequencyVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblFrequencyVar.gridx = 1;
		gbc_lblFrequencyVar.gridy = 1;
		panel_2.add(lblFrequencyVar, gbc_lblFrequencyVar);

		JLabel lblTemperature = new JLabel("Core Temp:");
		lblTemperature.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblTemperature = new GridBagConstraints();
		gbc_lblTemperature.fill = GridBagConstraints.BOTH;
		gbc_lblTemperature.insets = new Insets(0, 0, 5, 5);
		gbc_lblTemperature.gridx = 0;
		gbc_lblTemperature.gridy = 2;
		panel_2.add(lblTemperature, gbc_lblTemperature);

		lblTemperatureVar = new JLabel("Loading...");
		lblTemperatureVar.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTemperatureVar.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblTemperatureVar = new GridBagConstraints();
		gbc_lblTemperatureVar.fill = GridBagConstraints.BOTH;
		gbc_lblTemperatureVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblTemperatureVar.gridx = 1;
		gbc_lblTemperatureVar.gridy = 2;
		panel_2.add(lblTemperatureVar, gbc_lblTemperatureVar);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		panel.add(panel_1, gbc_panel_1);

		startUpdater();
	}

	private void startUpdater() {
		updateTimer.schedule(new TimerTask() {
			Date start = new Date();
			Date last = new Date();

			@Override
			public void run() {
				if (isDetailOpen()) {

					double time = System.currentTimeMillis() - start.getTime();

					if (System.currentTimeMillis() - last.getTime() > updatePeriod * 2) {
						trace.addPoint(time - 1, Double.NaN);
					}

					// usage
					double usage = Double.parseDouble(
							profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TOTAL_USAGE).get());

					val_usage.setText(String.format("Average Utilization: %5.2f%%", usage));

					last = new Date();
					trace.addPoint(time, usage);
					chart.getAxisX().getRangePolicy().setRange(new Range(time, time - (60 * updatePeriod)));

					// set dynamic attributes
					// temperature
					if (profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TEMP) != null) {
						lblTemperatureVar
								.setText(profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TEMP).get());
					}

				}

			}

		}, 0, updatePeriod);
	}

	private ClientProfile profile;
	private InfoMaster im;
	private Timer updateTimer = new Timer();

	private JLabel lblModelVar;

	@Override
	public void setTarget(ClientProfile p) {

		// clear chart only if the new profile differs from the old
		if ((profile != null) && p.getCid() != profile.getCid()) {
			trace.removeAllPoints();
		}

		profile = p;

		// set static attributes
		lblModelVar.setText(profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_VENDOR).get() + " "
				+ profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_MODEL).get());
		lblFrequencyVar.setText(profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_FREQUENCY_MAX).get());

	}

	private SwingWorker<Void, Void> timeout = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			Thread.sleep(3000);
			return null;
		}

		@Override
		protected void done() {
			if (lblTemperatureVar.getText().equals("Loading...")) {
				lblTemperatureVar.setText("N/A");
			}
		};

	};

	@Override
	public void setShowing(boolean showing) {
		this.showing = showing;
		if (showing) {
			im = new InfoMaster(InfoParam.newBuilder().setCpuUsage(true).setCpuTemp(true).build(), profile.getCid(),
					(int) updatePeriod);
			StreamStore.addStream(im);

			// launch timeout
			timeout.execute();
		} else {
			if (im != null) {
				StreamStore.removeStreamBySID(im.getStreamID());
			}
		}
	}

	@Override
	public void updateGraphics() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWeight() {
		return 10;
	}

	@Override
	public int getDWidth() {
		return 100;
	}

	@Override
	public boolean isDetailOpen() {
		return showing;
	}

	@Override
	protected void finalize() throws Throwable {
		if (updateTimer != null) {
			updateTimer.cancel();
		}
		super.finalize();
	}

}
