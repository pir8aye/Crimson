/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.screen.netman.auth;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;

public class AuthPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public EPanel ep;

	public JButton btnExport;

	private AuthTable authTable;

	public AuthPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		ep = new EPanel(panel);
		add(ep, BorderLayout.CENTER);

		authTable = new AuthTable(this);
		panel.add(authTable, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar, BorderLayout.NORTH);

		JButton btnAddGroup = new JButton(UIUtil.getIcon("icons16/general/group_add.png"));
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (UIStore.createGroup == null) {
					new Thread(new Runnable() {
						public void run() {
							resetEPanels();
							UIStore.createGroup = new CreateGroup(ep);
							ep.raise(UIStore.createGroup, 240);
						}
					}).start();
				}
			}
		});
		btnAddGroup.setToolTipText("Create new group");

		btnAddGroup.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnAddGroup);

		JButton btnNewButton_2 = new JButton(UIUtil.getIcon("icons16/general/group_import.png"));
		btnNewButton_2.setToolTipText("Import group from file");
		btnNewButton_2.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnNewButton_2);

		btnExport = new JButton(UIUtil.getIcon("icons16/general/group_export.png"));
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnExport.setEnabled(false);
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Export selected group to file");

				if (jfc.showDialog(null, "Export") == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					new Thread(new Runnable() {
						public void run() {
							Crypto.exportGroup(authTable.getSelected().getGroup(), file);
						}
					}).start();
				}

			}
		});
		btnExport.setEnabled(false);
		btnExport.setToolTipText("Export group to file");
		btnExport.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnExport);

		JButton btnNewButton_1 = new JButton(UIUtil.getIcon("icons16/general/textfield_password_add.png"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetEPanels();

				if (UIStore.createPassword == null) {
					new Thread(new Runnable() {
						public void run() {
							resetEPanels();
							UIStore.createPassword = new CreatePassword(ep);
							ep.raise(UIStore.createPassword, 100);
						}
					}).start();
				}
			}
		});
		btnNewButton_1.setToolTipText("Create new password");
		btnNewButton_1.setMargin(new Insets(2, 2, 2, 2));
		menuBar.add(btnNewButton_1);

	}

	private void resetEPanels() {
		UIStore.createGroup = null;
		UIStore.createPassword = null;
	}

}
