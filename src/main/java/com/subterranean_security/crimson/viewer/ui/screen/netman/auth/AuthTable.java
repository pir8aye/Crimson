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
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
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

				AttributeGroup am = getSelected();

				if (e.getButton() == MouseEvent.BUTTON3) {
					// right click
					popup = new JPopupMenu();
					if (AuthType.GROUP.toString().equals(am.getStr(AK_AUTH.TYPE))) {
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
					if (AuthType.GROUP.toString().equals(am.getStr(AK_AUTH.TYPE))) {
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

	public AttributeGroup getSelected() {
		return tm.getAt(table.getSelectedRow());
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final AttributeKey[] headers = new AttributeKey[] { AK_AUTH.ID, AK_AUTH.TYPE, AK_AUTH.NAME,
			AK_AUTH.CREATION_DATE };

	private List<AttributeGroup> authList;

	public TM() {
		authList = ProfileStore.getServer().getGroupsOfType(TypeIndex.AUTH);
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return authList.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].toString();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return authList.get(rowIndex).getAttribute(headers[columnIndex]).get();
	}

	public AttributeGroup getAt(int row) {
		return authList.get(row);
	}

}
