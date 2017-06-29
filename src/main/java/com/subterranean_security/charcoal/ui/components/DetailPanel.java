package com.subterranean_security.charcoal.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class DetailPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public DetailPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(
				new String[] { "Disabled", "Profiles", "Database", "Network", "Connections" }));
		menuBar.add(comboBox);

	}

}
