/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.screen.netman.listener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.net.command.ListenerCom;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

public class AddListener extends JPanel {

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
	private JButton okButton;

	private EPanel ep;
	private JPanel panel_1;

	public AddListener(EPanel ep) {
		this.ep = ep;

		init();
		updateOwners();
	}

	public void init() {
		setBounds(100, 100, 592, 212);
		setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(250, 130));
			panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Details", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panel);
			panel.setLayout(null);
			{
				JLabel lblOptionalName = new JLabel("Optional Name:");
				lblOptionalName.setHorizontalAlignment(SwingConstants.TRAILING);
				lblOptionalName.setBounds(8, 20, 97, 13);
				lblOptionalName.setFont(new Font("Dialog", Font.BOLD, 10));
				panel.add(lblOptionalName);
			}
			{
				fld_name = new JTextField();
				fld_name.setBounds(113, 16, 125, 19);
				panel.add(fld_name);
				fld_name.setColumns(10);
			}
			{
				JLabel lblPort = new JLabel("Port:");
				lblPort.setHorizontalAlignment(SwingConstants.TRAILING);
				lblPort.setBounds(8, 45, 97, 13);
				lblPort.setFont(new Font("Dialog", Font.BOLD, 10));
				panel.add(lblPort);
			}
			{
				fld_port = new JTextField();
				fld_port.setBounds(113, 41, 70, 19);
				fld_port.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						refreshFields();
					}
				});
				panel.add(fld_port);
				fld_port.setColumns(6);
			}
			{
				JLabel lblOwner = new JLabel("Owner:");
				lblOwner.setHorizontalAlignment(SwingConstants.TRAILING);
				lblOwner.setBounds(8, 70, 97, 13);
				lblOwner.setFont(new Font("Dialog", Font.BOLD, 10));
				panel.add(lblOwner);
			}
			{
				owner = new JComboBox<String>();
				owner.setBounds(113, 66, 125, 19);
				panel.add(owner);
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(250, 130));
			panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Flags", TitledBorder.LEADING,
					TitledBorder.TOP, null, new Color(51, 51, 51)));
			contentPanel.add(panel);
			{
				chckbxUseUpnp = new JCheckBox("Use UPnP");
				chckbxUseUpnp.setBounds(5, 17, 86, 22);
				chckbxUseUpnp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent arg0) {
						sl.setInfo("UPnP attemps to automatically port forward the router");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
				panel.setLayout(null);
				chckbxUseUpnp.setFont(new Font("Dialog", Font.BOLD, 11));
				panel.add(chckbxUseUpnp);
			}
			{
				chckbxRestrictToLocalhost = new JCheckBox("Restrict to localhost");
				chckbxRestrictToLocalhost.setBounds(5, 44, 154, 22);
				chckbxRestrictToLocalhost.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						sl.setInfo("Only connections originating from localhost will be accepted");
					}

					@Override
					public void mouseExited(MouseEvent e) {
						sl.setDefault();
					}
				});
				chckbxRestrictToLocalhost.setFont(new Font("Dialog", Font.BOLD, 11));
				panel.add(chckbxRestrictToLocalhost);
			}
			{
				chckbxAcceptClients = new JCheckBox("Accept clients");
				chckbxAcceptClients.setBounds(5, 71, 115, 22);
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
				panel.add(chckbxAcceptClients);
			}
			{
				chckbxAcceptViewers = new JCheckBox("Accept viewers");
				chckbxAcceptViewers.setBounds(5, 98, 122, 22);
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
				panel.add(chckbxAcceptViewers);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMargin(new Insets(2, 4, 2, 4));
				cancelButton.setFont(new Font("Dialog", Font.BOLD, 11));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						resetEPanels();
						ep.drop();
					}
				});
				buttonPane.add(cancelButton);
			}
			{
				okButton = new JButton("Add");
				okButton.setMargin(new Insets(2, 6, 2, 6));
				okButton.setFont(new Font("Dialog", Font.BOLD, 11));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new Thread(() -> {
							if (!verify()) {
								return;
							}
							ListenerCom.addListener(ListenerConfig.newBuilder()
									.setName(fld_name.getText().isEmpty() ? "Unnamed Listener" : fld_name.getText())
									.setClientAcceptor(chckbxAcceptClients.isSelected())
									.setViewerAcceptor(chckbxAcceptViewers.isSelected())
									.setLocalhostExclusive(chckbxRestrictToLocalhost.isSelected())
									.setPort(Integer.parseInt(fld_port.getText()))
									.setOwner((String) owner.getSelectedItem()).setId(IDGen.listener()).build());
							resetEPanels();
							ep.drop();
						}).start();

					}
				});
				buttonPane.add(okButton);
			}
		}
		{
			panel_1 = new JPanel();
			panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			add(panel_1, BorderLayout.NORTH);
			panel_1.setLayout(new BorderLayout(0, 0));
			{
				sl = new StatusLabel("Create new listener");
				panel_1.add(sl);
			}
		}
	}

	public boolean verify() {
		if (!ValidationUtil.port(fld_port.getText())) {
			sl.setBad("Invalid port");
			return false;
		}

		for (ListenerConfig lc : ViewerProfileStore.getServer().listeners) {
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
		if (ViewerProfileStore.getLocalViewer().getPermissions().getFlag(Perm.Super)) {
			List<ViewerProfile> viewers = ViewerProfileStore.getViewers();
			o = new String[viewers.size()];
			for (int i = 0; i < o.length; i++) {
				o[i] = viewers.get(i).get(AKeySimple.VIEWER_USER);
			}
		} else {
			o = new String[] { ViewerProfileStore.getLocalViewer().get(AKeySimple.VIEWER_USER) };
		}
		owner.setModel(new DefaultComboBoxModel<String>(o));
	}

	public void resetEPanels() {
		UIStore.EAddListener = null;
	}
}
