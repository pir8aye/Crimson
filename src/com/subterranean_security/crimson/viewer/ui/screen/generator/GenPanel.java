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
package com.subterranean_security.crimson.viewer.ui.screen.generator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.Misc.Group;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.components.EntropyHarvester;

public class GenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public JTextField fld_path;
	private JPanel otab;
	private JPanel ntab;
	private JPanel etab;
	private JPanel optab;
	public JTextField fld_ctime;
	private StatusLabel lbl_status;
	private JCheckBox cbx_waiver;
	private JTextField fld_group_name;
	private JTextArea txt_output_desc;
	private NetworkTargetTable table;
	private JTextField fld_server;
	private JTextField fld_port;

	private static final String jar = "Runnable Java Archive (.jar)";
	private static final String exe = "Windows Portable Executable (.exe)";
	private static final String apk = "Android Application (.apk)";
	private static final String sh = "Shell Script (.sh)";
	private JSpinner fld_delay;
	private JSpinner fld_connect_period;

	private String group_text = "Group authentication is the most secure mechanism. A \"group key\" is embedded in the client and only servers that posses this key may authenticate with the client and vice versa.";
	private String pass_text = "A simple password is used to authenticate the client. This is less secure than group authentication";
	private String none_text = "The client will request to skip authentication entirely.";

	private String[] ipath_win = new String[] { "C:/Users/%USERNAME%/Documents/Crimson" };
	private String[] ipath_lin = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_osx = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_sol = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_bsd = new String[] { "/home/%USERNAME%/.crimson" };

	private static final String defaultHint = "set or load generation options";

	private Timer timer = new Timer();
	private GroupTimer gt = new GroupTimer();

	public GenPanel() {
		init();

		changeToJar();

		if (Common.isDebugMode()) {
			table.add(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build());
			fld_path.setText("C:/Users/dev/Desktop/client.jar");
			cbx_waiver.setSelected(true);
		}
		timer.schedule(gt, 0, 750);
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);

		optab = new JPanel();
		tabbedPane.addTab(null, optab);
		tabbedPane.setTabComponentAt(0, new GenTabComponent("link", "Setup"));
		optab.setLayout(new BoxLayout(optab, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();

		optab.add(panel_3);

		type_comboBox = new JComboBox<String>();
		type_comboBox.setEnabled(false);
		type_comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch ((String) type_comboBox.getSelectedItem()) {
				case jar: {
					changeToJar();
					return;
				}
				case exe: {
					changeToExe();
					return;
				}
				case apk: {
					changeToApk();
					return;
				}
				case sh: {
					changeToSh();
					return;
				}
				}
			}
		});
		panel_3.setLayout(new BorderLayout(0, 0));
		type_comboBox.setFont(new Font("Dialog", Font.BOLD, 11));
		type_comboBox.setModel(new DefaultComboBoxModel(new String[] { "Runnable Java Archive (.jar)",
				"Windows Portable Executable (.exe)", "Android Application (.apk)", "Shell Script (.sh)" }));
		panel_3.add(type_comboBox, BorderLayout.NORTH);

		JPanel panel_100 = new JPanel();

		panel_3.add(panel_100, BorderLayout.CENTER);
		panel_100.setLayout(new BorderLayout(0, 0));

		txt_output_desc = new JTextArea();
		txt_output_desc.setWrapStyleWord(true);
		txt_output_desc.setFont(new Font("Dialog", Font.PLAIN, 11));
		txt_output_desc.setLineWrap(true);
		txt_output_desc.setEditable(false);
		txt_output_desc.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txt_output_desc.setOpaque(false);
		panel_100.add(txt_output_desc);
		panel_100.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		panel_3.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
		panel_3.add(Box.createHorizontalStrut(5), BorderLayout.WEST);

		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(300, 60));
		panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Platform Compatibility", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_2.setLayout(null);

		lblWindows = new JLabel("Windows");
		lblWindows.setToolTipText("Runs on Vista through Windows 10");
		lblWindows.setFont(new Font("Dialog", Font.BOLD, 10));
		lblWindows.setBounds(12, 18, 70, 15);
		panel_2.add(lblWindows);

		lblSolaris = new JLabel("Solaris");
		lblSolaris.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSolaris.setBounds(12, 37, 70, 15);
		panel_2.add(lblSolaris);

		lblLinux = new JLabel("Linux");
		lblLinux.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLinux.setBounds(132, 18, 70, 15);
		panel_2.add(lblLinux);

		lblBsd = new JLabel("BSD");
		lblBsd.setFont(new Font("Dialog", Font.BOLD, 10));
		lblBsd.setBounds(132, 37, 70, 15);
		panel_2.add(lblBsd);

		lblMacOsX = new JLabel("Mac OS X");
		lblMacOsX.setFont(new Font("Dialog", Font.BOLD, 10));
		lblMacOsX.setBounds(214, 18, 70, 15);
		panel_2.add(lblMacOsX);

		lblAndroid = new JLabel("Android");
		lblAndroid.setFont(new Font("Dialog", Font.BOLD, 10));
		lblAndroid.setBounds(214, 37, 70, 15);
		panel_2.add(lblAndroid);
		panel_3.add(panel_2, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(null, "Optional Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		optab.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_15 = new JPanel();

		panel_1.add(panel_15, BorderLayout.CENTER);
		panel_15.setLayout(new BorderLayout(0, 0));

		JLabel lblInstallMessage = new JLabel("Install Message:");
		lblInstallMessage.setVerticalAlignment(SwingConstants.TOP);
		panel_15.add(lblInstallMessage, BorderLayout.WEST);
		lblInstallMessage.setFont(new Font("Dialog", Font.BOLD, 10));

		JScrollPane scrollPane = new JScrollPane();
		panel_15.add(scrollPane, BorderLayout.EAST);
		panel_15.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		panel_15.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

		fld_install_message = new JTextArea();
		fld_install_message.setColumns(20);
		fld_install_message.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("a message to be displayed on client installation");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setInfo(defaultHint);
			}
		});
		fld_install_message.setLineWrap(true);
		fld_install_message.setWrapStyleWord(true);
		fld_install_message.setFont(new Font("Dialog", Font.PLAIN, 10));
		scrollPane.setViewportView(fld_install_message);

		JPanel panel_16 = new JPanel();
		panel_1.add(panel_16, BorderLayout.NORTH);
		panel_16.setLayout(new BorderLayout(0, 0));

		JLabel lblIdentifier = new JLabel("Client Identifier:");
		lblIdentifier.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_16.add(lblIdentifier, BorderLayout.WEST);

		textField = new JTextField();
		panel_16.add(textField, BorderLayout.EAST);
		textField.setColumns(15);

		etab = new JPanel();
		tabbedPane.addTab(null, etab);
		tabbedPane.setTabComponentAt(1, new GenTabComponent("checkerboard", "Execution"));
		etab.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_7 = new JPanel();
		panel_7.setPreferredSize(new Dimension(330, 100));
		panel_7.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		etab.add(panel_7);
		panel_7.setLayout(null);

		JLabel lblDelay = new JLabel("Execution delay:");
		lblDelay.setFont(new Font("Dialog", Font.BOLD, 10));
		lblDelay.setBounds(12, 20, 126, 15);
		panel_7.add(lblDelay);

		fld_delay = new JSpinner();
		fld_delay.setModel(new SpinnerNumberModel(0, 0, 3600, 1));
		fld_delay.setBounds(161, 17, 57, 19);
		panel_7.add(fld_delay);

		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSeconds.setBounds(229, 20, 57, 15);
		panel_7.add(lblSeconds);

		JCheckBox chckbxRecoverFromErrors = new JCheckBox("Recover from errors");
		chckbxRecoverFromErrors.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxRecoverFromErrors.setBounds(12, 40, 280, 23);
		panel_7.add(chckbxRecoverFromErrors);

		JCheckBox chckbxInstallWhenIdle = new JCheckBox("Install when IDLE");
		chckbxInstallWhenIdle.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxInstallWhenIdle.setBounds(12, 67, 274, 25);
		panel_7.add(chckbxInstallWhenIdle);

		JPanel ptab = new JPanel();
		tabbedPane.addTab(null, ptab);
		tabbedPane.setTabComponentAt(2, new GenTabComponent("folder", "Paths"));
		ptab.setLayout(null);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(
				new TitledBorder(null, "Installation Directory", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(12, 12, 341, 144);
		ptab.add(panel_6);
		panel_6.setLayout(null);

		fld_install_windows = new JComboBox<String>();
		fld_install_windows.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_windows.setModel(new DefaultComboBoxModel<String>(ipath_win));
		fld_install_windows.setEditable(true);
		fld_install_windows.setBounds(90, 17, 228, 19);
		panel_6.add(fld_install_windows);

		fld_install_linux = new JComboBox<String>();
		fld_install_linux.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_linux.setModel(new DefaultComboBoxModel<String>(ipath_lin));
		fld_install_linux.setEditable(true);
		fld_install_linux.setBounds(90, 42, 228, 19);
		panel_6.add(fld_install_linux);

		fld_install_osx = new JComboBox<String>();
		fld_install_osx.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_osx.setModel(new DefaultComboBoxModel<String>(ipath_osx));
		fld_install_osx.setEditable(true);
		fld_install_osx.setBounds(90, 67, 228, 19);
		panel_6.add(fld_install_osx);

		fld_install_bsd = new JComboBox<String>();
		fld_install_bsd.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_bsd.setModel(new DefaultComboBoxModel<String>(ipath_bsd));
		fld_install_bsd.setEditable(true);
		fld_install_bsd.setBounds(90, 117, 228, 19);
		panel_6.add(fld_install_bsd);

		fld_install_solaris = new JComboBox<String>();
		fld_install_solaris.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_solaris.setModel(new DefaultComboBoxModel<String>(ipath_sol));
		fld_install_solaris.setEditable(true);
		fld_install_solaris.setBounds(90, 92, 228, 19);
		panel_6.add(fld_install_solaris);

		JLabel lblWindows = new JLabel("Windows:");
		lblWindows.setFont(new Font("Dialog", Font.BOLD, 11));
		lblWindows.setBounds(12, 20, 70, 15);
		panel_6.add(lblWindows);

		JLabel lblLinux = new JLabel("Linux:");
		lblLinux.setBounds(12, 44, 70, 15);
		panel_6.add(lblLinux);

		JLabel lblOsX = new JLabel("OS X:");
		lblOsX.setBounds(12, 69, 70, 15);
		panel_6.add(lblOsX);

		JLabel lblSolaris = new JLabel("Solaris:");
		lblSolaris.setBounds(12, 94, 70, 15);
		panel_6.add(lblSolaris);

		JLabel lblBsd = new JLabel("BSD:");
		lblBsd.setBounds(12, 119, 70, 15);
		panel_6.add(lblBsd);

		ntab = new JPanel();
		tabbedPane.addTab(null, ntab);
		tabbedPane.setTabComponentAt(3, new GenTabComponent("computer", "Network"));
		ntab.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_17 = new JPanel();
		panel_17.setPreferredSize(new Dimension(354, 300));
		ntab.add(panel_17);
		panel_17.setLayout(null);

		table = new NetworkTargetTable();
		table.setBounds(12, 12, 330, 65);
		panel_17.add(table);

		JPanel panel_4 = new JPanel();
		panel_4.setBounds(12, 89, 330, 66);
		panel_17.add(panel_4);
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Add New Target",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_4.setLayout(null);

		fld_server = new JTextField();
		fld_server.setBounds(72, 17, 246, 19);
		panel_4.add(fld_server);
		fld_server.setColumns(10);

		fld_port = new JTextField();
		fld_port.setBounds(72, 39, 47, 19);
		panel_4.add(fld_port);
		fld_port.setColumns(10);

		JButton btnAdd = new JButton("Add Target");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String server = fld_server.getText();
				String port = fld_port.getText();
				if (!CUtil.Validation.port(port)) {
					lbl_status.setBad("Invalid port");
					return;
				}
				for (NetworkTarget nt : table.getTargets()) {
					if (nt.getServer().equals(server) && nt.getPort() == Integer.parseInt(port)) {
						lbl_status.setBad("Target already exists");
						return;
					}
				}
				if (CUtil.Validation.dns(server) || CUtil.Validation.ip(server)) {
					table.add(NetworkTarget.newBuilder().setServer(server).setPort(Integer.parseInt(port)).build());
					fld_server.setText("");
					fld_port.setText("");
					lbl_status.setGood("Added Target");
				} else {
					lbl_status.setBad("Invalid server address");
					return;
				}

			}
		});
		btnAdd.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAdd.setMargin(new Insets(2, 4, 2, 4));
		btnAdd.setBounds(240, 40, 78, 19);
		panel_4.add(btnAdd);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Dialog", Font.BOLD, 11));
		lblPort.setBounds(12, 43, 53, 15);
		panel_4.add(lblPort);

		JLabel lblServer = new JLabel("Server:");
		lblServer.setFont(new Font("Dialog", Font.BOLD, 11));
		lblServer.setBounds(12, 20, 53, 15);
		panel_4.add(lblServer);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(12, 168, 330, 101);
		panel_17.add(panel_5);
		panel_5.setBorder(
				new TitledBorder(null, "Network Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setLayout(null);

		fld_connect_period = new JSpinner();
		fld_connect_period.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_connect_period.setModel(new SpinnerNumberModel(20, 5, 3600, 1));
		fld_connect_period.setBounds(207, 17, 55, 20);
		panel_5.add(fld_connect_period);

		JLabel lblConnectionPeriod = new JLabel("Connection Period:");
		lblConnectionPeriod.setFont(new Font("Dialog", Font.BOLD, 10));
		lblConnectionPeriod.setBounds(12, 20, 129, 15);
		panel_5.add(lblConnectionPeriod);

		JLabel lblSeconds_1 = new JLabel("seconds");
		lblSeconds_1.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSeconds_1.setBounds(267, 20, 51, 15);
		panel_5.add(lblSeconds_1);

		JPanel atab = new JPanel();
		tabbedPane.addTab(null, atab);
		atab.setLayout(new BorderLayout(0, 0));

		JPanel panel_8 = new JPanel();
		panel_8.setBorder(
				new TitledBorder(null, "Authentication Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		atab.add(panel_8, BorderLayout.NORTH);
		panel_8.setLayout(new BorderLayout(0, 0));

		JTextArea txtrGroupAuthenticationIs = new JTextArea();
		txtrGroupAuthenticationIs.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtrGroupAuthenticationIs.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtrGroupAuthenticationIs.setOpaque(false);
		txtrGroupAuthenticationIs.setWrapStyleWord(true);
		txtrGroupAuthenticationIs.setLineWrap(true);
		txtrGroupAuthenticationIs.setText(group_text);
		panel_8.add(txtrGroupAuthenticationIs, BorderLayout.CENTER);

		JPanel panel_10 = new JPanel();
		panel_8.add(panel_10, BorderLayout.WEST);

		JPanel panel_9 = new JPanel();
		atab.add(panel_9, BorderLayout.CENTER);
		panel_9.setLayout(new CardLayout(0, 0));

		authType = new JComboBox();
		authType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch ((String) authType.getSelectedItem()) {
				case "Group": {
					txtrGroupAuthenticationIs.setText(group_text);
					((CardLayout) panel_9.getLayout()).show(panel_9, "group");
					break;
				}
				case "Password": {
					txtrGroupAuthenticationIs.setText(pass_text);
					((CardLayout) panel_9.getLayout()).show(panel_9, "Password");
					break;
				}
				case "None": {
					txtrGroupAuthenticationIs.setText(none_text);
					((CardLayout) panel_9.getLayout()).show(panel_9, "None");
					break;
				}
				}
			}
		});
		authType.setModel(new DefaultComboBoxModel(new String[] { "Group", "Password", "None" }));
		panel_10.add(authType);

		JPanel authpanel_group = new JPanel();
		panel_9.add(authpanel_group, "group");
		authpanel_group.setLayout(new BorderLayout(0, 0));

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		authpanel_group.add(panel_11, BorderLayout.NORTH);
		panel_11.setLayout(new BorderLayout(0, 0));

		JLabel lblGroupKeyPrefix = new JLabel("Group Key Prefix:");
		lblGroupKeyPrefix.setFont(new Font("Dialog", Font.BOLD, 11));
		panel_11.add(lblGroupKeyPrefix, BorderLayout.WEST);

		key_prefix = new JLabel("4 5 9 2 0 1 5 3 8 3 1 3 4 2 5 5");
		key_prefix.setFont(new Font("Dialog", Font.BOLD, 11));
		key_prefix.setHorizontalAlignment(SwingConstants.CENTER);
		panel_11.add(key_prefix, BorderLayout.CENTER);

		JPanel panel_12 = new JPanel();
		authpanel_group.add(panel_12);
		panel_12.setLayout(new BorderLayout(0, 0));

		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_12.add(panel_13, BorderLayout.NORTH);
		panel_13.setLayout(new BorderLayout(0, 0));

		JLabel lblGroupName = new JLabel("  Group Name:");
		lblGroupName.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_13.add(lblGroupName, BorderLayout.WEST);

		JPanel panel_14 = new JPanel();
		panel_13.add(panel_14, BorderLayout.EAST);

		fld_group_name = new JTextField();
		fld_group_name.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("A simple identifier for clients installed by this installer");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setInfo(defaultHint);
			}
		});
		fld_group_name.setBounds(130, 20, 117, 19);
		panel_14.add(fld_group_name);
		fld_group_name.setColumns(10);

		JButton btnRandom_1 = new JButton("Randomize");
		btnRandom_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fld_group_name.setText("Grp-" + CUtil.Misc.randString(5));
			}
		});
		btnRandom_1.setMargin(new Insets(2, 2, 2, 2));
		btnRandom_1.setFont(new Font("Dialog", Font.BOLD, 9));
		btnRandom_1.setBounds(259, 20, 70, 19);
		panel_14.add(btnRandom_1);

		EntropyHarvester eh = new EntropyHarvester();
		eh.hpanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				gt.mix(e.getPoint());
			}
		});
		panel_12.add(eh, BorderLayout.CENTER);

		JPanel authpanel_password = new JPanel();
		authpanel_password.setBorder(
				new TitledBorder(null, "Password Authentication", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_9.add(authpanel_password, "Password");

		JPanel authpanel_none = new JPanel();
		panel_9.add(authpanel_none, "None");

		JLabel lblNewLabel = new JLabel("Warning: Authentication mode is set to NONE");
		authpanel_none.add(lblNewLabel);

		JLabel lblThisClientWill = new JLabel("This client will be able to authenticate with any server!");
		lblThisClientWill.setFont(new Font("Dialog", Font.BOLD, 10));
		authpanel_none.add(lblThisClientWill);

		JLabel lblSslEncryptionWill = new JLabel("SSL encryption will still be used");
		lblSslEncryptionWill.setFont(new Font("Dialog", Font.BOLD, 10));
		authpanel_none.add(lblSslEncryptionWill);
		tabbedPane.setTabComponentAt(4, new GenTabComponent("lock", "Auth"));

		otab = new JPanel();
		otab.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tabbedPane.addTab(null, otab);
		tabbedPane.setTabComponentAt(5, new GenTabComponent("linechart", "Output"));
		otab.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_18 = new JPanel();
		panel_18.setPreferredSize(new Dimension(354, 300));
		otab.add(panel_18);
		panel_18.setLayout(null);

		cbx_waiver = new JCheckBox(
				"<html>I will use Crimson in accordance with all applicable laws and never on unauthorized machines.");
		cbx_waiver.setBounds(12, 232, 333, 41);
		panel_18.add(cbx_waiver);
		cbx_waiver.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel pl_timestamps = new JPanel();
		pl_timestamps.setBounds(12, 92, 330, 68);
		panel_18.add(pl_timestamps);
		pl_timestamps.setBorder(
				new TitledBorder(null, "Arbitrary Timestamps", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JButton btnRandom = new JButton("Random");
		btnRandom.setBounds(263, 19, 55, 17);
		btnRandom.setPreferredSize(new Dimension(55, 17));
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setCreationDate(new Date(CUtil.Misc.rand(0L, new Date().getTime())));
			}
		});
		pl_timestamps.setLayout(null);

		JLabel lblCreation = new JLabel("Creation:");
		lblCreation.setBounds(12, 20, 52, 13);
		lblCreation.setFont(new Font("Dialog", Font.BOLD, 10));
		pl_timestamps.add(lblCreation);

		fld_ctime = new JTextField();
		fld_ctime.setBounds(82, 19, 176, 17);
		fld_ctime.setHorizontalAlignment(SwingConstants.CENTER);
		fld_ctime.setFont(new Font("Dialog", Font.PLAIN, 10));
		pl_timestamps.add(fld_ctime);
		fld_ctime.setColumns(20);
		btnRandom.setFont(new Font("Dialog", Font.BOLD, 9));
		btnRandom.setMargin(new Insets(1, 4, 1, 4));
		pl_timestamps.add(btnRandom);

		JPanel pl_output = new JPanel();
		pl_output.setBounds(12, 12, 330, 68);
		panel_18.add(pl_output);
		pl_output.setBorder(
				new TitledBorder(null, "Output Location", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pl_output.setLayout(null);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				if (jfc.showDialog(otab, "Select") == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					// TODO check extension
					fld_path.setText(file.getAbsolutePath());
				}
			}
		});
		btnBrowse.setFont(new Font("Dialog", Font.BOLD, 9));
		btnBrowse.setMargin(new Insets(2, 4, 2, 4));
		btnBrowse.setBounds(269, 18, 49, 19);
		pl_output.add(btnBrowse);

		fld_path = new JTextField();
		fld_path.setFont(new Font("Dialog", Font.PLAIN, 11));
		fld_path.setBounds(65, 18, 198, 19);
		pl_output.add(fld_path);
		fld_path.setColumns(10);

		JLabel lblFile = new JLabel("File:");
		lblFile.setFont(new Font("Dialog", Font.BOLD, 10));
		lblFile.setBounds(12, 20, 49, 15);
		pl_output.add(lblFile);

		JLabel lblApproximateOutputSize = new JLabel("Approximate Output Size:");
		lblApproximateOutputSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblApproximateOutputSize.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblApproximateOutputSize.setFont(new Font("Dialog", Font.BOLD, 10));
		lblApproximateOutputSize.setBounds(20, 44, 290, 15);
		pl_output.add(lblApproximateOutputSize);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		lbl_status = new StatusLabel();
		lbl_status.setInfo(defaultHint);
		panel.add(lbl_status);

	}

	public Date currentCTime;// see getvalues() for reasoning
	private JTextArea fld_install_message;
	private JComboBox<String> fld_install_windows;
	private JComboBox<String> fld_install_linux;
	private JComboBox<String> fld_install_osx;
	private JComboBox<String> fld_install_bsd;
	private JComboBox<String> fld_install_solaris;
	private JComboBox<String> type_comboBox;
	private JLabel key_prefix;
	private JTextField textField;
	private JComboBox authType;
	private JLabel lblWindows;
	private JLabel lblSolaris;
	private JLabel lblLinux;
	private JLabel lblBsd;
	private JLabel lblMacOsX;
	private JLabel lblAndroid;

	private void setCreationDate(Date d) {
		currentCTime = d;
		fld_ctime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(currentCTime));

	}

	public ClientConfig getValues() {

		if (!cbx_waiver.isSelected()) {
			return null;
		}

		ClientConfig.Builder ic = ClientConfig.newBuilder();

		ic.setBuildNumber(Common.build);
		ic.setViewerUser(ViewerStore.Profiles.vp.getUser());
		ic.setOutputType((String) type_comboBox.getSelectedItem());
		ic.setDelay((int) fld_delay.getValue());
		ic.setReconnectPeriod((int) fld_connect_period.getValue());

		ic.setImsg(fld_install_message.getText());

		switch ((String) authType.getSelectedItem()) {
		case "Group": {
			ic.setAuthType(AuthType.GROUP);
			ic.setGroup(Group.newBuilder().setName(fld_group_name.getText()).setKey(CUtil.Misc.randString(64)).build());
			break;
		}
		case "Password": {
			ic.setAuthType(AuthType.PASSWORD);
			break;
		}
		case "None": {
			ic.setAuthType(AuthType.NO_AUTH);
			break;
		}
		}

		if (fld_install_windows.getSelectedItem() == null) {
			ic.setPathWin(ipath_win[0]);
		} else {
			ic.setPathWin((String) fld_install_windows.getSelectedItem());
		}

		if (fld_install_linux.getSelectedItem() == null) {
			ic.setPathLin(ipath_lin[0]);
		} else {
			ic.setPathLin((String) fld_install_linux.getSelectedItem());
		}

		if (fld_install_osx.getSelectedItem() == null) {
			ic.setPathOsx(ipath_osx[0]);
		} else {
			ic.setPathOsx((String) fld_install_osx.getSelectedItem());
		}

		if (fld_install_solaris.getSelectedItem() == null) {
			ic.setPathSol(ipath_sol[0]);
		} else {
			ic.setPathSol((String) fld_install_solaris.getSelectedItem());
		}

		if (fld_install_bsd.getSelectedItem() == null) {
			ic.setPathBsd(ipath_bsd[0]);
		} else {
			ic.setPathBsd((String) fld_install_bsd.getSelectedItem());
		}

		for (NetworkTarget nt : table.getTargets()) {
			if (nt != null) {
				ic.addTarget(nt);
			}
		}

		return ic.build();
	}

	public boolean testValues(ClientConfig config) {// TODO finsh

		if (config == null) {
			lbl_status.setBad("You must agree to the terms first");
			return false;
		}

		if (config.getTargetCount() == 0) {
			lbl_status.setBad("You must specify at least one server (Network Target)");
			return false;
		}

		// test paths
		if (!CUtil.Validation.path(config.getPathWin())) {
			lbl_status.setBad("Invalid Windows install path");
			return false;
		}

		if (!CUtil.Validation.path(config.getPathLin())) {
			lbl_status.setBad("Invalid Linux install path");
			return false;
		}

		if (!CUtil.Validation.path(config.getPathOsx())) {
			lbl_status.setBad("Invalid OSX install path");
			return false;
		}

		if (!CUtil.Validation.path(config.getPathSol())) {
			lbl_status.setBad("Invalid Solaris install path");
			return false;
		}

		if (!CUtil.Validation.path(config.getPathBsd())) {
			lbl_status.setBad("Invalid BSD install path");
			return false;
		}

		return true;
	}

	private void changeToJar() {

		txt_output_desc.setText(
				"The jar file installer works on many platforms and is the most popular way to install Crimson on clients.  Java 1.8 or greater is required before installation.");

		fld_install_bsd.setEnabled(true);

		lblAndroid.setForeground(StatusLabel.bad);
		lblMacOsX.setForeground(StatusLabel.good);
		lblBsd.setForeground(StatusLabel.good);
		lblLinux.setForeground(StatusLabel.good);
		lblSolaris.setForeground(StatusLabel.good);
		lblWindows.setForeground(StatusLabel.good);
	}

	private void changeToExe() {

		txt_output_desc.setText(
				"The executable installer can install Crimson on Windows machines only.  Java is NOT required before installation.");

		fld_install_bsd.setEnabled(false);

		lblAndroid.setForeground(StatusLabel.bad);
		lblMacOsX.setForeground(StatusLabel.bad);
		lblBsd.setForeground(StatusLabel.bad);
		lblLinux.setForeground(StatusLabel.bad);
		lblSolaris.setForeground(StatusLabel.bad);
		lblWindows.setForeground(StatusLabel.good);
	}

	private void changeToApk() {

		txt_output_desc.setText("The Android installer can install Crimson on Android devices version 2.2 and up.");

		fld_install_bsd.setEnabled(false);

		lblAndroid.setForeground(StatusLabel.good);
		lblMacOsX.setForeground(StatusLabel.bad);
		lblBsd.setForeground(StatusLabel.bad);
		lblLinux.setForeground(StatusLabel.bad);
		lblSolaris.setForeground(StatusLabel.bad);
		lblWindows.setForeground(StatusLabel.bad);
	}

	private void changeToSh() {

		txt_output_desc.setText("");

		fld_install_bsd.setEnabled(true);

		lblAndroid.setForeground(StatusLabel.bad);
		lblMacOsX.setForeground(StatusLabel.good);
		lblBsd.setForeground(StatusLabel.good);
		lblLinux.setForeground(StatusLabel.good);
		lblSolaris.setForeground(StatusLabel.good);
		lblWindows.setForeground(StatusLabel.bad);

	}

	private void refreshAuth() {
		String c = (String) authType.getSelectedItem();

	}

	class GroupTimer extends TimerTask {

		private Random rand = new Random();
		private String last = "" + new Date().getTime();

		private int upper = 20;
		private int lower = 10;

		@Override
		public void run() {
			hash();
			display(50);
		}

		private void hash() {
			last = Crypto.sign(last, CUtil.Misc.randString(rand.nextInt(upper - lower + 1) + lower)).replaceAll("\\+|/",
					CUtil.Misc.randString(1));
		}

		private void display(int delay) {
			String data = last;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 16; i++) {
				sb.append(' ');
				sb.append(data.charAt(i));
			}
			key_prefix.setText("");
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			key_prefix.setText(sb.toString().substring(1).toUpperCase());
		}

		public void mix(Point p) {
			last += p.x + p.y;
			hash();
			display(0);
		}

	}

	public void cancelTimer() {
		timer.cancel();
		timer = null;

	}
}
