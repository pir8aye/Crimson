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

import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class FileTable extends JPanel {
	private static final long serialVersionUID = 1L;
	public TM tm = new TM();
	public TR tr = new TR(tm);

	private JTable table = new JTable();
	public Pane pane;

	public FileTable(Pane parent) {
		pane = parent;
		initContextMenu();
		init();
		initRowSorter();
	}

	public ArrayList<FileItem> selection = null;

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

				pane.btnProperties.setEnabled(sourceRow != -1);
				pane.btnDelete.setEnabled(sourceRow != -1);
				if (sourceRow == -1) {
					source.clearSelection();
					return;
				}
				// select row or go down
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}

				selection = new ArrayList<FileItem>();
				for (int i : source.getSelectedRows()) {
					selection.add(tm.getFile(i));
				}

				if (e.getButton() == MouseEvent.BUTTON3) {

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
		menu_delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.delete(selection);
			}
		});

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
				// TODO multi selection
				pane.info(selection.get(0).getIcon().getDescription());
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

	public void setFiles(ArrayList<FileItem> list) {
		tm.setFiles(list);
	}

	private void initRowSorter() {
		TableRowSorter<TM> sorter = new TableRowSorter<TM>(tm);
		sorter.toggleSortOrder(0);

		// TODO hardcoded headers
		sorter.setComparator(0, new FileItem.NameComparator());
		sorter.setComparator(1, new FileItem.SizeComparator());

		table.setRowSorter(sorter);
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
		return files.get(rowIndex);
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

		super.getTableCellRendererComponent(table, setupCell(table, (FileItem) value, column), isSelected, hasFocus,
				row, column);
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
	private Object setupCell(JTable table, FileItem file, int viewColumn) {
		setBorder(noFocusBorder);

		switch (tm.headers[table.convertColumnIndexToModel(viewColumn)]) {
		case "Name": {
			setHorizontalAlignment(SwingConstants.LEFT);
			return file.getIcon();
		}
		case "Size": {
			setHorizontalAlignment(viewColumn == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
			return file.getSize();
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

class FileItem {
	private ImageIcon icon;

	private long size;
	private String sizeStr;

	private long mtime;
	private String mtimeStr;

	private boolean folder;

	public FileItem(String name, boolean dir, long size, long mtime) {
		this.mtime = mtime;
		this.size = size;
		this.folder = dir;

		if (dir) {
			this.sizeStr = size + ((size == 1) ? " item " : " items");
			icon = UIUtil.getIcon("icons16/files/file_extension_folder.png");
		} else {
			this.sizeStr = UnitTranslator.familiarize(size, UnitTranslator.BYTES);

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

	public String getName() {
		return icon.getDescription();
	}

	public String getSize() {
		return sizeStr;
	}

	public String getMtime() {
		return mtimeStr;
	}

	public boolean isFolder() {
		return folder;
	}

	static class NameComparator implements Comparator<FileItem> {
		@Override
		public int compare(FileItem o1, FileItem o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1.folder && !o2.folder) {
				return -1;
			} else if (!o1.folder && o2.folder) {
				return 1;
			}

			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}
	}

	static class MTimeComparator implements Comparator<FileItem> {
		@Override
		public int compare(FileItem o1, FileItem o2) {
			return (int) (o1.mtime - o2.mtime);
		}
	}

	static class SizeComparator implements Comparator<FileItem> {
		@Override
		public int compare(FileItem o1, FileItem o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1.folder && !o2.folder) {
				return -1;
			} else if (!o1.folder && o2.folder) {
				return 1;
			}

			return (int) (o1.size - o2.size);
		}
	}

}
