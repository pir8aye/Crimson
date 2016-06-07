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
package com.subterranean_security.crimson.viewer.ui.common.components;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class DataViewer extends JPanel {

	private static final long serialVersionUID = 1L;
	private DataViewerModel dvm;

	public DataViewer() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		dvm = new DataViewerModel();
		JTable table = new JTable();
		table.setModel(dvm);

		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
	}

	public void setList(ArrayList<String[]> l) {
		dvm.setList(l);

	}

	public void setHeaders(String[] h) {
		dvm.setHeaders(h);

	}

}

class DataViewerModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private String[] headers = { "Name", "Value" };// default
	public ArrayList<String[]> values;

	public DataViewerModel() {
		values = new ArrayList<String[]>();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return values.get(rowIndex)[columnIndex];
	}

	public void setHeaders(String[] h) {
		headers = h;
		this.fireTableStructureChanged();
	}

	public void setList(ArrayList<String[]> l) {
		values = l;
		this.fireTableDataChanged();

	}

}