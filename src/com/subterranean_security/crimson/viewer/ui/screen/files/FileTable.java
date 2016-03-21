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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.util.CUtil;

public class FileTable extends JPanel {
	private static final long serialVersionUID = 1L;
	private TM tm = new TM();
	private TR tr = new TR();

	private JTable table = new JTable();
	private Pane pane;

	public FileTable(Pane parent) {
		pane = parent;
		setLayout(new BorderLayout());
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (pane.loading) {
					return;
				}
				// get source of click
				JTable source = (JTable) e.getSource();
				final int sourceRow = source.rowAtPoint(e.getPoint());
				if (sourceRow == -1) {
					source.clearSelection();
					return;
				}
				// select row or go down
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}
				if (e.getClickCount() == 2) {
					pane.down(tm.files.get(sourceRow).getIcon().getDescription());
				}
			}
		});
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, tr);
		table.setModel(tm);

		JScrollPane jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);

	}

	public void setFiles(ArrayList<FileListlet> list) {
		ArrayList<FileItem> items = new ArrayList<FileItem>();

		try {
			for (FileListlet fl : list) {
				FileItem fi = new FileItem();
				URL url = null;
				if (fl.getDir()) {
					url = getClass().getResource(
							"/com/subterranean_security/crimson/viewer/ui/res/image/icons16/files/file_extension_folder.png");

				} else {
					String[] ext = fl.getName().split("\\.");
					url = getClass().getResource(
							"/com/subterranean_security/crimson/viewer/ui/res/image/icons16/files/file_extension_"
									+ ext[ext.length - 1] + ".png");

					if (url == null) {
						url = getClass().getResource(
								"/com/subterranean_security/crimson/viewer/ui/res/image/icons16/files/file_extension_default.png");
					}
				}
				ImageIcon ico = new ImageIcon(ImageIO.read(url));
				ico.setDescription(fl.getName());
				fi.setIcon(ico);
				items.add(fi);

				CUtil.Misc.familiarize(fl.getSize(), CUtil.Misc.BYTES);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tm.setFiles(items);
	}

}

class TM extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] headers = new String[] { "Name", "Size" };
	public ArrayList<FileItem> files = new ArrayList<FileItem>();

	public void setFiles(ArrayList<FileItem> list) {
		files = list;
		this.fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (headers[columnIndex]) {
		case "Name": {
			return files.get(rowIndex).getIcon();

		}
		case "Size": {
			return files.get(rowIndex).getSize();
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

class FileItem {
	private ImageIcon icon;
	private String size;
	private String mtime;

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMtime() {
		return mtime;
	}

	public void setMtime(String mtime) {
		this.mtime = mtime;
	}

}
