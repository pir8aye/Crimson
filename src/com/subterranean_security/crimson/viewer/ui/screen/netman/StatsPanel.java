package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public StatsPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel lblListeners = new JLabel("Listeners:");
		panel.add(lblListeners);

		JLabel label = new JLabel("0");
		panel.add(label);

		JLabel lblInternalIp = new JLabel("Internal IP:");
		panel.add(lblInternalIp);

		JLabel label_1 = new JLabel("0.0.0.0");
		panel.add(label_1);

		JLabel lblExternalIp = new JLabel("External IP:");
		panel.add(lblExternalIp);

		JLabel label_2 = new JLabel("0.0.0.0");
		panel.add(label_2);

	}

}
