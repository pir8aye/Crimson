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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.subterranean_security.crimson.core.storage.Headers;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ViewerStore;

public class HostList extends JPanel {

	private static final long serialVersionUID = 1L;

	private static JTable table = new JTable();
	private TM tm = new TM();
	private TR tr = new TR();

	public HostList() {
		init();
	}

	public void init() {
		setLayout(new BorderLayout());
		table.setModel(tm);
		table.setDefaultRenderer(Object.class, tr);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (MainFrame.main.dp.isMoving()) {
					return;
				}

				// get source of click
				int sourceRow = table.rowAtPoint(e.getPoint());
				if (sourceRow == -1) {
					table.clearSelection();
					MainFrame.main.dp.closeDetail();
					return;
				} else if (!table.isRowSelected(sourceRow)) {
					// select row
					table.changeSelection(sourceRow, 0, false, false);
				}

				ClientProfile selected = tm.getRow(sourceRow);

				if (e.getButton() == MouseEvent.BUTTON1) {
					// open up the detail
					MainFrame.main.dp.showDetail(selected);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					ContextMenu.getMenu(selected, "list").show(table, e.getX(), e.getY());

				}

			}
		});

		JScrollPane jsp = new JScrollPane(table);

		add(jsp, BorderLayout.CENTER);
	}

	public void addOrUpdate(ClientProfile cp) {
		cp.loadIcons();
		for (int i = 0; i < tm.getClientList().size(); i++) {
			if (cp.getCvid() == tm.getClientList().get(i).getCvid()) {
				tm.fireTableRowsUpdated(i, i);
				return;
			}
		}
		tm.add(cp);
	}

	public void removeClient(ClientProfile cp) {
		for (int i = 0; i < tm.getClientList().size(); i++) {
			if (cp.getCvid() == tm.getClientList().get(i).getCvid()) {
				tm.getClientList().remove(i);
				tm.fireTableRowsDeleted(i, i);
				return;
			}
		}
	}

	public void refreshHeaders() {
		tm.refreshHeaders();
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	public Headers[] headers = new Headers[] {};

	private ArrayList<ClientProfile> clients = new ArrayList<ClientProfile>();

	public ArrayList<ClientProfile> getClientList() {
		return clients;
	}

	public void add(ClientProfile cp) {
		if (ViewerState.trialMode && clients.size() == 1) {
			MainFrame.main.np.addNote("info", "Host limitation (1) has been reached");
			return;
		}

		clients.add(cp);
		fireTableRowsInserted(clients.size() - 1, clients.size() - 1);
	}

	public TM() {
		refreshHeaders();
	}

	public void refreshHeaders() {
		try {
			headers = (Headers[]) ViewerStore.Databases.local.getObject("hostlist.headers");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.fireTableStructureChanged();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return clients.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].toString();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (headers[columnIndex]) {
		case USERNAME:
			return clients.get(rowIndex).getUsername();
		case USER_STATUS:
			return clients.get(rowIndex).getUserStatus();
		case HOSTNAME:
			return clients.get(rowIndex).getHostname();
		case INTERNAL_IP:
			return null;
		case EXTERNAL_IP:
			return clients.get(rowIndex).getExtIp();
		case LANGUAGE:
			return clients.get(rowIndex).getLanguage();
		case ACTIVE_WINDOW:
			return clients.get(rowIndex).getActiveWindow();
		case IP_LOCATION:
			return clients.get(rowIndex).getLocationIcon();
		case CPU_MODEL:
			return clients.get(rowIndex).getCpuModel();
		case CPU_USAGE:
			return clients.get(rowIndex).getCpuUsage();
		case CPU_TEMP:
			return clients.get(rowIndex).getCpuTempAverage();
		case CVID:
			return clients.get(rowIndex).getCvid();
		case RAM_CAPACITY:
			return clients.get(rowIndex).getSystemRamCapacity();
		case RAM_USAGE:
			return clients.get(rowIndex).getSystemRamUsage();
		case CRIMSON_VERSION:
			return clients.get(rowIndex).getCrimsonVersion();
		case OS_FAMILY:
			return clients.get(rowIndex).getOsFamily();
		case OS_ARCH:
			return clients.get(rowIndex).getOsArch();
		case JAVA_VERSION:
			return clients.get(rowIndex).getJavaVersion();
		case MONITOR_COUNT:
			return null;
		case VIRTUALIZATION:
			return clients.get(rowIndex).getVirtualization();
		case TIMEZONE:
			return clients.get(rowIndex).getTimezone();
		case CPU_SPEED:
			return null;
		case MESSAGE_PING:
			return clients.get(rowIndex).getMessageLatency();
		case SCREEN_PREVIEW:
			return null;
		case OS_NAME:
			return clients.get(rowIndex).getOsNameIcon();
		default:
			return null;

		}

	}

	public ClientProfile getRow(int selected) {
		return clients.get(selected);
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
			setText((String) value);
		}
	}

}
