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
package com.subterranean_security.crimson.viewer.ui.screen.main.dmodules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.attribute.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AKeyCPU;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.util.ProtoUtil;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.common.components.StatusConsole;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.dpanel.DModule;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

public class Processor extends TracedPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private ITrace2D trace;

	private boolean showing = false;

	private JLabel val_usage;

	private JLabel statConsoleModel;
	private JLabel statConsoleFreq;
	private JLabel statConsoleTemp;

	public Processor() {
		super();
		initChart();
		init();
	}

	private void initChart() {

		chart = new Chart2D();

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
		chart.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		chart.setPaintLabels(false);
		chart.setAutoscrolls(true);
	}

	private void initTraces() {

		trace = new Trace2DLtd(60);
		trace.setColor(Color.RED);

		addTraces();
	}

	private void addTraces() {
		chart.removeAllTraces();
		chart.addTrace(trace);
	}

	private void init() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Processor", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowHeights = new int[] { 57, 0, 0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

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

		GridBagConstraints gbc_chart = new GridBagConstraints();
		gbc_chart.insets = new Insets(0, 0, 5, 0);
		gbc_chart.fill = GridBagConstraints.BOTH;
		gbc_chart.gridx = 0;
		gbc_chart.gridy = 0;
		panel_3.add(chart, gbc_chart);

		val_usage = new JLabel("Loading...");
		val_usage.setFont(new Font("Dialog", Font.BOLD, 9));

		JPanel val_usage_panel = new JPanel(new BorderLayout());
		val_usage_panel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		val_usage_panel.add(val_usage);

		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 1;
		panel_3.add(val_usage_panel, gbc_lblNewLabel_5);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		panel.add(panel_1, gbc_panel_1);

		statusConsole = new StatusConsole();
		statConsoleModel = statusConsole.addRow("Model");
		statConsoleFreq = statusConsole.addRow("Frequency");
		statConsoleTemp = statusConsole.addRow("Core Temp");

		statConsoleModel.setText("Loading...");
		statConsoleFreq.setText("Loading...");
		statConsoleTemp.setText("Loading...");

		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(statusConsole);

	}

	class UpdateTask extends TimerTask {

		@Override
		public void run() {
			double time = System.currentTimeMillis() - start.getTime();

			// break trace if the last update was longer than two
			// periods ago
			if (System.currentTimeMillis() - last.getTime() > updatePeriod * 2) {
				trace.addPoint(time - 1, Double.NaN);
			}

			// usage
			String usage = getPrimaryCPU().get(AKeyCPU.CPU_TOTAL_USAGE);
			double u;
			if (usage != null) {
				u = Double.parseDouble(usage);

				val_usage.setText(String.format("Average Utilization: %5.2f%%", u));
			} else {
				u = Double.NaN;
			}

			last = new Date();
			trace.addPoint(time, u);

			chart.getAxisX().getRangePolicy().setRange(new Range(time, time - (60 * updatePeriod)));

			// set dynamic attributes
			// temperature
			String temp = getPrimaryCPU().get(AKeyCPU.CPU_TEMP);
			if (temp != null) {
				statConsoleTemp.setText(temp);
			}

		}

	}

	private ClientProfile profile;
	private List<AttributeGroup> cpuList;

	private AttributeGroup getPrimaryCPU() {
		return cpuList.get(0);
	}

	@Override
	public void setTarget(ClientProfile p) {
		List<ITrace2D> savedTraces = traceList.get(p.getCvid());
		if (savedTraces == null) {
			initTraces();
			savedTraces = new ArrayList<ITrace2D>();
			savedTraces.add(trace);
			traceList.put(p.getCvid(), savedTraces);
		} else {
			trace = savedTraces.get(0);
			addTraces();
		}

		profile = p;
		cpuList = profile.getGroupList(AttributeKey.Type.CPU);

		// set static attributes
		try {
			System.out.println("cpu model: " + getPrimaryCPU().get(AKeyCPU.CPU_MODEL));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		statConsoleModel.setText(getPrimaryCPU().get(AKeyCPU.CPU_MODEL));
		statConsoleFreq.setText(getPrimaryCPU().get(AKeyCPU.CPU_FREQUENCY_MAX));

	}

	private SwingWorker<Void, Void> timeout = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			Thread.sleep(3000);
			return null;
		}

		@Override
		protected void done() {
			if ("Loading...".equals(statConsoleModel.getText())) {
				statConsoleModel.setText("N/A");
			}
			if ("Loading...".equals(statConsoleFreq.getText())) {
				statConsoleFreq.setText("N/A");
			}
			if ("Loading...".equals(statConsoleTemp.getText())) {
				statConsoleTemp.setText("N/A");
			}
		};

	};

	private StatusConsole statusConsole;

	private InfoMaster im;

	@Override
	public void setShowing(boolean showing) {
		this.showing = showing;
		if (showing) {

			im = new InfoMaster(ProtoUtil.getInfoParam(AKeyCPU.CPU_TOTAL_USAGE, AKeyCPU.CPU_TEMP).build(),
					profile.getCvid(), (int) updatePeriod);
			StreamStore.addStream(im);

			// launch timeout
			timeout.execute();
			startRefresh(new UpdateTask());
		} else {
			if (im != null) {
				StreamStore.removeStreamBySID(im.getStreamID());
			}
			stopRefresh();
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

}
