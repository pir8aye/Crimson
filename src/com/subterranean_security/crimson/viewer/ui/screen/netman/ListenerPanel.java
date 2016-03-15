package com.subterranean_security.crimson.viewer.ui.screen.netman;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.JCheckBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class ListenerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ListenerTable lt = new ListenerTable();
	private JTextField textField;
	private JTextField textField_1;

	public ListenerPanel() {
		setLayout(new BorderLayout(0, 0));
		add(lt);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Add Listener", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 17, 21, 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblOptionalName = new JLabel("Optional Name:");
		lblOptionalName.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblOptionalName = new GridBagConstraints();
		gbc_lblOptionalName.fill = GridBagConstraints.BOTH;
		gbc_lblOptionalName.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionalName.gridx = 0;
		gbc_lblOptionalName.gridy = 0;
		panel_2.add(lblOptionalName, gbc_lblOptionalName);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.anchor = GridBagConstraints.WEST;
		gbc_textField.fill = GridBagConstraints.VERTICAL;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel_2.add(textField, gbc_textField);
		textField.setColumns(15);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.WEST;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 1;
		panel_2.add(lblPort, gbc_lblPort);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.anchor = GridBagConstraints.EAST;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		panel_2.add(textField_1, gbc_textField_1);
		textField_1.setColumns(6);
		
				JLabel lblOwner = new JLabel("Owner:");
				lblOwner.setFont(new Font("Dialog", Font.BOLD, 10));
				GridBagConstraints gbc_lblOwner = new GridBagConstraints();
				gbc_lblOwner.anchor = GridBagConstraints.WEST;
				gbc_lblOwner.insets = new Insets(0, 0, 5, 5);
				gbc_lblOwner.gridx = 0;
				gbc_lblOwner.gridy = 2;
				panel_2.add(lblOwner, gbc_lblOwner);
		
				JComboBox comboBox = new JComboBox();
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.anchor = GridBagConstraints.WEST;
				gbc_comboBox.insets = new Insets(0, 0, 5, 5);
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 2;
				panel_2.add(comboBox, gbc_comboBox);

		JCheckBox chckbxLocalListener = new JCheckBox("Local Listener");
		chckbxLocalListener.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_chckbxLocalListener = new GridBagConstraints();
		gbc_chckbxLocalListener.fill = GridBagConstraints.BOTH;
		gbc_chckbxLocalListener.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLocalListener.gridx = 0;
		gbc_chckbxLocalListener.gridy = 3;
		panel_2.add(chckbxLocalListener, gbc_chckbxLocalListener);

		JCheckBox chckbxUpnp = new JCheckBox("UPnP");
		chckbxUpnp.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_chckbxUpnp = new GridBagConstraints();
		gbc_chckbxUpnp.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUpnp.anchor = GridBagConstraints.WEST;
		gbc_chckbxUpnp.gridx = 1;
		gbc_chckbxUpnp.gridy = 3;
		panel_2.add(chckbxUpnp, gbc_chckbxUpnp);
		
		JCheckBox chckbxClientListener = new JCheckBox("Client Listener");
		chckbxClientListener.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_chckbxClientListener = new GridBagConstraints();
		gbc_chckbxClientListener.anchor = GridBagConstraints.WEST;
		gbc_chckbxClientListener.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxClientListener.gridx = 0;
		gbc_chckbxClientListener.gridy = 4;
		panel_2.add(chckbxClientListener, gbc_chckbxClientListener);
		
		JCheckBox chckbxViewerListener = new JCheckBox("Viewer Listener");
		chckbxViewerListener.setFont(new Font("Dialog", Font.BOLD, 10));
		GridBagConstraints gbc_chckbxViewerListener = new GridBagConstraints();
		gbc_chckbxViewerListener.anchor = GridBagConstraints.WEST;
		gbc_chckbxViewerListener.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxViewerListener.gridx = 1;
		gbc_chckbxViewerListener.gridy = 4;
		panel_2.add(chckbxViewerListener, gbc_chckbxViewerListener);

		JButton btnAdd = new JButton("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridx = 3;
		gbc_btnAdd.gridy = 4;
		panel_2.add(btnAdd, gbc_btnAdd);
	}

}
