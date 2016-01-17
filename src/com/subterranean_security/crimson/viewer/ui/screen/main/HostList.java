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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// get source of click
				JTable source = (JTable) e.getSource();
				final int sourceRow = source.rowAtPoint(e.getPoint());
				if (sourceRow == -1) {
					source.clearSelection();
					MainFrame.main.dp.closeDetail();
					return;
				}
				// select row
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}
				// final Connection selected = h.getHostConnection(sourceRow);

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

					JMenu quick = new JMenu();
					quick.setText("Quick Commands");
					popup.add(quick);

					JMenuItem poweroff = new JMenuItem();
					poweroff.setText("Shutdown");
					poweroff.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {

							new Thread() {
								public void run() {

								}
							}.start();

						}

					});
					quick.add(poweroff);

					JMenuItem restart = new JMenuItem();
					restart.setText("Restart");
					restart.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {

							new Thread() {
								public void run() {

								}
							}.start();

						}

					});
					quick.add(restart);

					JMenuItem refresh = new JMenuItem();
					refresh.setText("Refresh Information");
					refresh.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {

							new Thread() {
								public void run() {

								}
							}.start();

						}

					});
					quick.add(refresh);

					popup.show(table, e.getX(), e.getY());

				} else {
					// open up the detail
					MainFrame.main.dp.showDetail();
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

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	String[] headers = new String[] { "Username", "Hostname" };

	public TM() {
		refreshHeaders();
	}

	public void refreshHeaders() {
		try {
			headers = (String[]) ViewerStore.Databases.local.getObject("list_headers");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("Failed to update list headers");
			e.printStackTrace();
		}
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
		switch (headers[columnIndex]) {
		case "Username": {
			return ViewerStore.Profiles.profiles.get(rowIndex).getUsername();
		}
		case "Activity": {

		}
		case "Status": {

		}
		case "Hostname": {
			return ViewerStore.Profiles.profiles.get(rowIndex).getHostname();
		}
		case "System Uptime": {

		}
		case "Java Uptime": {

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
