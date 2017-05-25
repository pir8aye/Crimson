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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.attribute.keys.AKeyCPU;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.net.stream.StreamStore;
import com.subterranean_security.crimson.core.net.stream.info.InfoMaster;
import com.subterranean_security.crimson.core.net.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.util.ProtoUtil;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.net.stream.VInfoSlave;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class MenuControls extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int width = 400;
	private static final int length = 175;
	private JLabel valViewerRamUsage;
	private JLabel lblServerMemUsage;
	private JLabel lblViewerCpuUsage;
	private JLabel valViewerCpuUsage;
	private JLabel valViewerCpuTemp;
	private JLabel valServerRamUsage;
	private JLabel valServerCpuUsage;
	private JLabel valServerCpuTemp;

	public static MenuControls mc;
	private JLabel valClients;
	private JLabel valUsers;
	private JLabel valStatus;
	private JLabel val_local_ip;
	private JLabel val_server_ip;
	private JLabel valUsername;

	public MenuControls() {
		init();
	}

	public void init() {
		mc = this;
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setSize(new Dimension(400, 172));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Server",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(0, 0, 198, 168);
		panel.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblConnections = new JLabel("Status:");
		lblConnections.setIcon(UIUtil.getIcon("icons16/general/server.png"));
		lblConnections.setFont(new Font("Dialog", Font.BOLD, 10));
		lblConnections.setBounds(6, 17, 67, 17);
		panel_1.add(lblConnections);

		valStatus = new JLabel("Loading...");
		valStatus.setFont(new Font("Dialog", Font.BOLD, 10));
		valStatus.setHorizontalAlignment(SwingConstants.TRAILING);
		valStatus.setBounds(91, 17, 95, 17);
		panel_1.add(valStatus);

		JLabel lblLoggedInUsers = new JLabel("Users connected:");
		lblLoggedInUsers.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		lblLoggedInUsers.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLoggedInUsers.setBounds(6, 57, 115, 17);
		panel_1.add(lblLoggedInUsers);

		JLabel lblTotalConn = new JLabel("Clients connected:");
		lblTotalConn.setIcon(UIUtil.getIcon("icons16/general/users_3.png"));
		lblTotalConn.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTotalConn.setBounds(6, 74, 115, 17);
		panel_1.add(lblTotalConn);

		JLabel lblServerCpuTemp = new JLabel("CPU temperature:");
		lblServerCpuTemp.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblServerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerCpuTemp.setBounds(6, 97, 115, 17);
		panel_1.add(lblServerCpuTemp);

		JLabel lblServerCpuUsage = new JLabel("CPU footprint:");
		lblServerCpuUsage.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblServerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerCpuUsage.setBounds(6, 120, 115, 17);
		panel_1.add(lblServerCpuUsage);

		lblServerMemUsage = new JLabel("RAM footprint:");
		lblServerMemUsage.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		lblServerMemUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerMemUsage.setBounds(6, 137, 115, 17);
		panel_1.add(lblServerMemUsage);

		valUsers = new JLabel("loading...");
		valUsers.setHorizontalAlignment(SwingConstants.TRAILING);
		valUsers.setFont(new Font("Dialog", Font.BOLD, 10));
		valUsers.setBounds(121, 57, 70, 17);
		panel_1.add(valUsers);

		valClients = new JLabel("loading...");
		valClients.setHorizontalAlignment(SwingConstants.TRAILING);
		valClients.setFont(new Font("Dialog", Font.BOLD, 10));
		valClients.setBounds(121, 74, 70, 17);
		panel_1.add(valClients);

		valServerCpuTemp = new JLabel("loading...");
		valServerCpuTemp.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerCpuTemp.setBounds(121, 97, 70, 17);
		panel_1.add(valServerCpuTemp);

		valServerCpuUsage = new JLabel("loading...");
		valServerCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerCpuUsage.setBounds(121, 120, 70, 17);
		panel_1.add(valServerCpuUsage);

		valServerRamUsage = new JLabel("loading...");
		valServerRamUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valServerRamUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valServerRamUsage.setBounds(121, 137, 70, 17);
		panel_1.add(valServerRamUsage);

		JLabel label = new JLabel("IP Address:");
		label.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setBounds(6, 34, 88, 17);
		panel_1.add(label);

		val_server_ip = new JLabel("loading...");
		val_server_ip.setHorizontalAlignment(SwingConstants.TRAILING);
		val_server_ip.setFont(new Font("Dialog", Font.BOLD, 10));
		val_server_ip.setBounds(87, 34, 104, 17);
		panel_1.add(val_server_ip);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229), 1, true), "Local",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(198, 0, 198, 168);
		panel.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUsername.setBounds(6, 17, 94, 17);
		panel_2.add(lblUsername);

		valUsername = new JLabel("loading...");
		valUsername.setHorizontalAlignment(SwingConstants.TRAILING);
		valUsername.setFont(new Font("Dialog", Font.BOLD, 10));
		valUsername.setBounds(87, 17, 104, 17);
		panel_2.add(valUsername);

		JLabel lblViewerRamFootprint = new JLabel("RAM footprint:");
		lblViewerRamFootprint.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		lblViewerRamFootprint.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerRamFootprint.setBounds(6, 137, 115, 17);
		panel_2.add(lblViewerRamFootprint);

		lblViewerCpuUsage = new JLabel("CPU footprint:");
		lblViewerCpuUsage.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblViewerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerCpuUsage.setBounds(6, 120, 115, 17);
		panel_2.add(lblViewerCpuUsage);

		JLabel lblViewerCpuTemp = new JLabel("CPU temperature:");
		lblViewerCpuTemp.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblViewerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		lblViewerCpuTemp.setBounds(6, 97, 115, 17);
		panel_2.add(lblViewerCpuTemp);

		valViewerCpuTemp = new JLabel("loading...");
		valViewerCpuTemp.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerCpuTemp.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerCpuTemp.setBounds(121, 97, 70, 17);
		panel_2.add(valViewerCpuTemp);

		valViewerCpuUsage = new JLabel("loading...");
		valViewerCpuUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerCpuUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerCpuUsage.setBounds(121, 120, 70, 17);
		panel_2.add(valViewerCpuUsage);

		valViewerRamUsage = new JLabel("loading...");
		valViewerRamUsage.setHorizontalAlignment(SwingConstants.TRAILING);
		valViewerRamUsage.setFont(new Font("Dialog", Font.BOLD, 10));
		valViewerRamUsage.setBounds(121, 137, 70, 17);
		panel_2.add(valViewerRamUsage);

		JLabel lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		lblIpAddress.setFont(new Font("Dialog", Font.BOLD, 10));
		lblIpAddress.setBounds(6, 34, 88, 17);
		panel_2.add(lblIpAddress);

		val_local_ip = new JLabel("loading...");
		val_local_ip.setHorizontalAlignment(SwingConstants.TRAILING);
		val_local_ip.setFont(new Font("Dialog", Font.BOLD, 10));
		val_local_ip.setBounds(87, 34, 104, 17);
		panel_2.add(val_local_ip);

		add(Box.createHorizontalStrut(width), BorderLayout.SOUTH);
		add(Box.createVerticalStrut(length), BorderLayout.EAST);

	}

	public void refresh() {
		ServerProfile sp = ProfileStore.getServer();
		ViewerProfile vp = ProfileStore.getLocalViewer();

		valViewerRamUsage.setText(vp.get(AKeySimple.CLIENT_RAM_USAGE));
		// valViewerCpuTemp.setText(ProfileStore.getLocalClient().getPrimaryCPU().getAttribute(AKeyCPU.CPU_TEMP).get());
		valViewerCpuUsage.setText(vp.get(AKeySimple.CLIENT_CPU_USAGE) + " %");
		valServerRamUsage.setText(ViewerState.isOnline() ? sp.get(AKeySimple.CLIENT_RAM_USAGE) : "");
		// valServerCpuTemp.setText(ViewerState.isOnline() ?
		// ProfileStore.getServer().getCpuTemp() : "");
		valServerCpuUsage.setText(ViewerState.isOnline() ? sp.get(AKeySimple.CLIENT_CPU_USAGE) + " %" : "");
		valClients.setText(ViewerState.isOnline() ? "" + sp.get(AKeySimple.SERVER_CONNECTED_CLIENTS) : "");
		valUsers.setText(ViewerState.isOnline() ? "" + sp.get(AKeySimple.SERVER_CONNECTED_VIEWERS) : "");
		val_local_ip.setText(vp.get(AKeySimple.VIEWER_LOGIN_IP));
		val_server_ip.setText(ViewerState.isOnline() ? sp.get(AKeySimple.NET_EXTERNALIP) : "");
		valUsername.setText(vp.get(AKeySimple.VIEWER_USER));

		if (!ViewerState.isOnline()) {
			valStatus.setText("Offline");
			valStatus.setForeground(Color.gray);// TODO

		} else {
			valStatus.setText("Online");
			// valStatus.setForeground(new Color(200, 0, 0));

		}

	}

	private InfoMaster im;
	private InfoSlave is;
	private static final InfoParam param = ProtoUtil
			.getInfoParam(AKeyCPU.CPU_TEMP, AKeySimple.CLIENT_RAM_USAGE, AKeySimple.CLIENT_CPU_USAGE).build();

	public void startStreams() {

		im = new InfoMaster(param, 1000);
		StreamStore.addStream(im);
		is = new VInfoSlave(param);
		StreamStore.addStream(is);
	}

	public void stopStreams() {
		StreamStore.removeStreamBySID(im.getStreamID());
		StreamStore.removeStreamBySID(is.getStreamID());
	}
}
