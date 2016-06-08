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
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class HostListHeaders extends JPanel {

	private static final long serialVersionUID = 1L;

	public JCheckBox chckbxCountry = new JCheckBox(Headers.COUNTRY.toString());
	public JCheckBox chckbxLanguage = new JCheckBox(Headers.LANGUAGE.toString());
	public JCheckBox chckbxCrimsonVersion = new JCheckBox(Headers.CRIMSON_VERSION.toString());
	public JCheckBox chckbxTimezone = new JCheckBox(Headers.TIMEZONE.toString());
	public JCheckBox chckbxHostname = new JCheckBox(Headers.HOSTNAME.toString());
	public JCheckBox chckbxUsername = new JCheckBox(Headers.USERNAME.toString());
	public JCheckBox chckbxInternalIp = new JCheckBox(Headers.INTERNAL_IP.toString());
	public JCheckBox chckbxExternalIp = new JCheckBox(Headers.EXTERNAL_IP.toString());
	public JCheckBox chckbxOSFamily = new JCheckBox(Headers.OS_FAMILY.toString());
	public JCheckBox chckbxRamCapacity = new JCheckBox(Headers.RAM_CAPACITY.toString());
	public JCheckBox chckbxUserStatus = new JCheckBox(Headers.USER_STATUS.toString());

	public JCheckBox chckbxMessagePing = new JCheckBox(Headers.MESSAGE_PING.toString());
	public JCheckBox chckbxRamUsage = new JCheckBox(Headers.RAM_USAGE.toString());
	public JCheckBox chckbxCpuTemp = new JCheckBox(Headers.CPU_TEMP.toString());
	public JCheckBox chckbxCpuUsage = new JCheckBox(Headers.CPU_USAGE.toString());
	public JCheckBox chckbxActiveWindow = new JCheckBox(Headers.ACTIVE_WINDOW.toString());
	public JCheckBox chckbxScreenPreview = new JCheckBox(Headers.SCREEN_PREVIEW.toString());

	public JCheckBox chckbxMonitorCount = new JCheckBox(Headers.MONITOR_COUNT.toString());
	public JCheckBox chckbxOSArch = new JCheckBox(Headers.OS_ARCH.toString());
	public JCheckBox chckbxVirtualization = new JCheckBox(Headers.VIRTUALIZATION.toString());
	public JCheckBox chckbxJavaVersion = new JCheckBox(Headers.JAVA_VERSION.toString());
	public JCheckBox chckbxCpuModel = new JCheckBox(Headers.CPU_MODEL.toString());
	public JCheckBox chckbxOsName = new JCheckBox(Headers.OS_NAME.toString());
	public JCheckBox chckbxCvid = new JCheckBox(Headers.CVID.toString());

	public HostListHeaders() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel static_panel = new JPanel();
		static_panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(static_panel);
		GridBagLayout gbl_static_panel = new GridBagLayout();
		gbl_static_panel.columnWidths = new int[] { 173, 173, 173, 0 };
		gbl_static_panel.rowHeights = new int[] { 25, 25, 25, 25, 25, 0, 0 };
		gbl_static_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_static_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		static_panel.setLayout(gbl_static_panel);
		chckbxCountry.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxCountry = new GridBagConstraints();
		gbc_chckbxCountry.fill = GridBagConstraints.BOTH;
		gbc_chckbxCountry.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCountry.gridx = 0;
		gbc_chckbxCountry.gridy = 0;
		static_panel.add(chckbxCountry, gbc_chckbxCountry);
		chckbxLanguage.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxLanguage = new GridBagConstraints();
		gbc_chckbxLanguage.fill = GridBagConstraints.BOTH;
		gbc_chckbxLanguage.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLanguage.gridx = 1;
		gbc_chckbxLanguage.gridy = 0;
		static_panel.add(chckbxLanguage, gbc_chckbxLanguage);
		chckbxCrimsonVersion.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxCrimsonVersion = new GridBagConstraints();
		gbc_chckbxCrimsonVersion.fill = GridBagConstraints.BOTH;
		gbc_chckbxCrimsonVersion.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxCrimsonVersion.gridx = 2;
		gbc_chckbxCrimsonVersion.gridy = 0;
		static_panel.add(chckbxCrimsonVersion, gbc_chckbxCrimsonVersion);
		chckbxTimezone.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxTimezone = new GridBagConstraints();
		gbc_chckbxTimezone.fill = GridBagConstraints.BOTH;
		gbc_chckbxTimezone.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxTimezone.gridx = 0;
		gbc_chckbxTimezone.gridy = 1;
		static_panel.add(chckbxTimezone, gbc_chckbxTimezone);
		chckbxHostname.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxHostname = new GridBagConstraints();
		gbc_chckbxHostname.fill = GridBagConstraints.BOTH;
		gbc_chckbxHostname.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxHostname.gridx = 1;
		gbc_chckbxHostname.gridy = 1;
		static_panel.add(chckbxHostname, gbc_chckbxHostname);
		chckbxUsername.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxUsername = new GridBagConstraints();
		gbc_chckbxUsername.fill = GridBagConstraints.BOTH;
		gbc_chckbxUsername.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxUsername.gridx = 2;
		gbc_chckbxUsername.gridy = 1;
		static_panel.add(chckbxUsername, gbc_chckbxUsername);
		chckbxInternalIp.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxInternalIp = new GridBagConstraints();
		gbc_chckbxInternalIp.fill = GridBagConstraints.BOTH;
		gbc_chckbxInternalIp.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxInternalIp.gridx = 0;
		gbc_chckbxInternalIp.gridy = 2;
		static_panel.add(chckbxInternalIp, gbc_chckbxInternalIp);
		chckbxExternalIp.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxExternalIp = new GridBagConstraints();
		gbc_chckbxExternalIp.fill = GridBagConstraints.BOTH;
		gbc_chckbxExternalIp.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxExternalIp.gridx = 1;
		gbc_chckbxExternalIp.gridy = 2;
		static_panel.add(chckbxExternalIp, gbc_chckbxExternalIp);

		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxNewCheckBox.gridx = 2;
		gbc_chckbxNewCheckBox.gridy = 2;
		chckbxOsName.setFont(new Font("Dialog", Font.BOLD, 11));
		static_panel.add(chckbxOsName, gbc_chckbxNewCheckBox);
		chckbxRamCapacity.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxRamCapacity = new GridBagConstraints();
		gbc_chckbxRamCapacity.fill = GridBagConstraints.BOTH;
		gbc_chckbxRamCapacity.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxRamCapacity.gridx = 0;
		gbc_chckbxRamCapacity.gridy = 3;
		static_panel.add(chckbxRamCapacity, gbc_chckbxRamCapacity);
		chckbxMonitorCount.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxMonitorCount = new GridBagConstraints();
		gbc_chckbxMonitorCount.fill = GridBagConstraints.BOTH;
		gbc_chckbxMonitorCount.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMonitorCount.gridx = 1;
		gbc_chckbxMonitorCount.gridy = 3;
		static_panel.add(chckbxMonitorCount, gbc_chckbxMonitorCount);
		chckbxOSFamily.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxOSFamily = new GridBagConstraints();
		gbc_chckbxOSFamily.fill = GridBagConstraints.BOTH;
		gbc_chckbxOSFamily.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxOSFamily.gridx = 2;
		gbc_chckbxOSFamily.gridy = 3;
		static_panel.add(chckbxOSFamily, gbc_chckbxOSFamily);
		chckbxVirtualization.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxVirtualization = new GridBagConstraints();
		gbc_chckbxVirtualization.fill = GridBagConstraints.BOTH;
		gbc_chckbxVirtualization.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxVirtualization.gridx = 0;
		gbc_chckbxVirtualization.gridy = 4;
		static_panel.add(chckbxVirtualization, gbc_chckbxVirtualization);
		chckbxJavaVersion.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxJavaVersion = new GridBagConstraints();
		gbc_chckbxJavaVersion.fill = GridBagConstraints.BOTH;
		gbc_chckbxJavaVersion.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxJavaVersion.gridx = 1;
		gbc_chckbxJavaVersion.gridy = 4;
		static_panel.add(chckbxJavaVersion, gbc_chckbxJavaVersion);
		chckbxOSArch.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxOSArch = new GridBagConstraints();
		gbc_chckbxOSArch.fill = GridBagConstraints.BOTH;
		gbc_chckbxOSArch.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxOSArch.gridx = 2;
		gbc_chckbxOSArch.gridy = 4;
		static_panel.add(chckbxOSArch, gbc_chckbxOSArch);

		GridBagConstraints gbc_chckbxCvid = new GridBagConstraints();
		gbc_chckbxCvid.anchor = GridBagConstraints.WEST;
		gbc_chckbxCvid.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxCvid.gridx = 0;
		gbc_chckbxCvid.gridy = 5;
		chckbxCvid.setFont(new Font("Dialog", Font.BOLD, 11));
		static_panel.add(chckbxCvid, gbc_chckbxCvid);
		chckbxCpuModel.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_chckbxCpuModel = new GridBagConstraints();
		gbc_chckbxCpuModel.fill = GridBagConstraints.BOTH;
		gbc_chckbxCpuModel.gridx = 2;
		gbc_chckbxCpuModel.gridy = 5;
		static_panel.add(chckbxCpuModel, gbc_chckbxCpuModel);

		JPanel dynamic_panel = new JPanel();
		dynamic_panel.setBorder(new TitledBorder(null, "Dynamic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(dynamic_panel);
		dynamic_panel.setLayout(new GridLayout(5, 3, 0, 0));
		chckbxMessagePing.setFont(new Font("Dialog", Font.BOLD, 11));

		dynamic_panel.add(chckbxMessagePing);
		chckbxUserStatus.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxUserStatus);
		chckbxRamUsage.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxRamUsage);
		chckbxCpuTemp.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxCpuTemp);
		chckbxCpuUsage.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxCpuUsage);
		chckbxActiveWindow.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxActiveWindow);
		chckbxScreenPreview.setFont(new Font("Dialog", Font.BOLD, 11));
		dynamic_panel.add(chckbxScreenPreview);

	}

}
