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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class GenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public HPanel hp;
	private GenPanel gp = new GenPanel();

	public GenDialog() {
		setTitle("Generator");
		setIconImages(UIUtil.getIconList());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 455, 380);
		getContentPane().setLayout(new BorderLayout(0, 0));

		hp = new HPanel(gp);

		final JButton btn_gen = new JButton("Generate");
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
							MainFrame.main.np.addNote("info", "Please wait while the server generates your installer");
							dispose();
							ViewerCommands.generate(config, out, creation);

						} else {
							btn_gen.setEnabled(true);
						}

					}
				}).start();
			}
		});
		Component[] buttons = { Box.createHorizontalStrut(btn_gen.getPreferredSize().width), Box.createHorizontalGlue(),
				hp.initBtnUP(), Box.createHorizontalGlue(), btn_gen };
		hp.nmenu.setButtons(buttons);

		final JButton btn_loadProfile = new JButton("Load Profile");
		btn_loadProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if (LoadDialog.global == null) {
				// LoadDialog.global = new LoadDialog();
				// }

				// LoadDialog.global.setVisible(true);
			}
		});

		final JButton btn_newProfile = new JButton("Create Profile");
		btn_newProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if (CreateDialog.global == null) {
				// CreateDialog.global = new CreateDialog();
				// }

				// CreateDialog.global.setVisible(true);
			}
		});

		final JButton btn_newListener = new JButton("Create Corresponding Listener");
		btn_newListener.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {

					}
				}).start();
			}
		});

		hp.hmenu.addButton(btn_loadProfile);
		hp.hmenu.addButton(btn_newProfile);
		hp.hmenu.addButton(btn_newListener);

		hp.hmenu.setDesc(
				"Use this dialog to generate an installer in the format of your choice.  The installer can then be used to install Crimson on clients.  The jar output type is most commonly used.");

		hp.refreshHeight();
		getContentPane().add(hp);
	}

	@Override
	public void dispose() {
		gp.cancelTimer();
		super.dispose();
	}

}
