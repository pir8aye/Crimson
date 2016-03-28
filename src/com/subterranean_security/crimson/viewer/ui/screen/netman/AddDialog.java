package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField fld_name;
	private JTextField fld_port;
	private JCheckBox chckbxUseUpnp;
	private JCheckBox chckbxRestrictToLocalhost;
	private JCheckBox chckbxAcceptClients;
	private JCheckBox chckbxAcceptViewers;
	private JComboBox owner;

	public AddDialog() {
		setTitle("Add Listener");
		setBounds(100, 100, 250, 320);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			panel.setBorder(
					new TitledBorder(null, "Listener Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			contentPanel.add(panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			{
				JLabel lblOptionalName = new JLabel("Optional Name:");
				lblOptionalName.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_lblOptionalName = new GridBagConstraints();
				gbc_lblOptionalName.insets = new Insets(0, 0, 5, 5);
				gbc_lblOptionalName.anchor = GridBagConstraints.EAST;
				gbc_lblOptionalName.gridx = 0;
				gbc_lblOptionalName.gridy = 0;
				panel.add(lblOptionalName, gbc_lblOptionalName);
			}
			{
				fld_name = new JTextField();
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.insets = new Insets(0, 0, 5, 0);
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = 1;
				gbc_textField.gridy = 0;
				panel.add(fld_name, gbc_textField);
				fld_name.setColumns(10);
			}
			{
				JLabel lblPort = new JLabel("Port:");
				lblPort.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_lblPort = new GridBagConstraints();
				gbc_lblPort.anchor = GridBagConstraints.EAST;
				gbc_lblPort.insets = new Insets(0, 0, 5, 5);
				gbc_lblPort.gridx = 0;
				gbc_lblPort.gridy = 1;
				panel.add(lblPort, gbc_lblPort);
			}
			{
				fld_port = new JTextField();
				GridBagConstraints gbc_textField_1 = new GridBagConstraints();
				gbc_textField_1.insets = new Insets(0, 0, 5, 0);
				gbc_textField_1.anchor = GridBagConstraints.WEST;
				gbc_textField_1.gridx = 1;
				gbc_textField_1.gridy = 1;
				panel.add(fld_port, gbc_textField_1);
				fld_port.setColumns(6);
			}
			{
				JLabel lblOwner = new JLabel("Owner:");
				lblOwner.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_lblOwner = new GridBagConstraints();
				gbc_lblOwner.anchor = GridBagConstraints.EAST;
				gbc_lblOwner.insets = new Insets(0, 0, 0, 5);
				gbc_lblOwner.gridx = 0;
				gbc_lblOwner.gridy = 2;
				panel.add(lblOwner, gbc_lblOwner);
			}
			{
				owner = new JComboBox();
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 2;
				panel.add(owner, gbc_comboBox);
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Flags", TitledBorder.LEADING,
					TitledBorder.TOP, null, new Color(51, 51, 51)));
			contentPanel.add(panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			{
				chckbxUseUpnp = new JCheckBox("Use UPnP");
				chckbxUseUpnp.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_chckbxUseUpnp = new GridBagConstraints();
				gbc_chckbxUseUpnp.insets = new Insets(0, 0, 5, 0);
				gbc_chckbxUseUpnp.anchor = GridBagConstraints.WEST;
				gbc_chckbxUseUpnp.gridx = 0;
				gbc_chckbxUseUpnp.gridy = 0;
				panel.add(chckbxUseUpnp, gbc_chckbxUseUpnp);
			}
			{
				chckbxRestrictToLocalhost = new JCheckBox("Restrict to localhost");
				chckbxRestrictToLocalhost.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_chckbxRestrictToLocalhost = new GridBagConstraints();
				gbc_chckbxRestrictToLocalhost.insets = new Insets(0, 0, 5, 0);
				gbc_chckbxRestrictToLocalhost.anchor = GridBagConstraints.WEST;
				gbc_chckbxRestrictToLocalhost.gridx = 0;
				gbc_chckbxRestrictToLocalhost.gridy = 1;
				panel.add(chckbxRestrictToLocalhost, gbc_chckbxRestrictToLocalhost);
			}
			{
				chckbxAcceptClients = new JCheckBox("Accept clients");
				chckbxAcceptClients.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_chckbxAcceptClients = new GridBagConstraints();
				gbc_chckbxAcceptClients.anchor = GridBagConstraints.WEST;
				gbc_chckbxAcceptClients.insets = new Insets(0, 0, 5, 0);
				gbc_chckbxAcceptClients.gridx = 0;
				gbc_chckbxAcceptClients.gridy = 2;
				panel.add(chckbxAcceptClients, gbc_chckbxAcceptClients);
			}
			{
				chckbxAcceptViewers = new JCheckBox("Accept viewers");
				chckbxAcceptViewers.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_chckbxAcceptViewers = new GridBagConstraints();
				gbc_chckbxAcceptViewers.anchor = GridBagConstraints.WEST;
				gbc_chckbxAcceptViewers.gridx = 0;
				gbc_chckbxAcceptViewers.gridy = 3;
				panel.add(chckbxAcceptViewers, gbc_chckbxAcceptViewers);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new Thread(new Runnable() {
							public void run() {
								StringBuffer error = new StringBuffer();
								ViewerCommands.addListener(error, ListenerConfig.newBuilder()
										.setName(fld_name.getText()).setClientAcceptor(chckbxAcceptClients.isSelected())
										.setViewerAcceptor(chckbxAcceptViewers.isSelected())
										.setLocalhostExclusive(chckbxRestrictToLocalhost.isSelected())
										.setPort(Integer.parseInt(fld_port.getText())).setID("temp id").build());// TODO
							}
						}).start();

					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
