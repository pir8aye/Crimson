package com.subterranean_security.crimson.viewer.ui.screen.generator.tabs;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.IOException;

import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ui.UICommon;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class FTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblWin;
	private JLabel lblLin;
	private JLabel lblOsx;
	private JLabel lblSol;
	private JLabel lblBsd;
	private JLabel lblKeylogger;

	public FTab() {
		init();
		try {
			lblWin.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jni-win-size")),
					CUtil.Misc.BYTES));
			lblLin.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jni-lin-size")),
					CUtil.Misc.BYTES));
			lblOsx.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jni-osx-size")),
					CUtil.Misc.BYTES));
			lblSol.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jni-sol-size")),
					CUtil.Misc.BYTES));
			lblBsd.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jni-bsd-size")),
					CUtil.Misc.BYTES));
			lblKeylogger.setText(CUtil.Misc.familiarize(Long.parseLong(CUtil.Misc.getManifestAttr("jnativehook-size")),
					CUtil.Misc.BYTES));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		setLayout(null);

		JLabel lblTipRemoveUnneeded = new JLabel("Tip: remove unneeded features to reduce output size");
		lblTipRemoveUnneeded.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTipRemoveUnneeded.setBounds(12, 273, 316, 15);
		add(lblTipRemoveUnneeded);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UICommon.basic, "Platform Compatibility", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
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

		lblWin = new JLabel();
		lblWin.setHorizontalAlignment(SwingConstants.TRAILING);
		lblWin.setFont(new Font("Dialog", Font.BOLD, 10));
		lblWin.setBounds(234, 22, 70, 15);
		panel.add(lblWin);

		lblLin = new JLabel();
		lblLin.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLin.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLin.setBounds(234, 42, 70, 15);
		panel.add(lblLin);

		lblOsx = new JLabel();
		lblOsx.setHorizontalAlignment(SwingConstants.TRAILING);
		lblOsx.setFont(new Font("Dialog", Font.BOLD, 10));
		lblOsx.setBounds(234, 62, 70, 15);
		panel.add(lblOsx);

		lblSol = new JLabel();
		lblSol.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSol.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSol.setBounds(234, 82, 70, 15);
		panel.add(lblSol);

		lblBsd = new JLabel();
		lblBsd.setHorizontalAlignment(SwingConstants.TRAILING);
		lblBsd.setFont(new Font("Dialog", Font.BOLD, 10));
		lblBsd.setBounds(234, 102, 70, 15);
		panel.add(lblBsd);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(UICommon.basic, "Features", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(12, 151, 316, 57);
		add(panel_1);
		panel_1.setLayout(null);

		JCheckBox chckbxKeylogger = new JCheckBox("Keylogger");
		chckbxKeylogger.setSelected(true);
		chckbxKeylogger.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxKeylogger.setBounds(8, 18, 129, 23);
		panel_1.add(chckbxKeylogger);

		lblKeylogger = new JLabel();
		lblKeylogger.setHorizontalAlignment(SwingConstants.TRAILING);
		lblKeylogger.setFont(new Font("Dialog", Font.BOLD, 10));
		lblKeylogger.setBounds(234, 21, 70, 15);
		panel_1.add(lblKeylogger);

		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 252, 316, 15);
		add(lblNewLabel);

	}

}
