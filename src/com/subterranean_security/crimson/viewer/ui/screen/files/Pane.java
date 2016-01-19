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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.fm.LocalFilesystem;

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

	public boolean loading = false;

	public Pane(FMPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		add(ft, BorderLayout.CENTER);

		final JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageIcon selected = (ImageIcon) comboBox.getSelectedItem();
				switch (selected.getDescription().toLowerCase()) {
				case "viewer": {
					type = TYPE.VIEWER;
					break;
				}
				case "server": {
					type = TYPE.SERVER;
					break;
				}
				default: {
					type = TYPE.CLIENT;
					break;
				}
				}
				refresh();
			}
		});
		comboBox.setRenderer(new ComboBoxRenderer());
		comboBox.setModel(new FileComboBoxModel());
		add(comboBox, BorderLayout.SOUTH);
		refresh();
	}

	public void up() {
		new Thread(new Runnable() {
			public void run() {
				Date start = new Date();
				switch (type) {
				case CLIENT:
					break;
				case SERVER:
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
					break;
				case SERVER:
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
					break;
				case SERVER:
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
