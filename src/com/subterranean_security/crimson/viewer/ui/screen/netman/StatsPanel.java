package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public StatsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 146, 104, 18, 77, 51, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel label = new JLabel("Listeners:");
		label.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);

		JLabel label_1 = new JLabel("0");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setFont(new Font("Dialog", Font.PLAIN, 11));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 0;
		add(label_1, gbc_label_1);

		JLabel lblClients = new JLabel("Clients:");
		lblClients.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblClients = new GridBagConstraints();
		gbc_lblClients.insets = new Insets(0, 0, 5, 5);
		gbc_lblClients.gridx = 3;
		gbc_lblClients.gridy = 0;
		add(lblClients, gbc_lblClients);

		JLabel lblNewLabel = new JLabel("val");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 4;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblLocalInternalIp = new JLabel("Local Internal IP:");
		lblLocalInternalIp.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblLocalInternalIp = new GridBagConstraints();
		gbc_lblLocalInternalIp.anchor = GridBagConstraints.WEST;
		gbc_lblLocalInternalIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblLocalInternalIp.gridx = 0;
		gbc_lblLocalInternalIp.gridy = 1;
		add(lblLocalInternalIp, gbc_lblLocalInternalIp);

		JLabel label_3 = new JLabel("0.0.0.0");
		label_3.setHorizontalAlignment(SwingConstants.TRAILING);
		label_3.setFont(new Font("Dialog", Font.PLAIN, 11));
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 1;
		gbc_label_3.gridy = 1;
		add(label_3, gbc_label_3);

		JLabel lblViewers = new JLabel("Viewers:");
		lblViewers.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblViewers = new GridBagConstraints();
		gbc_lblViewers.insets = new Insets(0, 0, 5, 5);
		gbc_lblViewers.gridx = 3;
		gbc_lblViewers.gridy = 1;
		add(lblViewers, gbc_lblViewers);

		JLabel lblVal = new JLabel("val");
		GridBagConstraints gbc_lblVal = new GridBagConstraints();
		gbc_lblVal.insets = new Insets(0, 0, 5, 0);
		gbc_lblVal.gridx = 4;
		gbc_lblVal.gridy = 1;
		add(lblVal, gbc_lblVal);

		JLabel lblServerExternalIp = new JLabel("Server External IP:");
		lblServerExternalIp.setFont(new Font("Dialog", Font.BOLD, 11));
		GridBagConstraints gbc_lblServerExternalIp = new GridBagConstraints();
		gbc_lblServerExternalIp.anchor = GridBagConstraints.WEST;
		gbc_lblServerExternalIp.insets = new Insets(0, 0, 0, 5);
		gbc_lblServerExternalIp.gridx = 0;
		gbc_lblServerExternalIp.gridy = 2;
		add(lblServerExternalIp, gbc_lblServerExternalIp);

		JLabel label_5 = new JLabel("0.0.0.0");
		label_5.setHorizontalAlignment(SwingConstants.TRAILING);
		label_5.setFont(new Font("Dialog", Font.PLAIN, 11));
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.EAST;
		gbc_label_5.insets = new Insets(0, 0, 0, 5);
		gbc_label_5.gridx = 1;
		gbc_label_5.gridy = 2;
		add(label_5, gbc_label_5);

	}

}
