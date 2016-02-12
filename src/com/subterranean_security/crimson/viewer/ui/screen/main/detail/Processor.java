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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.net.Stream.InfoParam;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoMaster;
import com.subterranean_security.crimson.sv.Profile;

public class DynamicProperty extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	public DynamicProperty() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Processor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblGraph = new JLabel("GRAPH");
		GridBagConstraints gbc_lblGraph = new GridBagConstraints();
		gbc_lblGraph.insets = new Insets(0, 0, 5, 0);
		gbc_lblGraph.gridx = 0;
		gbc_lblGraph.gridy = 0;
		panel.add(lblGraph, gbc_lblGraph);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 305, 129, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblTotalCpuUsage = new JLabel("Total CPU Usage:");
		GridBagConstraints gbc_lblTotalCpuUsage = new GridBagConstraints();
		gbc_lblTotalCpuUsage.anchor = GridBagConstraints.WEST;
		gbc_lblTotalCpuUsage.insets = new Insets(0, 0, 5, 0);
		gbc_lblTotalCpuUsage.gridx = 0;
		gbc_lblTotalCpuUsage.gridy = 0;
		panel_2.add(lblTotalCpuUsage, gbc_lblTotalCpuUsage);

		JLabel label = new JLabel("0%");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 1;
		gbc_label.gridy = 0;
		panel_2.add(label, gbc_label);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "RAM", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1);
	}

	private int streamID;
	private Profile profile;
	private InfoMaster im;

	@Override
	public void setTarget(Profile p) {
		StreamStore.removeStream(streamID);
		if (p != null) {
			profile = p;
			im = new InfoMaster(InfoParam.newBuilder().build());
			StreamStore.addStream(im.getStreamID(), im);
			im.start();
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

}
