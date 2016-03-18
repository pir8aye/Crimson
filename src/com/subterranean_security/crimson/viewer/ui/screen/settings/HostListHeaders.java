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

	// TODO FINISH
	public JCheckBox chckbxCountry = new JCheckBox(Headers.COUNTRY.toString());
	public JCheckBox chckbxLanguage = new JCheckBox(Headers.LANGUAGE.toString());
	public JCheckBox chckbxCrimsonVersion = new JCheckBox(Headers.CRIMSON_VERSION.toString());
	public JCheckBox chckbxTimezone = new JCheckBox("Timezone");
	public JCheckBox chckbxHostname = new JCheckBox("Hostname");
	public JCheckBox chckbxUsername = new JCheckBox("Username");
	public JCheckBox chckbxInternalIp = new JCheckBox("Internal IP");
	public JCheckBox chckbxExternalIp = new JCheckBox("External IP");
	public JCheckBox chckbxOSFamily = new JCheckBox("Operating System");
	public JCheckBox chckbxRamCapacity = new JCheckBox("RAM Capacity");
	public JCheckBox chckbxUserStatus = new JCheckBox("User Status");

	public JCheckBox chckbxMessagePing = new JCheckBox("Message Ping");
	public JCheckBox chckbxRamUsage = new JCheckBox("RAM Usage");
	public JCheckBox chckbxCpuTemp = new JCheckBox("CPU Temp");
	public JCheckBox chckbxCpuUsage = new JCheckBox("CPU Usage");
	public JCheckBox chckbxActiveWindow = new JCheckBox("Active Window");
	public JCheckBox chckbxScreenPreview = new JCheckBox("Screen Preview");

	public JCheckBox chckbxMonitorCount = new JCheckBox("Monitor Count");
	public JCheckBox chckbxOSArch = new JCheckBox("OS Architecture");
	public JCheckBox chckbxVirtualization = new JCheckBox("Virtualization");
	public JCheckBox chckbxJavaVersion = new JCheckBox("Java Version");
	public final JCheckBox chckbxCpuModel = new JCheckBox("CPU Model");

	public HostListHeaders() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel static_panel = new JPanel();
		static_panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(static_panel);
		static_panel.setLayout(new GridLayout(5, 3, 0, 0));

		static_panel.add(chckbxCountry);
		static_panel.add(chckbxLanguage);
		static_panel.add(chckbxCrimsonVersion);
		static_panel.add(chckbxTimezone);
		static_panel.add(chckbxHostname);
		static_panel.add(chckbxUsername);
		static_panel.add(chckbxInternalIp);
		static_panel.add(chckbxExternalIp);
		static_panel.add(chckbxOSFamily);
		static_panel.add(chckbxRamCapacity);

		static_panel.add(chckbxMonitorCount);
		static_panel.add(chckbxOSArch);
		static_panel.add(chckbxVirtualization);
		static_panel.add(chckbxJavaVersion);
		
		static_panel.add(chckbxCpuModel);

		JPanel dynamic_panel = new JPanel();
		dynamic_panel.setBorder(new TitledBorder(null, "Dynamic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(dynamic_panel);
		dynamic_panel.setLayout(new GridLayout(5, 3, 0, 0));

		dynamic_panel.add(chckbxMessagePing);
		dynamic_panel.add(chckbxUserStatus);
		dynamic_panel.add(chckbxRamUsage);
		dynamic_panel.add(chckbxCpuTemp);
		dynamic_panel.add(chckbxCpuUsage);
		dynamic_panel.add(chckbxActiveWindow);
		dynamic_panel.add(chckbxScreenPreview);

	}

}
