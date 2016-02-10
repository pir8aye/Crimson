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
package com.subterranean_security.crimson.viewer.ui.screen.settings;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.storage.Headers;

public class HostListHeaders extends JPanel {

	private static final long serialVersionUID = 1L;

	//TODO FINISH
	public JCheckBox chckbxLocation = new JCheckBox(Headers.COUNTRY.toString());
	public JCheckBox chckbxLanguage = new JCheckBox(Headers.LANGUAGE.toString());
	public JCheckBox chckbxCrimsonVersion = new JCheckBox(Headers.CRIMSON_VERSION.toString());
	public JCheckBox chckbxTimezone = new JCheckBox("Timezone");
	public JCheckBox chckbxHostname = new JCheckBox("Hostname");
	public JCheckBox chckbxUsername = new JCheckBox("Username");
	public JCheckBox chckbxInternalIp = new JCheckBox("Internal IP");
	public JCheckBox chckbxExternalIp = new JCheckBox("External IP");
	public JCheckBox chckbxOperatingSystem = new JCheckBox("Operating System");
	public JCheckBox chckbxRamCapacity = new JCheckBox("RAM Capacity");
	public JCheckBox chckbxUserStatus = new JCheckBox("User Status");

	public JCheckBox chckbxMessagePing = new JCheckBox("Message Ping");
	public JCheckBox chckbxRamUsage = new JCheckBox("RAM Usage");
	public JCheckBox chckbxCpuTemp = new JCheckBox("CPU Temp");
	public JCheckBox chckbxCpuUsage = new JCheckBox("CPU Usage");
	public JCheckBox chckbxActiveWindow = new JCheckBox("Active Window");
	public JCheckBox chckbxScreenPreview = new JCheckBox("Screen Preview");

	public HostListHeaders() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		panel.setLayout(new GridLayout(5, 3, 0, 0));

		panel.add(chckbxLocation);
		panel.add(chckbxLanguage);
		panel.add(chckbxCrimsonVersion);
		panel.add(chckbxTimezone);
		panel.add(chckbxHostname);
		panel.add(chckbxUsername);
		panel.add(chckbxInternalIp);
		panel.add(chckbxExternalIp);
		panel.add(chckbxOperatingSystem);
		panel.add(chckbxRamCapacity);
		panel.add(chckbxUserStatus);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Dynamic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1);
		panel_1.setLayout(new GridLayout(5, 3, 0, 0));

		panel_1.add(chckbxMessagePing);
		panel_1.add(chckbxRamUsage);
		panel_1.add(chckbxCpuTemp);
		panel_1.add(chckbxCpuUsage);
		panel_1.add(chckbxActiveWindow);
		panel_1.add(chckbxScreenPreview);

	}

}
