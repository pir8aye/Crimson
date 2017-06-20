package com.subterranean_security.charcoal.ui.components.details;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.Profile;

public class ProfileDetail extends TableDetail {

	private static final long serialVersionUID = 1L;

	public ProfileDetail() {
		table.setModel(new TM());
	}

	@Override
	public void nowOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nowClosed() {
		// TODO Auto-generated method stub

	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public String[] headers = new String[] { "Cvid", "Type", "Viewer User" };

	private List<Profile> clients;

	public void add(ClientProfile cp) {
		clients.add(cp);
		fireTableRowsInserted(clients.size() - 1, clients.size() - 1);
	}

	public void remove(ClientProfile cp) {
		int index = clients.indexOf(cp);
		clients.remove(cp);
		fireTableRowsDeleted(index, index);
	}

	public TM() {
		clients = new ArrayList<Profile>();
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

	public Profile getRow(int selected) {
		return clients.get(selected);
	}

}