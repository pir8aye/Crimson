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
package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_LISTENER;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;

public class ListenerTable extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
	private TM tm = new TM();

	public ListenerTable(ListenerPanel parent) {
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
				if (sourceRow == -1) {
					source.clearSelection();
					return;
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

	public AttributeGroup getSelected() {
		return tm.getAt(table.getSelectedRow());
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final AttributeKey[] headers = new AttributeKey[] { AK_LISTENER.ID, AK_LISTENER.NAME, AK_LISTENER.PORT,
			AK_LISTENER.UPNP, AK_LISTENER.OWNER };
	private List<AttributeGroup> listeners;

	public TM() {
		listeners = ViewerProfileStore.getServer().getGroupsOfType(TypeIndex.LISTENER);
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return listeners.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].toString();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return listeners.get(rowIndex).getAttribute(headers[columnIndex]).get();
	}

	public AttributeGroup getAt(int row) {
		return listeners.get(row);
	}

}
