package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.ui.StatusLabel;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sv.PermissionTester;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.utility.UIStore;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class AddDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField fld_name;
	private JTextField fld_port;
	private JCheckBox chckbxUseUpnp;
	private JCheckBox chckbxRestrictToLocalhost;
	private JCheckBox chckbxAcceptClients;
	private JCheckBox chckbxAcceptViewers;
	private JComboBox<String> owner;
	private StatusLabel sl;
	private JPanel panel_1;
	private JButton okButton;

	public AddDialog() {
		init();
		updateOwners();
	}

	public void init() {
		setResizable(false);
		setTitle("Add Listener");
		setIconImages(UUtil.getIconList());
		setBounds(100, 100, 250, 329);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Details", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			{
				JLabel lblOptionalName = new JLabel("Optional Name:");
				lblOptionalName.setFont(new Font("Dialog", Font.BOLD, 10));
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
				lblPort.setFont(new Font("Dialog", Font.BOLD, 10));
				GridBagConstraints gbc_lblPort = new GridBagConstraints();
				gbc_lblPort.anchor = GridBagConstraints.EAST;
				gbc_lblPort.insets = new Insets(0, 0, 5, 5);
				gbc_lblPort.gridx = 0;
				gbc_lblPort.gridy = 1;
				panel.add(lblPort, gbc_lblPort);
			}
			{
				fld_port = new JTextField();
				fld_port.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						refreshFields();
					}
				});
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
				lblOwner.setFont(new Font("Dialog", Font.BOLD, 10));
				GridBagConstraints gbc_lblOwner = new GridBagConstraints();
				gbc_lblOwner.anchor = GridBagConstraints.EAST;
				gbc_lblOwner.insets = new Insets(0, 0, 0, 5);
				gbc_lblOwner.gridx = 0;
				gbc_lblOwner.gridy = 2;
				panel.add(lblOwner, gbc_lblOwner);
			}
			{
				owner = new JComboBox<String>();
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
				chckbxUseUpnp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent arg0) {
						sl.setInfo("");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
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
				chckbxRestrictToLocalhost.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						sl.setInfo("");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
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
				chckbxAcceptClients.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						refreshBoxes();
					}
				});
				chckbxAcceptClients.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						sl.setInfo("Allow clients to connect?");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
				chckbxAcceptClients.setSelected(true);
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
				chckbxAcceptViewers.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						refreshBoxes();
					}
				});
				chckbxAcceptViewers.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						sl.setInfo("Allow viewers to connect?");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
				chckbxAcceptViewers.setSelected(true);
				chckbxAcceptViewers.setFont(new Font("Dialog", Font.BOLD, 11));
				GridBagConstraints gbc_chckbxAcceptViewers = new GridBagConstraints();
				gbc_chckbxAcceptViewers.anchor = GridBagConstraints.WEST;
				gbc_chckbxAcceptViewers.gridx = 0;
				gbc_chckbxAcceptViewers.gridy = 3;
				panel.add(chckbxAcceptViewers, gbc_chckbxAcceptViewers);
			}
		}
		{
			panel_1 = new JPanel();
			panel_1.add(Box.createVerticalStrut(20), BorderLayout.WEST);
			contentPanel.add(panel_1);
			panel_1.setLayout(new BorderLayout(0, 0));
			{
				sl = new StatusLabel();
				panel_1.add(sl);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new Thread(new Runnable() {
							public void run() {
								if (!verify()) {
									return;
								}
								StringBuffer error = new StringBuffer();
								ViewerCommands.addListener(error,
										ListenerConfig.newBuilder()
												.setName(fld_name.getText().isEmpty() ? "Unnamed Listener"
														: fld_name.getText())
												.setClientAcceptor(chckbxAcceptClients.isSelected())
												.setViewerAcceptor(chckbxAcceptViewers.isSelected())
												.setLocalhostExclusive(chckbxRestrictToLocalhost.isSelected())
												.setPort(Integer.parseInt(fld_port.getText()))
												.setOwner((String) owner.getSelectedItem()).setId(IDGen.getListenerID())
												.build());
								dispose();
							}
						}).start();

					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		UIStore.addDialog = null;
	}

	public boolean verify() {
		if (!CUtil.Validation.port(fld_port.getText())) {
			sl.setBad("Invalid port");
			return false;
		}

		for (ListenerConfig lc : ViewerStore.Profiles.server.listeners) {
			if (lc.getPort() == Integer.parseInt(fld_port.getText())) {
				sl.setBad("Port in use");
				return false;
			}
		}

		return true;
	}

	public void refreshFields() {
		if (verify()) {
			okButton.setEnabled(true);
			sl.setDefault();
		} else {
			okButton.setEnabled(false);
		}

	}

	public void refreshBoxes() {
		if (!chckbxAcceptClients.isSelected() && !chckbxAcceptViewers.isSelected()) {
			sl.setBad("Must accept some connections");
			okButton.setEnabled(false);
		} else {
			sl.setDefault();
			okButton.setEnabled(true);
		}
	}

	public void updateOwners() {
		String[] o = null;
		if (PermissionTester.verifyServerPermission(ViewerStore.Profiles.vp.getPermissions(), "super")) {
			o = new String[ViewerStore.Profiles.server.users.size()];
			for (int i = 0; i < o.length; i++) {
				o[i] = ViewerStore.Profiles.server.users.get(i).getUser();
			}
		} else {
			o = new String[] { ViewerStore.Profiles.vp.getUser() };
		}
		owner.setModel(new DefaultComboBoxModel<String>(o));
	}

}
