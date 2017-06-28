/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.screen.torrent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_TORRENT;

public class TorrentList extends JPanel {

	private static final long serialVersionUID = 1L;

	public TM tm = new TM();
	public TR tr = new TR(tm);

	private JTable table = new JTable();

	public TorrentList() {
		init();
		initRowSorter();
	}

	public void init() {

		setLayout(new BorderLayout());

		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, tr);
		table.setModel(tm);

		JScrollPane jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);

	}

	private void initRowSorter() {
		TableRowSorter<TM> sorter = new TableRowSorter<TM>(tm);
		sorter.toggleSortOrder(0);

		// TODO custom comparators
		// sorter.setComparator(0, new FileItem.NameComparator());

		table.setRowSorter(sorter);
	}
}

enum Headers {
	NAME, SIZE, DOWNLOADED, UPLOADED, DOWNLOAD_RATE, UPLOAD_RATE, COMPLETE, SEEDERS, PEERS;
}

class TM extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	public Headers[] headers = new Headers[] { Headers.NAME };
	private ArrayList<AttributeGroup> torrents = new ArrayList<AttributeGroup>();

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return torrents.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].name();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return torrents.get(rowIndex);
	}

}

class TR extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private TM tm;

	public TR(TM tm) {
		this.tm = tm;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		super.getTableCellRendererComponent(table, setupCell(table, (AttributeGroup) value, column), isSelected,
				hasFocus, row, column);
		return this;
	}

	/**
	 * Setup and return the data for the given cell
	 * 
	 * @param table
	 * @param file
	 * @param viewColumn
	 * @return
	 */
	private Object setupCell(JTable table, AttributeGroup file, int viewColumn) {
		setBorder(noFocusBorder);

		switch (tm.headers[table.convertColumnIndexToModel(viewColumn)]) {
		case NAME: {
			setHorizontalAlignment(SwingConstants.LEFT);
			return file.getAttribute(AK_TORRENT.NAME).get();
		}
		case SIZE: {
			setHorizontalAlignment(viewColumn == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
			return file.getAttribute(AK_TORRENT.SIZE).get();
		}
		}
		return null;
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
