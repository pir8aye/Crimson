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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.viewer.ViewerStore;

public class HostList extends JPanel {

	private static final long serialVersionUID = 1L;

	private static JTable table = new JTable();
	private TM tm = new TM();
	private TR tr = new TR();

	public HostList() {
		setLayout(new BorderLayout());
		table.setModel(tm);
		table.setDefaultRenderer(Object.class, tr);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);

		JScrollPane jsp = new JScrollPane(table);

		add(jsp, BorderLayout.CENTER);
	}

	public void refreshTM() {
		tm.fireTableDataChanged();
	}
}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	String[] headers = new String[] { "Username", "Hostname" };

	public void setHeaders(ArrayList<String> headers) {
		// this.headers = headers;
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return ViewerStore.Profiles.profiles.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (headers[columnIndex].toLowerCase()) {
		case "username": {
			return ViewerStore.Profiles.profiles.get(rowIndex).getUsername();
		}
		case "activity": {

		}
		case "status": {

		}
		case "hostname": {
			return ViewerStore.Profiles.profiles.get(rowIndex).getHostname();
		}
		case "system uptime": {

		}
		case "java uptime": {

		}
		case "Internal IP": {

		}
		case "External IP": {

		}
		case "Primary MAC Address": {

		}
		case "Language": {

		}
		case "Active Window": {

		}
		case "Location": {

		}
		case "Distance": {

		}
		case "CPU Name": {

		}
		case "CPU Usage": {

		}
		case "CPU Temperature": {

		}
		case "RAM Capacity": {

		}
		case "RAM Usage": {

		}
		case "Version": {

		}
		case "Operating System": {

		}
		case "OS Architecture": {

		}
		case "Java Version": {

		}
		case "Monitors": {

		}
		case "Upload Speed": {

		}
		case "Download Speed": {

		}
		case "Virtualization": {

		}
		case "Timezone": {

		}

		}
		return null;
	}

}

class TR extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(noFocusBorder);
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
