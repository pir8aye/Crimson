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
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.ui.screen.settings.ListHeaderPopup;

public class HostList extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final AttributeKey[] defaultHeaders = new AttributeKey[] { AKeySimple.IPLOC_COUNTRY,
			AKeySimple.OS_NAME, AKeySimple.USER_NAME, AKeySimple.NET_HOSTNAME, AKeySimple.OS_LANGUAGE };

	private static JTable table = new JTable();
	private TM tm = new TM();
	private TR tr = new TR(tm);

	public HostList() {
		init();
		initRowSorter();
	}

	public void init() {
		setLayout(new BorderLayout());
		table.setModel(tm);
		table.setDefaultRenderer(Object.class, tr);
		table.setRowHeight(18);
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
					ContextMenuFactory.getMenu(selected, "list").show(table, e.getX(), e.getY());

				}

			}
		});
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					ListHeaderPopup popup = new ListHeaderPopup(DatabaseStore.getDatabase());
					popup.show(table.getTableHeader(), e.getX(), e.getY());
				}
			}
		});

		JScrollPane jsp = new JScrollPane(table);

		add(jsp, BorderLayout.CENTER);
	}

	public void addClient(ClientProfile cp) {
		tm.add(cp);
	}

	// TODO only update cell
	public void updateField(ClientProfile cp, AttributeKey aa) {
		for (int i = 0; i < tm.getClientList().size(); i++) {
			if (cp.getCvid() == tm.getClientList().get(i).getCvid()) {
				tm.fireTableRowsUpdated(i, i);
				return;
			}
		}
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
		initRowSorter();
	}

	private void initRowSorter() {
		TableRowSorter<TM> sorter = new TableRowSorter<TM>(tm);
		sorter.toggleSortOrder(0);

		for (int i = 0; i < tm.headers.length; i++) {
			AttributeKey aa = tm.headers[i];
			if (aa instanceof AKeySimple) {
				AKeySimple sa = (AKeySimple) aa;
				switch (sa) {
				case CLIENT_CID:
					sorter.setComparator(i, new ClientProfile.CidComparator());
				default:
					sorter.setComparator(i, new ClientProfile.SimpleAttributeComparator(sa));
				}
			} else {
				// TODO complex attribute
			}
		}

		table.setRowSorter(sorter);
	}

	public void select(ClientProfile cp) {
		List<ClientProfile> profiles = tm.getClientList();
		for (int i = 0; i < profiles.size(); i++) {
			if (cp.getCvid() == profiles.get(i).getCvid()) {
				table.setRowSelectionInterval(i, i);
				return;
			}
		}
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public AttributeKey[] headers = new AttributeKey[] {};

	private ArrayList<ClientProfile> clients = new ArrayList<ClientProfile>();

	public ArrayList<ClientProfile> getClientList() {
		return clients;
	}

	public void add(ClientProfile cp) {
		clients.add(cp);
		fireTableRowsInserted(clients.size() - 1, clients.size() - 1);
	}

	public TM() {
		refreshHeaders();
	}

	public void refreshHeaders() {
		try {
			headers = (AttributeKey[]) DatabaseStore.getDatabase().getObject("hostlist.headers");
		} catch (Exception e) {
			headers = HostList.defaultHeaders;
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
		return clients.get(rowIndex);
	}

	public ClientProfile getRow(int selected) {
		return clients.get(selected);
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

		super.getTableCellRendererComponent(table, setupCell(table, (ClientProfile) value, column), isSelected,
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
	private Object setupCell(JTable table, ClientProfile profile, int viewColumn) {
		setBorder(noFocusBorder);

		AttributeKey aa = tm.headers[table.convertColumnIndexToModel(viewColumn)];
		if (aa instanceof AKeySimple) {
			AKeySimple sa = (AKeySimple) aa;
			switch (sa) {
			case IPLOC_COUNTRY:
				return profile.getLocationIcon();
			case OS_NAME:
				return profile.getOsNameIcon();
			default:
				return profile.get(sa);
			}
		} else {
			// TODO complex attribute
			return null;
		}
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
