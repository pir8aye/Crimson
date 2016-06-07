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

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.storage.Headers;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.dpanel.DPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;

public class HostList extends JPanel {

	private static final long serialVersionUID = 1L;

	private static JTable table = new JTable();
	private TM tm = new TM();
	private TR tr = new TR();

	private JPopupMenu popup;
	private JMenuItem control;
	private JMenuItem graph;

	private JMenuItem poweroff;
	private JMenuItem restart;
	private JMenuItem refresh;

	private JMenuItem uninstall;

	public HostList() {
		setLayout(new BorderLayout());
		initContextMenu();
		table.setModel(tm);
		table.setDefaultRenderer(Object.class, tr);
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (DPanel.moving) {
					return;
				}

				// get source of click
				JTable source = (JTable) e.getSource();
				final int sourceRow = source.rowAtPoint(e.getPoint());
				if (sourceRow == -1) {
					source.clearSelection();
					MainFrame.main.dp.closeDetail();
					return;
				}

				final ClientProfile sp = ViewerStore.Profiles.clients.get(sourceRow);

				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					initContextActions(sp);
					popup.show(table, e.getX(), e.getY());

				} else {
					// open up the detail
					MainFrame.main.dp.showDetail(sp);
				}

				// select row
				if (!source.isRowSelected(sourceRow)) {
					source.changeSelection(sourceRow, 0, false, false);
				}
			}
		});

		JScrollPane jsp = new JScrollPane(table);

		add(jsp, BorderLayout.CENTER);
	}

	private void initContextActions(ClientProfile cp) {
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						ClientCPFrame ccpf = new ClientCPFrame(cp);
						ccpf.setLocationRelativeTo(null);
						ccpf.setVisible(true);
					}
				}.start();

			}

		});
		poweroff.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, cp.getCvid(), StateType.SHUTDOWN)) {
							// TODO
						}
					}
				}.start();

			}

		});
		restart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {
						StringBuffer error = new StringBuffer();
						if (!ViewerCommands.changeClientState(error, cp.getCvid(), StateType.RESTART)) {
							// TODO
						}
					}
				}.start();

			}

		});
		refresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				new Thread() {
					public void run() {

					}
				}.start();

			}

		});
	}

	private void initContextMenu() {
		popup = new JPopupMenu();
		control = new JMenuItem("Control Panel");
		control.setIcon(UIUtil.getIcon("icons16/general/cog.png"));

		popup.add(control);

		graph = new JMenuItem("Find in Graph");
		graph.setIcon(UIUtil.getIcon("icons16/general/diagramm.png"));
		popup.add(graph);

		JMenu quick = new JMenu("Quick Commands");
		quick.setIcon(UIUtil.getIcon("icons16/general/bow.png"));
		popup.add(quick);

		JMenu state = new JMenu("Change State");
		state.setIcon(UIUtil.getIcon("icons16/general/power_surge.png"));
		quick.add(state);

		poweroff = new JMenuItem("Shutdown");
		poweroff.setIcon(UIUtil.getIcon("icons16/general/lcd_tv_off.png"));
		state.add(poweroff);

		restart = new JMenuItem("Restart");
		restart.setIcon(UIUtil.getIcon("icons16/general/arrow_redo.png"));
		state.add(restart);

		uninstall = new JMenuItem("Uninstall Crimson");
		uninstall.setIcon(UIUtil.getIcon("icons16/general/radioactivity.png"));
		state.add(uninstall);

		refresh = new JMenuItem("Refresh");
		refresh.setIcon(UIUtil.getIcon("icons16/general/inbox_download.png"));

		quick.add(refresh);
	}

	public void updateRow(int r) {
		// TODO update individual cell
		tm.fireTableRowsUpdated(r, r);
	}

	public void insertRow(int r) {
		tm.fireTableRowsInserted(r, r);
	}

	public void refreshHeaders() {
		tm.refreshHeaders();
	}

}

class TM extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	Headers[] headers = new Headers[] {};

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
		return ViewerStore.Profiles.clients.size();
	}

	@Override
	public String getColumnName(int column) {
		return headers[column].toString();
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (headers[columnIndex]) {
		case USERNAME:
			return ViewerStore.Profiles.clients.get(rowIndex).getUsername();
		case USER_STATUS:
			return ViewerStore.Profiles.clients.get(rowIndex).getUserStatus();
		case HOSTNAME:
			return ViewerStore.Profiles.clients.get(rowIndex).getHostname();
		case INTERNAL_IP:
			return null;
		case EXTERNAL_IP:
			return ViewerStore.Profiles.clients.get(rowIndex).getExtIp();
		case LANGUAGE:
			return ViewerStore.Profiles.clients.get(rowIndex).getLanguage();
		case ACTIVE_WINDOW:
			return ViewerStore.Profiles.clients.get(rowIndex).getActiveWindow();
		case COUNTRY:
			return ViewerStore.Profiles.clients.get(rowIndex).getLocationIcon();
		case CPU_MODEL:
			return ViewerStore.Profiles.clients.get(rowIndex).getCpuModel();
		case CPU_USAGE:
			return null;
		case CPU_TEMP:
			return ViewerStore.Profiles.clients.get(rowIndex).getCpuTemp();
		case CVID:
			return ViewerStore.Profiles.clients.get(rowIndex).getCvid();
		case RAM_CAPACITY:
			return ViewerStore.Profiles.clients.get(rowIndex).getSystemRamCapacity();
		case RAM_USAGE:
			return ViewerStore.Profiles.clients.get(rowIndex).getSystemRamUsage();
		case CRIMSON_VERSION:
			return ViewerStore.Profiles.clients.get(rowIndex).getCrimsonVersion();
		case OS_FAMILY:
			return ViewerStore.Profiles.clients.get(rowIndex).getOsFamily();
		case OS_ARCH:
			return ViewerStore.Profiles.clients.get(rowIndex).getOsArch();
		case JAVA_VERSION:
			return ViewerStore.Profiles.clients.get(rowIndex).getJavaVersion();
		case MONITOR_COUNT:
			return null;
		case VIRTUALIZATION:
			return ViewerStore.Profiles.clients.get(rowIndex).getVirtualization();
		case TIMEZONE:
			return ViewerStore.Profiles.clients.get(rowIndex).getTimezone();
		case CPU_SPEED:
			return null;
		case MESSAGE_PING:
			return ViewerStore.Profiles.clients.get(rowIndex).getMessageLatency();
		case SCREEN_PREVIEW:
			return null;

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
			super.setValue(value);
		}
	}

}
