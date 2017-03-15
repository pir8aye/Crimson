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
package com.subterranean_security.crimson.viewer.ui.screen.main.detail.dmodules;

import java.awt.BorderLayout;
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
import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
import com.subterranean_security.crimson.viewer.ui.common.components.StatusConsole;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.DModule;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyMinimumViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

public class NetInterfaces extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private long updatePeriod = 900;

	private ITrace2D tx = new Trace2DLtd(60);
	private ITrace2D rx = new Trace2DLtd(60);

	private boolean showing = false;

	private JLabel val_usage;

	private JLabel statConsoleMAC;
	private JLabel statConsoleIP;
	private JLabel statConsoleNetmask;

	private Chart2D chart;

	public NetInterfaces() {
		initChart();
		init();
	}

	public void initChart() {
		tx.setColor(new Color(251, 0, 24));
		rx.setColor(new Color(0, 215, 123));

		chart = new Chart2D();
		chart.setUseAntialiasing(true);
		chart.setBackground(Color.WHITE);
		chart.getAxisX().setVisible(false);
		chart.getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(0, 1024)));
		chart.getAxisX().setRangePolicy(new RangePolicyFixedViewport(new Range(0, 60)));
		chart.getAxisX().setPaintGrid(false);
		chart.getAxisX().setPaintScale(false);
		chart.getAxisY().setVisible(false);
		chart.getAxisY().setPaintGrid(true);
		chart.getAxisY().setPaintScale(false);
		chart.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		chart.setPaintLabels(false);
		chart.setAutoscrolls(true);
		chart.addTrace(tx);
		chart.addTrace(rx);
	}

	public void init() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Primary Network Interface",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		add(mainPanel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowHeights = new int[] { 57, 0, 0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		mainPanel.setLayout(gbl_panel);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		mainPanel.add(panel_3, gbc_panel_3);
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
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 1;
		panel_3.add(val_usage, gbc_lblNewLabel_5);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		mainPanel.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		statusConsole = new StatusConsole();
		statConsoleMAC = statusConsole.addRow("MAC");
		statConsoleIP = statusConsole.addRow("IP");
		statConsoleNetmask = statusConsole.addRow("Netmask");

		statConsoleMAC.setText("Loading...");
		statConsoleIP.setText("Loading...");
		statConsoleNetmask.setText("Loading...");

		panel_1.add(statusConsole, BorderLayout.CENTER);

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
						tx.addPoint(time - 1, Double.NaN);
						rx.addPoint(time - 1, Double.NaN);
					}

					Attribute txAttribute = profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_TX_SPEED);
					Attribute rxAttribute = profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_RX_SPEED);
					String txs = "loading";
					String rxs = "loading";
					double t;
					double r;
					if (txAttribute != null && rxAttribute != null) {
						txs = txAttribute.get();
						rxs = rxAttribute.get();

						t = UnitTranslator.nicSpeed(txs);
						r = UnitTranslator.nicSpeed(rxs);
					} else {
						t = Double.NaN;
						r = Double.NaN;
					}

					val_usage.setText(String.format("DN: %s UP: %s", rxs, txs));

					last = new Date();
					tx.addPoint(time, t);
					rx.addPoint(time, r);
					chart.getAxisX().getRangePolicy().setRange(new Range(time, time - (60 * updatePeriod)));

				}

			}

		}, 0, updatePeriod);
	}

	private ClientProfile profile;
	private InfoMaster im;
	private Timer updateTimer = new Timer();

	@Override
	public void setTarget(ClientProfile p) {

		// clear chart only if the new profile differs from the old
		if ((profile != null) && p.getCid() != profile.getCid()) {
			tx.removeAllPoints();
			rx.removeAllPoints();
		}

		profile = p;

		// set static attributes
		statConsoleMAC.setText(profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_MAC).get());
		statConsoleIP.setText(profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_IP).get());
		statConsoleNetmask.setText(profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_MASK).get());

		// set title
		mainPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)),
				profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_DESC).get(), TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
	}

	private SwingWorker<Void, Void> timeout = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			Thread.sleep(3000);
			return null;
		}

		@Override
		protected void done() {
			if (statConsoleMAC.getText().equals("Loading...")) {
				statConsoleMAC.setText("N/A");
			}
			if (statConsoleIP.getText().equals("Loading...")) {
				statConsoleIP.setText("N/A");
			}
			if (statConsoleNetmask.getText().equals("Loading...")) {
				statConsoleNetmask.setText("N/A");
			}

		};

	};

	private JPanel mainPanel;

	private StatusConsole statusConsole;

	@Override
	public void setShowing(boolean showing) {
		this.showing = showing;
		if (showing) {
			im = new InfoMaster(InfoParam.newBuilder().setNicRxSpeed(true).setNicTxSpeed(true).build(),
					profile.getCid(), (int) updatePeriod);
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
