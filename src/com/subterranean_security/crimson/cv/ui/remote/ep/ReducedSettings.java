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
package com.subterranean_security.crimson.cv.ui.remote.ep;

import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.UIUtil;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.EtchedBorder;
import java.awt.Dimension;
import java.awt.Font;

public class ReducedSettings extends JPanel {

	private static final long serialVersionUID = 1L;
	public JComboBox monitorBox;
	public JComboBox colorBox;

	public ReducedSettings() {
		setPreferredSize(new Dimension(108, 55));
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(null);

		JLabel lblCaptureDevice = new JLabel();
		lblCaptureDevice.setIcon(UIUtil.getIcon("icons16/general/viewer.png"));
		lblCaptureDevice.setBounds(6, 6, 16, 18);
		add(lblCaptureDevice);

		JLabel lblColorQuality = new JLabel();
		lblColorQuality.setIcon(UIUtil.getIcon("icons16/general/palette.png"));
		lblColorQuality.setBounds(6, 26, 16, 18);
		add(lblColorQuality);

		monitorBox = new JComboBox();
		monitorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		monitorBox.setBounds(30, 6, 68, 18);
		add(monitorBox);

		colorBox = new JComboBox();
		colorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		colorBox.setBounds(30, 26, 68, 18);
		add(colorBox);

	}
}
