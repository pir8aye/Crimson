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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class FileTable extends JPanel {
	private static final long serialVersionUID = 1L;
	public TM tm = new TM();
	public TR tr = new TR(tm);
	public TableRowSorter<TM> rs = new TableRowSorter<TM>(tm);

	private JTable table = new JTable();
	public Pane pane;

	public FileTable(Pane parent) {
		pane = parent;
		initContextMenu();
		init();

	}

	private ArrayList<FileItem> selection = null;

	public void init() {

		setLayout(new BorderLayout());
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (pane.loading) {
					return;
				}
				// switch path to view if needed
				pane.pwd.openView();

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

				if (e.getButton() == MouseEvent.BUTTON3) {
					selection = new ArrayList<FileItem>();
					for (int i : source.getSelectedRows()) {
						selection.add(tm.getFile(i));
					}
					initContextActions();

					popup.show(table, e.getPoint().x, e.getPoint().y);
				} else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (tm.getFile(sourceRow).isFolder()) {
						pane.down(tm.getFile(sourceRow).getIcon().getDescription());
					}

					return;
				}
			}
		});
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, tr);
		table.setModel(tm);
		table.setRowSorter(rs);

		rs.setComparator(1, new Comparator<String>() {

			@Override
			public int compare(String i1, String i2) {
				if (i1.contains("item")) {
					i1 = "" + (Long.parseLong(i1.substring(0, i1.indexOf(' '))) - 10000000) + " items";
				}
				if (i2.contains("item")) {
					i2 = "" + (Long.parseLong(i2.substring(0, i2.indexOf(' '))) - 10000000) + " items";
				}
				return Long.compare(CUtil.Misc.defamiliarize(i1, CUtil.Misc.BYTES),
						CUtil.Misc.defamiliarize(i2, CUtil.Misc.BYTES));

			}
		});

		JScrollPane jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);

	}

	JPopupMenu popup;

	JMenuItem menu_copy;
	JMenuItem menu_cut;
	JMenuItem menu_delete;
	JMenuItem menu_rename;
	JMenuItem menu_compress;
	JMenuItem menu_decompress;
	JMenuItem menu_properties;

	private void initContextMenu() {
		popup = new JPopupMenu();

		menu_copy = new JMenuItem("Copy");
		menu_copy.setIcon(UIUtil.getIcon("icons16/general/page_copy.png"));

		menu_cut = new JMenuItem("Cut");
		menu_cut.setIcon(UIUtil.getIcon("icons16/general/cut.png"));

		menu_delete = new JMenuItem("Delete");
		menu_delete.setIcon(UIUtil.getIcon("icons16/general/folder_delete.png"));

		menu_rename = new JMenuItem("Rename");
		menu_rename.setIcon(UIUtil.getIcon("icons16/general/textfield_rename.png"));

		menu_compress = new JMenuItem("Compress");
		menu_compress.setIcon(UIUtil.getIcon("icons16/general/compress.png"));

		menu_decompress = new JMenuItem("Decompress");
		menu_decompress.setIcon(UIUtil.getIcon("icons16/general/server_uncompress.png"));

		menu_properties = new JMenuItem("Properties");
		menu_properties.setIcon(UIUtil.getIcon("icons16/general/attributes_display.png"));
		menu_properties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {
					public void run() {
						pane.info(selection.get(0).getIcon().getDescription());

					}
				}).start();

			}

		});

	}

	private void initContextActions() {

		popup.removeAll();

		if (selection.size() == 0) {
			return;
		} else if (selection.size() == 1) {
			popup.add(menu_rename);
			popup.add(menu_properties);

		} else {

		}

		popup.add(menu_copy);
		popup.add(menu_cut);
		popup.add(menu_delete);
	}

	public void setFiles(List<FileListlet> list) {
		ArrayList<FileItem> items = new ArrayList<FileItem>();

		for (FileListlet fl : list) {
			items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
		}

		tm.setFiles(items);
	}

}

class TM extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	public String[] headers = new String[] { "Name", "Size" };
	private ArrayList<FileItem> files = new ArrayList<FileItem>();

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

	public FileItem getFile(int row) {
		return files.get(row);
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
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(noFocusBorder);
		setHorizontalAlignment(tm.headers[column].equals("Size") ? SwingConstants.RIGHT : SwingConstants.LEFT);

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

	private boolean folder;

	public FileItem(String name, boolean dir, long size, long mtime) {
		folder = dir;

		if (dir) {
			this.size = size + ((size == 1) ? " item " : " items");
			icon = UIUtil.getIcon("icons16/files/file_extension_folder.png");
		} else {
			this.size = CUtil.Misc.familiarize(size, CUtil.Misc.BYTES);

			icon = UIUtil.getIcon("icons16/files/file_extension_" + name.substring(name.lastIndexOf('.') + 1) + ".png");
			if (icon == null) {
				icon = UIUtil.getIcon("icons16/files/file_extension_default.png");
			}
		}
		icon.setDescription(name);
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public String getSize() {
		return size;
	}

	public String getMtime() {
		return mtime;
	}

	public boolean isFolder() {
		return folder;
	}

}
