package com.subterranean_security.crimson.viewer.ui.screen.generator.tabs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.nucleus.JarUtil;
import com.subterranean_security.crimson.viewer.ui.UICommon;

public class FTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblWin;
	private JLabel lblLin;
	private JLabel lblOsx;
	private JLabel lblSol;
	private JLabel lblBsd;
	private JLabel lblKeylogger;

	private long client_size;
	private long win_size;
	private long lin_size;
	private long osx_size;
	private long sol_size;
	private long bsd_size;
	private long jnativehook_size;
	public JCheckBox chckbxWindows;
	public JCheckBox chckbxLinux;
	public JCheckBox chckbxOsX;
	public JCheckBox chckbxSolaris;
	public JCheckBox chckbxBsd;
	public JCheckBox chckbxKeylogger;
	private JLabel lblTotal;

	private JLabel outputSize;

	public FTab(JLabel outputSize) {
		this.outputSize = outputSize;
		init();
		try {
			win_size = Long.parseLong(JarUtil.getManifestValue("jni-win-size"));
			lin_size = Long.parseLong(JarUtil.getManifestValue("jni-lin-size"));
			osx_size = Long.parseLong(JarUtil.getManifestValue("jni-osx-size"));
			sol_size = Long.parseLong(JarUtil.getManifestValue("jni-sol-size"));
			bsd_size = Long.parseLong(JarUtil.getManifestValue("jni-bsd-size"));
			jnativehook_size = Long.parseLong(JarUtil.getManifestValue("jnativehook-size"));
			client_size = JarUtil.getResourceSize("/com/subterranean_security/crimson/server/res/bin/client.jar")
					+ Long.parseLong(JarUtil.getManifestValue("client-lib-size"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		refresh();
	}

	private void refresh() {
		long total = client_size;
		if (chckbxWindows.isSelected()) {
			lblWin.setText(UnitTranslator.familiarize(win_size, UnitTranslator.BYTES));
			total += win_size;
		} else {
			lblWin.setText("0 KB");
		}

		if (chckbxLinux.isSelected()) {
			lblLin.setText(UnitTranslator.familiarize(lin_size, UnitTranslator.BYTES));
			total += lin_size;
		} else {
			lblLin.setText("0 KB");
		}

		if (chckbxOsX.isSelected()) {
			lblOsx.setText(UnitTranslator.familiarize(osx_size, UnitTranslator.BYTES));
			total += osx_size;
		} else {
			lblOsx.setText("0 KB");
		}

		if (chckbxSolaris.isSelected()) {
			lblSol.setText(UnitTranslator.familiarize(sol_size, UnitTranslator.BYTES));
			total += sol_size;
		} else {
			lblSol.setText("0 KB");
		}

		if (chckbxBsd.isSelected()) {
			lblBsd.setText(UnitTranslator.familiarize(bsd_size, UnitTranslator.BYTES));
			total += bsd_size;
		} else {
			lblBsd.setText("0 KB");
		}

		if (chckbxKeylogger.isSelected()) {
			lblKeylogger.setText(UnitTranslator.familiarize(jnativehook_size, UnitTranslator.BYTES));
			total += jnativehook_size;
		} else {
			lblKeylogger.setText("0 KB");
		}

		lblTotal.setText("Output size: " + UnitTranslator.familiarize(total, UnitTranslator.BYTES));
		outputSize.setText("Approximate output size: " + UnitTranslator.familiarize(total, UnitTranslator.BYTES));

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

		chckbxWindows = new JCheckBox("Windows");
		chckbxWindows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		chckbxWindows.setSelected(true);
		chckbxWindows.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxWindows.setBounds(8, 20, 129, 20);
		panel.add(chckbxWindows);

		chckbxLinux = new JCheckBox("Linux");
		chckbxLinux.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		chckbxLinux.setSelected(true);
		chckbxLinux.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxLinux.setBounds(8, 40, 129, 20);
		panel.add(chckbxLinux);

		chckbxOsX = new JCheckBox("OS X");
		chckbxOsX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		chckbxOsX.setSelected(true);
		chckbxOsX.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxOsX.setBounds(8, 60, 129, 20);
		panel.add(chckbxOsX);

		chckbxSolaris = new JCheckBox("Solaris");
		chckbxSolaris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		chckbxSolaris.setSelected(true);
		chckbxSolaris.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxSolaris.setBounds(8, 80, 129, 20);
		panel.add(chckbxSolaris);

		chckbxBsd = new JCheckBox("BSD");
		chckbxBsd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
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

		chckbxKeylogger = new JCheckBox("Keylogger");
		chckbxKeylogger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		chckbxKeylogger.setSelected(true);
		chckbxKeylogger.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxKeylogger.setBounds(8, 18, 129, 23);
		panel_1.add(chckbxKeylogger);

		lblKeylogger = new JLabel();
		lblKeylogger.setHorizontalAlignment(SwingConstants.TRAILING);
		lblKeylogger.setFont(new Font("Dialog", Font.BOLD, 10));
		lblKeylogger.setBounds(234, 21, 70, 15);
		panel_1.add(lblKeylogger);

		lblTotal = new JLabel("New label");
		lblTotal.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblTotal.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
		lblTotal.setBounds(12, 252, 316, 15);
		add(lblTotal);

	}

	public boolean testValues() {
		return (chckbxWindows.isSelected() || chckbxLinux.isSelected() || chckbxOsX.isSelected()
				|| chckbxBsd.isSelected() || chckbxSolaris.isSelected());
	}

}
