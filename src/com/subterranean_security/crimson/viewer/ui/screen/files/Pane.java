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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;

public class Pane extends JPanel {

	private static final long serialVersionUID = 1L;
	private FMPanel parent;

	public enum TYPE {
		SERVER, VIEWER, CLIENT;
	}

	public FileTable ft = new FileTable(this);

	private TYPE type = TYPE.VIEWER;

	// for viewers
	private LocalFilesystem lf = new LocalFilesystem();

	private int cid;
	private int fmid;

	public boolean loading = false;

	public Pane(FMPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		add(ft, BorderLayout.CENTER);

		final JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageIcon selected = (ImageIcon) comboBox.getSelectedItem();
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
		comboBox.setRenderer(new ComboBoxRenderer());
		comboBox.setModel(new FileComboBoxModel());
		comboBox.setSelectedIndex(0);
		add(comboBox, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton = new JButton("UP");
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnNewButton, BorderLayout.WEST);

		PathPanel path = new PathPanel();
		panel.add(path, BorderLayout.CENTER);

		refresh();
	}

	public void up() {
		new Thread(new Runnable() {
			public void run() {
				Date start = new Date();
				switch (type) {
				case CLIENT:
				case SERVER:
					ft.setFiles(ViewerCommands.fm_up(cid, fmid, true, true));
					break;
				case VIEWER:
					lf.up();
					refresh();
					break;

				}
				parent.console.addLine("Moved up in: " + (new Date().getTime() - start.getTime()) + " milliseconds");
			}
		}).start();

	}

	public void down(final String s) {

		new Thread(new Runnable() {
			public void run() {
				Date start = new Date();
				switch (type) {
				case CLIENT:
				case SERVER:
					ft.setFiles(ViewerCommands.fm_down(cid, fmid, s, true, true));
					break;
				case VIEWER:
					lf.down(s);
					refresh();
					break;

				}
				parent.console.addLine("Moved down in: " + (new Date().getTime() - start.getTime()) + " milliseconds");
			}
		}).start();

	}

	public void refresh() {
		// TODO investigate ways to remove thread overhead
		// probably by declaring the Runnable as a field
		new Thread(new Runnable() {
			public void run() {
				switch (type) {
				case CLIENT:
				case SERVER:
					ft.setFiles(ViewerCommands.fm_list(cid, fmid, true, true));
					break;
				case VIEWER:
					try {
						ft.setFiles(lf.list());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				}
			}
		}).start();

	}

}
