package com.subterranean_security.crimson.viewer.ui.screen.generator.tabs;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class FTab extends JPanel {

	private static final long serialVersionUID = 1L;

	public FTab() {
		setLayout(null);
		
		JLabel lblTipRemoveUnneeded = new JLabel("Tip: remove unneeded features to reduce output size");
		lblTipRemoveUnneeded.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTipRemoveUnneeded.setBounds(12, 273, 316, 15);
		add(lblTipRemoveUnneeded);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Platform Compatibility", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(12, 12, 316, 127);
		add(panel);
		panel.setLayout(null);
		
		JCheckBox chckbxWindows = new JCheckBox("Windows");
		chckbxWindows.setSelected(true);
		chckbxWindows.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxWindows.setBounds(8, 20, 129, 20);
		panel.add(chckbxWindows);
		
		JCheckBox chckbxLinux = new JCheckBox("Linux");
		chckbxLinux.setSelected(true);
		chckbxLinux.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxLinux.setBounds(8, 40, 129, 20);
		panel.add(chckbxLinux);
		
		JCheckBox chckbxOsX = new JCheckBox("OS X");
		chckbxOsX.setSelected(true);
		chckbxOsX.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxOsX.setBounds(8, 60, 129, 20);
		panel.add(chckbxOsX);
		
		JCheckBox chckbxSolaris = new JCheckBox("Solaris");
		chckbxSolaris.setSelected(true);
		chckbxSolaris.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxSolaris.setBounds(8, 80, 129, 20);
		panel.add(chckbxSolaris);
		
		JCheckBox chckbxBsd = new JCheckBox("BSD");
		chckbxBsd.setSelected(true);
		chckbxBsd.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxBsd.setBounds(8, 100, 129, 20);
		panel.add(chckbxBsd);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Features", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(12, 151, 316, 57);
		add(panel_1);
		panel_1.setLayout(null);
		
		JCheckBox chckbxKeylogger = new JCheckBox("Keylogger");
		chckbxKeylogger.setSelected(true);
		chckbxKeylogger.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxKeylogger.setBounds(8, 18, 129, 23);
		panel_1.add(chckbxKeylogger);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 252, 316, 15);
		add(lblNewLabel);

	}
}
