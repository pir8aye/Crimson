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
package com.subterranean_security.crimson.viewer.ui.screen.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.UINotification;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.NormalMenu;

public class GenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public HPanel hp;
	private GenPanel gp = new GenPanel();

	public GenDialog() {
		init();
	}

	private void init() {
		setTitle("Generator");
		setIconImages(UIUtil.getIconList());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 455, 380);
		getContentPane().setLayout(new BorderLayout(0, 0));

		hp = new HPanel(gp);
		hp.init(initNormalMenu(), initHiddenMenu());
		hp.setHMenuHeight(72);

		getContentPane().add(hp);
	}

	private NormalMenu initNormalMenu() {
		JButton btn_gen = new JButton("Generate");
		btn_gen.setPreferredSize(new Dimension(75, 25));
		btn_gen.setMargin(new Insets(2, 5, 2, 5));
		btn_gen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {

						btn_gen.setEnabled(false);
						ClientConfig config = gp.getValues();
						String out = gp.fld_path.getText();
						Date creation = gp.currentCTime;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							return;
						}
						if (gp.testValues(config)) {
							UINotification.addConsoleInfo("Generating target on server...");
							dispose();

							if (config.getAuthType() == AuthType.GROUP
									&& ((String) gp.atab.groupSelectionBox.getSelectedItem()).equals("Create Group")) {
								if (!ViewerCommands.createAuthMethod(AuthMethod.newBuilder()
										.addOwner(ProfileStore.getLocalViewer().get(AKeySimple.VIEWER_USER))
										.setId(IDGen.auth()).setCreation(new Date().getTime())
										.setType(AuthType.GROUP).setName(config.getGroupName())
										.setGroupSeedPrefix(gp.getGroupPrefix()).build()).getResult()) {
									UINotification.addConsoleBad("Failed to create authentication group");
									return;
								}
							} else if (config.getAuthType() == AuthType.PASSWORD
									&& !gp.atab.chckbxDontInstallPassword.isSelected()) {
								if (!ViewerCommands.createAuthMethod(AuthMethod.newBuilder()
										.addOwner(ProfileStore.getLocalViewer().get(AKeySimple.VIEWER_USER))
										.setId(IDGen.auth()).setCreation(new Date().getTime())
										.setType(AuthType.PASSWORD).setName(gp.atab.fld_password_name.getText())
										.setPassword(gp.atab.getPassword()).build()).getResult()) {
									UINotification.addConsoleBad("Failed to create password");
									return;
								}
							}
							ViewerCommands.generate(config, out, creation);

						} else {
							btn_gen.setEnabled(true);
						}

					}
				}).start();
			}
		});

		NormalMenu nmenu = new NormalMenu();
		nmenu.setButtons(Box.createHorizontalStrut(btn_gen.getPreferredSize().width), Box.createHorizontalGlue(),
				hp.getUpBtn(), Box.createHorizontalGlue(), btn_gen);
		return nmenu;
	}

	private HiddenMenu initHiddenMenu() {
		JButton btn_loadProfile = new JButton("Load Profile");
		btn_loadProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if (LoadDialog.global == null) {
				// LoadDialog.global = new LoadDialog();
				// }

				// LoadDialog.global.setVisible(true);
			}
		});

		JButton btn_newProfile = new JButton("Create Profile");
		btn_newProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if (CreateDialog.global == null) {
				// CreateDialog.global = new CreateDialog();
				// }

				// CreateDialog.global.setVisible(true);
			}
		});

		JButton btn_newListener = new JButton("Create Corresponding Listener");
		btn_newListener.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {

					}
				}).start();
			}
		});

		JButton help = new JButton("Show Help");
		help.setFont(new Font("Dialog", Font.BOLD, 9));
		help.setMargin(new Insets(0, 5, 0, 5));

		HiddenMenu hmenu = new HiddenMenu(true, help);
		return hmenu;
	}

	@Override
	public void dispose() {
		UIStore.genDialog = null;
		gp.cancelTimer();
		super.dispose();
	}

}
