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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;

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
				if (ViewerProfileStore.getLocalViewer().get(AKeySimple.VIEWER_USER)
						.equals(selected.get(AKeySimple.VIEWER_USER))) {
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

enum Headers {
	USERNAME, LAST_LOGIN, IP, LOCATION, PERMISSIONS;

	@Override
	public String toString() {
		switch (this) {
		case IP:
			return "IP";
		case LAST_LOGIN:
			return "Last Login";
		case LOCATION:
			return "Location";
		case PERMISSIONS:
			return "Permissions";
		case USERNAME:
			return "Username";
		}
		return super.toString();
	}
}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final Headers[] headers = new Headers[] { Headers.USERNAME, Headers.LAST_LOGIN, Headers.IP,
			Headers.LOCATION, Headers.PERMISSIONS };

	private List<ViewerProfile> users;

	public TM() {
		users = ViewerProfileStore.getViewers();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return users.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].toString();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (headers[columnIndex]) {

		case LAST_LOGIN: {
			if (users.get(rowIndex).get(AKeySimple.VIEWER_LOGIN_TIME) == null) {
				return "";
			}
			if (users.get(rowIndex).get(AKeySimple.VIEWER_LOGIN_TIME).equals("0")) {
				return "<hidden>";
			}
			return users.get(rowIndex).get(AKeySimple.VIEWER_LOGIN_TIME);
		}
		case USERNAME:
			return users.get(rowIndex).get(AKeySimple.VIEWER_USER);
		case IP:
			return users.get(rowIndex).get(AKeySimple.VIEWER_LOGIN_IP);
		case PERMISSIONS:
			return users.get(rowIndex).getPermissions().getFlag(Perm.Super) ? "yes" : "no";// TODO
		case LOCATION:
			return users.get(rowIndex).getLocationIcon16();

		}

		return null;
	}

	public ViewerProfile getAt(int row) {
		return users.get(row);
	}

}

class TR extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Color SUPERUSER_COLOR = new Color(146, 217, 123);

	private TM tm;

	public TR(TM tm) {
		this.tm = tm;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		System.out.println("Getting render component for: (" + row + ", " + column + ")");
		setBorder(noFocusBorder);

		// set the background of this cell if this user is a superuser
		if (tm.getAt(table.convertRowIndexToModel(row)).getPermissions().getFlag(Perm.Super)) {
			setBackground(SUPERUSER_COLOR);
		}

		return this;
	}

	protected void setValue(Object value) {
		if (value instanceof ImageIcon) {
			ImageIcon ico = (ImageIcon) value;
			setIcon(ico);
			setText(ico.getDescription());
		} else {
			setIcon(null);
			super.setValue(value);
		}
	}

}
