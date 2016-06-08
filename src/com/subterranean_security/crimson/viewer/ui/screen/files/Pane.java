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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class Pane extends JPanel {

	private static final long serialVersionUID = 1L;
	private FMPanel parent;

	public enum TYPE {
		SERVER, VIEWER, CLIENT;
	}

	public FileTable ft = new FileTable(this);

	protected TYPE type = TYPE.VIEWER;

	// for viewers
	private LocalFilesystem lf = new LocalFilesystem(true, true);

	private int cid;
	private int fmid;

	public boolean loading = false;
	public PathPanel pwd = new PathPanel();
	private JComboBox typeBox;

	public Pane(FMPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		add(ft, BorderLayout.CENTER);

		typeBox = new JComboBox();
		typeBox.setBackground(UIManager.getColor("Menu.background"));
		typeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageIcon selected = (ImageIcon) typeBox.getSelectedItem();
				String name = selected.getDescription().toLowerCase();
				new Thread(new Runnable() {
					public void run() {

						if (fmid != 0) {
							ViewerCommands.closeFileHandle(cid, fmid);
						}

						switch (name) {
						case "viewer": {
							type = TYPE.VIEWER;
							break;
						}
						case "server": {
							type = TYPE.SERVER;
							cid = 0;
							fmid = ViewerCommands.getFileHandle(cid);
							break;
						}
						default: {
							type = TYPE.CLIENT;
							cid = ViewerStore.Profiles.getClient(name).getCvid();
							System.out.println("Found cid: " + cid);
							fmid = ViewerCommands.getFileHandle(cid);
							break;
						}
						}
						refresh();
					}
				}).start();

			}
		});
		typeBox.setRenderer(new ComboBoxRenderer());
		typeBox.setModel(new FileComboBoxModel());
		typeBox.setSelectedIndex(0);
		add(typeBox, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		pwd.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panel.add(pwd, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar, BorderLayout.NORTH);

		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.setRequestFocusEnabled(false);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pwd.openView();
				up();
			}
		});
		btnNewButton_1.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton_1.setIcon(UIUtil.getIcon("icons16/general/folder_up.png"));
		menuBar.add(btnNewButton_1);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

	}

	public void properties() {

	}

	public void up() {
		new Thread(new Runnable() {
			public void run() {
				beginLoading();
				Date start = new Date();
				switch (type) {
				case CLIENT:
				case SERVER:
					ViewerCommands.fm_up(ft, cid, fmid, true, true);
					break;
				case VIEWER:
					lf.up();
					refresh();
					break;

				}
				parent.console.addLine("Moved up in: " + (new Date().getTime() - start.getTime()) + " milliseconds");
				stopLoading();
			}
		}).start();

	}

	public void down(final String s) {

		new Thread(new Runnable() {
			public void run() {
				beginLoading();
				Date start = new Date();
				switch (type) {
				case CLIENT:
				case SERVER:
					ViewerCommands.fm_down(ft, cid, fmid, s, true, true);
					break;
				case VIEWER:
					lf.down(s);
					refresh();
					break;

				}
				parent.console.addLine("Moved down in: " + (new Date().getTime() - start.getTime()) + " milliseconds");
				stopLoading();
			}
		}).start();

	}

	public void refresh() {
		// TODO investigate ways to remove thread overhead
		// probably by declaring the Runnable as a field
		new Thread(new Runnable() {
			public void run() {
				beginLoading();
				switch (type) {
				case CLIENT:
				case SERVER:
					ViewerCommands.fm_list(ft, cid, fmid, true, true);
					break;
				case VIEWER:
					try {
						ft.setFiles(lf.list());
						pwd.setPwd(lf.pwd());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				}
				stopLoading();
			}
		}).start();

	}

	public void beginLoading() {
		loading = true;
		pwd.beginLoading();
		ft.setEnabled(false);
		typeBox.setEnabled(false);
	}

	public void stopLoading() {
		loading = false;
		pwd.stopLoading();
		ft.setEnabled(true);
		typeBox.setEnabled(true);
	}

}
