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
import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
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

	private JLabel lblTypeVar;

	private Chart2D chart;

	private JLabel lblMacVar;

	public NetInterfaces() {
		initChart();
		init();
	}

	public void initChart() {
		tx.setColor(Color.RED);
		rx.setColor(Color.GREEN);

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

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Primary Network Interface",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowHeights = new int[] { 57, 0, 0, 0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
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

		JLabel lblIp = new JLabel("IP:");
		lblIp.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblIp = new GridBagConstraints();
		gbc_lblIp.fill = GridBagConstraints.BOTH;
		gbc_lblIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblIp.gridx = 0;
		gbc_lblIp.gridy = 0;
		panel_2.add(lblIp, gbc_lblIp);

		lblIpVar = new JLabel("Loading...");
		lblIpVar.setFont(new Font("Dialog", Font.BOLD, 9));
		lblIpVar.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblIpVar = new GridBagConstraints();
		gbc_lblIpVar.fill = GridBagConstraints.BOTH;
		gbc_lblIpVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblIpVar.gridx = 1;
		gbc_lblIpVar.gridy = 0;
		panel_2.add(lblIpVar, gbc_lblIpVar);

		JLabel lblMac = new JLabel("MAC:");
		lblMac.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblMac = new GridBagConstraints();
		gbc_lblMac.fill = GridBagConstraints.BOTH;
		gbc_lblMac.insets = new Insets(0, 0, 5, 5);
		gbc_lblMac.gridx = 0;
		gbc_lblMac.gridy = 1;
		panel_2.add(lblMac, gbc_lblMac);

		lblMacVar = new JLabel("Loading...");
		lblMacVar.setFont(new Font("Dialog", Font.BOLD, 9));
		lblMacVar.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblMacVar = new GridBagConstraints();
		gbc_lblMacVar.fill = GridBagConstraints.BOTH;
		gbc_lblMacVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblMacVar.gridx = 1;
		gbc_lblMacVar.gridy = 1;
		panel_2.add(lblMacVar, gbc_lblMacVar);

		JLabel lblType = new JLabel("Type:");
		lblType.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.fill = GridBagConstraints.BOTH;
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 2;
		panel_2.add(lblType, gbc_lblType);

		lblTypeVar = new JLabel("Loading...");
		lblTypeVar.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTypeVar.setFont(new Font("Dialog", Font.BOLD, 9));
		GridBagConstraints gbc_lblTypeVar = new GridBagConstraints();
		gbc_lblTypeVar.fill = GridBagConstraints.BOTH;
		gbc_lblTypeVar.insets = new Insets(0, 0, 5, 0);
		gbc_lblTypeVar.gridx = 1;
		gbc_lblTypeVar.gridy = 2;
		panel_2.add(lblTypeVar, gbc_lblTypeVar);

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

						String[] tParts = txs.split(" ");
						String[] rParts = rxs.split(" ");
						t = Double.parseDouble(tParts[0]);
						r = Double.parseDouble(rParts[0]);
					} else {
						t = Double.NaN;
						r = Double.NaN;
					}

					val_usage.setText(String.format("DN: %s UP: %s", rxs, txs));

					last = new Date();
					tx.addPoint(time, t);
					rx.addPoint(time, r);
					chart.getAxisX().getRangePolicy().setRange(new Range(time, time - (60 * updatePeriod)));

					// set dynamic attributes
					// temperature
					if (profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TEMP) != null) {
						lblTypeVar.setText(profile.getPrimaryCPU().queryAttribute(AttributeGroupType.CPU_TEMP).get());
					}

				}

			}

		}, 0, updatePeriod);
	}

	private ClientProfile profile;
	private InfoMaster im;
	private Timer updateTimer = new Timer();

	private JLabel lblIpVar;

	@Override
	public void setTarget(ClientProfile p) {

		// clear chart only if the new profile differs from the old
		if ((profile != null) && p.getCid() != profile.getCid()) {
			tx.removeAllPoints();
			rx.removeAllPoints();
		}

		profile = p;

		// set static attributes
		lblIpVar.setText(profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_IP).get());
		lblMacVar.setText(profile.getPrimaryNIC().queryAttribute(AttributeGroupType.NIC_MAC).get());

	}

	private SwingWorker<Void, Void> timeout = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			Thread.sleep(3000);
			return null;
		}

		@Override
		protected void done() {
			if (lblTypeVar.getText().equals("Loading...")) {
				lblTypeVar.setText("N/A");
			}
		};

	};

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
