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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;

public class NetworkTargetTable extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
	private TM tm = new TM();

	public NetworkTargetTable() {
		table.setModel(tm);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		setViewportView(table);

		addMouseListener(new MouseAdapter() {
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

				if (e.getButton() == MouseEvent.BUTTON3) {
					NetworkTarget selected = tm.getAt(sourceRow);
					// right click

				}
			}
		});
	}

	public void add(NetworkTarget nt) {
		tm.add(nt);
	}

	public ArrayList<NetworkTarget> getTargets() {
		return tm.getTargets();
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final String[] headers = new String[] { "Address", "Port" };
	private ArrayList<NetworkTarget> targets = new ArrayList<NetworkTarget>();

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return targets.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (headers[columnIndex]) {
		case "Address": {
			return targets.get(rowIndex).getServer();
		}
		case "Port": {
			return "" + targets.get(rowIndex).getPort();
		}

		}
		return null;
	}

	public void add(NetworkTarget nt) {
		targets.add(nt);
		this.fireTableDataChanged();
	}

	public ArrayList<NetworkTarget> getTargets() {
		return targets;
	}

	public NetworkTarget getAt(int row) {
		return targets.get(row);
	}

	public void removeAt(int row) {
		targets.remove(row);
	}

}
