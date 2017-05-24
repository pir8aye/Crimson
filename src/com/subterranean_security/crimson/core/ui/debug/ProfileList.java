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
package com.subterranean_security.crimson.core.ui.debug;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;

public class ProfileList extends JPanel {

	private static final long serialVersionUID = 1L;

	private static JTable table = new JTable();
	private ProfileTM tm = new ProfileTM();

	public ProfileList() {
		setLayout(new BorderLayout());
		table.setModel(tm);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// get source of click
				JTable source = (JTable) e.getSource();
				final int sourceRow = source.rowAtPoint(e.getPoint());
				if (sourceRow == -1) {
					source.clearSelection();
					return;
				}
				// select row
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}

				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {

					// right click on table
					JPopupMenu popup = new JPopupMenu();
					JMenuItem control = new JMenuItem();
					control.setText("Control Panel");
					control.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {

							new Thread() {
								public void run() {

								}
							}.start();

						}

					});
					popup.add(control);

					popup.show(table, e.getX(), e.getY());

				}
			}
		});

		JScrollPane jsp = new JScrollPane(table);

		add(jsp, BorderLayout.CENTER);
	}

	public void refreshTM() {
		tm.fireTableDataChanged();
	}
}

class ProfileTM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] headers = new String[] { "ClientID", "Hostname" };

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if (Universal.instance == Universal.Instance.VIEWER) {

			// return ViewerStore.Profiles.clients.size();
			return 0;
		} else {
			return 0;// TODO
		}
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ClientProfile p = null;
		if (Universal.instance == Universal.Instance.VIEWER) {

			// p = ViewerStore.Profiles.clients.get(rowIndex);

		}
		switch (headers[columnIndex]) {
		case "ClientID": {
			return "" + p.getCvid();
		}

		}
		return null;
	}

}
