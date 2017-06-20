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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class FullSettings extends JPanel {

	private static final long serialVersionUID = 1L;

	public JComboBox monitorBox;
	public JComboBox methodBox;
	public JComboBox colorBox;
	public JComboBox compBox;

	public FullSettings() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setPreferredSize(new Dimension(305, 92));
		setLayout(null);

		monitorBox = new JComboBox();
		monitorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		monitorBox.setBounds(130, 6, 164, 18);
		add(monitorBox);

		methodBox = new JComboBox();
		methodBox.setFont(new Font("Dialog", Font.BOLD, 10));
		methodBox.setBounds(130, 26, 164, 18);
		add(methodBox);

		JLabel lblCaptureDevice = new JLabel("Capture Device:");
		lblCaptureDevice.setIcon(UIUtil.getIcon("icons16/general/viewer.png"));
		lblCaptureDevice.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCaptureDevice.setBounds(12, 6, 117, 18);
		add(lblCaptureDevice);

		JLabel lblCapture = new JLabel("Capture Mode:");
		lblCapture.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblCapture.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCapture.setBounds(12, 26, 117, 18);
		add(lblCapture);

		JLabel lblColorQuality = new JLabel("Color Mode:");
		lblColorQuality.setIcon(UIUtil.getIcon("icons16/general/palette.png"));
		lblColorQuality.setFont(new Font("Dialog", Font.BOLD, 10));
		lblColorQuality.setBounds(12, 46, 117, 18);
		add(lblColorQuality);

		colorBox = new JComboBox();
		colorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		colorBox.setBounds(130, 46, 164, 18);
		add(colorBox);

		JLabel lblCompression = new JLabel("Compression:");
		lblCompression.setIcon(UIUtil.getIcon("icons16/general/compress.png"));
		lblCompression.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCompression.setBounds(12, 66, 117, 18);
		add(lblCompression);

		compBox = new JComboBox();
		compBox.setFont(new Font("Dialog", Font.BOLD, 10));
		compBox.setBounds(130, 66, 164, 18);
		add(compBox);

	}

}
