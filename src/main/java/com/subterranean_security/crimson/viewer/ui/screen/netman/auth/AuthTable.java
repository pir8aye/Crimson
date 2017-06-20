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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class AuthTable extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
	private TM tm = new TM();

	private JPopupMenu popup;
	private JMenuItem export;
	private JMenuItem copyPassword;

	public AuthTable(AuthPanel parent) {
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
				if (sourceRow == -1) {
					source.clearSelection();
					parent.btnExport.setEnabled(false);
					parent.resetEPanels();
					parent.ep.drop();
					return;
				}
				// select row
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}

				AuthMethod am = getSelected();

				if (e.getButton() == MouseEvent.BUTTON3) {
					// right click
					popup = new JPopupMenu();
					if (am.getType() == AuthType.GROUP) {
						export = new JMenuItem("Export Group");
						export.setIcon(UIUtil.getIcon("icons16/general/group_export.png"));

						popup.add(export);
					} else {
						copyPassword = new JMenuItem("Copy Password");
						copyPassword.setIcon(UIUtil.getIcon("icons16/general/page_copy.png"));
						popup.add(copyPassword);
					}
					popup.show(table, e.getX(), e.getY());
				} else {
					if (am.getType() == AuthType.GROUP) {
						parent.btnExport.setEnabled(true);

						parent.resetEPanels();
						parent.ep.raise(new GroupInfo(am), 120);
					} else {
						parent.btnExport.setEnabled(false);

						parent.resetEPanels();
						parent.ep.raise(new PasswordInfo(), 100);
					}
				}
			}
		});
	}

	public void fireTableDataChanged() {
		tm.fireTableDataChanged();
	}

	public AuthMethod getSelected() {
		return tm.getAt(table.getSelectedRow());
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final String[] headers = new String[] { "ID", "Authentication Type", "Name", "Creation Date" };

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return ViewerProfileStore.getServer().authMethods.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (headers[columnIndex]) {
		case "ID":
			return ViewerProfileStore.getServer().authMethods.get(rowIndex).getId();
		case "Authentication Type":
			return ViewerProfileStore.getServer().authMethods.get(rowIndex).getType();
		case "Name":
			return ViewerProfileStore.getServer().authMethods.get(rowIndex).getName();
		case "Creation Date":
			return new Date(ViewerProfileStore.getServer().authMethods.get(rowIndex).getCreation()).toString();

		}
		return null;
	}

	public AuthMethod getAt(int row) {
		return ViewerProfileStore.getServer().authMethods.get(row);
	}

	public void removeAt(int row) {
		ViewerProfileStore.getServer().authMethods.remove(row);
	}

}
