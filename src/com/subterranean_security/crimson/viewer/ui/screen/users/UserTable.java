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
package com.subterranean_security.crimson.viewer.ui.screen.users;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;

public class UserTable extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
	private TM tm = new TM();

	public UserTable(UsersPanel parent) {
		table.setModel(tm);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		setViewportView(table);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// get source of click
				JTable source = (JTable) e.getSource();
				final int sourceRow = source.rowAtPoint(e.getPoint());
				parent.btnRemove.setEnabled(sourceRow >= 0);
				parent.btnEditPermissions.setEnabled(sourceRow >= 0);
				if (sourceRow == -1) {
					source.clearSelection();
					return;
				}

				ViewerProfile selected = tm.getAt(sourceRow);
				if (ViewerStore.Profiles.getLocalViewer().getUser().equals(selected.getUser())) {
					parent.btnRemove.setEnabled(false);
				}

				// select row
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}

				if (e.getButton() == MouseEvent.BUTTON3) {
					// right click

				}
			}
		});
	}

	public void fireTableDataChanged() {
		tm.fireTableDataChanged();
	}

	public ViewerProfile getSelected() {
		return tm.getAt(table.getSelectedRow());
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final String[] headers = new String[] { "Username", "Login Time", "Login IP", "Superuser" };

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return ViewerStore.Profiles.getServer().users.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (headers[columnIndex]) {
		case "Username": {
			return ViewerStore.Profiles.getServer().users.get(rowIndex).getUser();
		}
		case "Login Time": {
			if (ViewerStore.Profiles.getServer().users.get(rowIndex).getLoginTime() == null) {
				return "";
			}
			if (ViewerStore.Profiles.getServer().users.get(rowIndex).getLoginTime().getTime() == 0) {
				return "<hidden>";
			}
			return ViewerStore.Profiles.getServer().users.get(rowIndex).getLoginTime().toString();
		}
		case "Login IP": {
			return ViewerStore.Profiles.getServer().users.get(rowIndex).getIp();
		}
		case "Superuser": {
			return ViewerStore.Profiles.getServer().users.get(rowIndex).getPermissions().getFlag(Perm.Super) ? "yes" : "no";
		}

		}
		return null;
	}

	public ViewerProfile getAt(int row) {
		return ViewerStore.Profiles.getServer().users.get(row);
	}

	public void removeAt(int row) {
		ViewerStore.Profiles.getServer().users.remove(row);
	}

}
