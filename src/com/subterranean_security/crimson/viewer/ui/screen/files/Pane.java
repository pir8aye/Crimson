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
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.MatteBorder;

import com.subterranean_security.crimson.core.net.RequestTimeoutException;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.net.command.FileManagerCom;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.screen.files.ep.AdvancedFileInfo;
import com.subterranean_security.crimson.viewer.ui.screen.files.ep.DeleteConfirmation;

public class Pane extends JPanel {

	private static final long serialVersionUID = 1L;
	public FMPanel parent;

	public enum TYPE {
		SERVER, VIEWER, CLIENT;
	}

	public FileTable ft = new FileTable(this);

	protected TYPE type = TYPE.VIEWER;

	// for viewers
	private LocalFS lf = new LocalFS(true, true);

	private int cid;
	private int fmid;

	public boolean loading = false;
	public PathPanel pwd = new PathPanel();
	private JComboBox typeBox;
	public JButton btnUp;
	public JButton btnProperties;
	public JButton btnDelete;

	public Pane(FMPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		add(ft, BorderLayout.CENTER);

		typeBox = new JComboBox();
		typeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageIcon selected = (ImageIcon) typeBox.getSelectedItem();
				String name = selected.getDescription().toLowerCase();// npe
				new Thread(new Runnable() {
					public void run() {

						if (fmid != 0) {
							FileManagerCom.closeFileHandle(cid, fmid);
						}

						switch (name) {
						case "viewer": {
							type = TYPE.VIEWER;
							break;
						}
						case "server": {
							type = TYPE.SERVER;
							cid = 0;
							fmid = FileManagerCom.getFileHandle(cid);
							break;
						}
						default: {
							type = TYPE.CLIENT;
							cid = ProfileStore.getClient(name).getCid();
							System.out.println("Found cid: " + cid);
							fmid = FileManagerCom.getFileHandle(cid);
							break;
						}
						}
						refresh();
					}
				}).start();

			}
		});
		typeBox.setRenderer(new ComboBoxRenderer());
		typeBox.setModel(new FileComboBoxModel());
		typeBox.setSelectedIndex(0);
		add(typeBox, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		pwd.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panel.add(pwd, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar, BorderLayout.NORTH);

		btnUp = new JButton("");
		btnUp.setFocusable(false);
		btnUp.setRequestFocusEnabled(false);
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pwd.openView();
				up();
			}
		});
		btnUp.setMargin(new Insets(0, 0, 0, 0));
		btnUp.setIcon(UIUtil.getIcon("icons16/general/folder_up.png"));
		menuBar.add(btnUp);

		btnDelete = new JButton("");
		btnDelete.setEnabled(false);
		btnDelete.setFocusable(false);
		btnDelete.setRequestFocusEnabled(false);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete(ft.selection);
			}
		});
		btnDelete.setMargin(new Insets(0, 0, 0, 0));
		btnDelete.setIcon(UIUtil.getIcon("icons16/general/folder_delete.png"));
		menuBar.add(btnDelete);

		btnProperties = new JButton("");
		btnProperties.setEnabled(false);
		btnProperties.setFocusable(false);
		btnProperties.setRequestFocusEnabled(false);
		btnProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO multi selection
				info(ft.selection.get(0).getIcon().getDescription());
			}
		});
		btnProperties.setMargin(new Insets(0, 0, 0, 0));
		btnProperties.setIcon(UIUtil.getIcon("icons16/general/attributes_display.png"));
		menuBar.add(btnProperties);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

	}

	public class UpWorker extends SwingWorker<ArrayList<FileItem>, Void> {

		private String pwd;

		@Override
		protected ArrayList<FileItem> doInBackground() throws Exception {

			ArrayList<FileItem> items = new ArrayList<FileItem>();
			switch (type) {
			case CLIENT:
			case SERVER:
				RS_FileListing rs = FileManagerCom.fm_up(cid, fmid, true, true);
				if (rs == null) {
					throw new RequestTimeoutException();
				} else {
					pwd = rs.getPath();
					for (FileListlet fl : rs.getListingList()) {
						items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
					}
				}
				break;
			case VIEWER:
				lf.up();
				pwd = lf.pwd();
				for (FileListlet fl : lf.list()) {
					items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
				}
				break;

			}

			return items;
		}

		@Override
		protected void done() {
			try {
				ft.setFiles(get());
				ft.pane.pwd.setPwd(pwd);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// parent.console.addLine("Request timed out", LineType.ORANGE);
			}

			stopLoading();
		}
	}

	public void up() {
		beginLoading();
		new UpWorker().execute();
	}

	public class DownWorker extends SwingWorker<ArrayList<FileItem>, Void> {

		private String down;
		private String pwd;

		public DownWorker(String s) {
			down = s;
		}

		@Override
		protected ArrayList<FileItem> doInBackground() throws Exception {

			ArrayList<FileItem> items = new ArrayList<FileItem>();
			switch (type) {
			case CLIENT:
			case SERVER:
				RS_FileListing rs = FileManagerCom.fm_down(cid, fmid, down, true, true);
				if (rs == null) {
					throw new RequestTimeoutException();
				} else {
					pwd = rs.getPath();
					for (FileListlet fl : rs.getListingList()) {
						items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
					}
				}
				break;
			case VIEWER:
				lf.down(down);
				pwd = lf.pwd();
				for (FileListlet fl : lf.list()) {
					items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
				}
				break;

			}

			return items;
		}

		@Override
		protected void done() {
			try {
				ft.setFiles(get());
				ft.pane.pwd.setPwd(pwd);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// parent.console.addLine("Request timed out", LineType.ORANGE);
			}

			stopLoading();
		}
	}

	public void down(String s) {
		beginLoading();
		new DownWorker(s).execute();
	}

	public class InfoWorker extends SwingWorker<RS_AdvancedFileInfo, Void> {

		private String name;

		public InfoWorker(String n) {
			name = n;
		}

		@Override
		protected RS_AdvancedFileInfo doInBackground() throws Exception {
			RS_AdvancedFileInfo rs = null;
			String path = pwd.getPwd() + "/" + name;
			switch (type) {
			case CLIENT:
			case SERVER:
				rs = FileManagerCom.fm_file_info(cid, path);
				break;
			case VIEWER:
				rs = LocalFS.getInfo(path);
				break;

			}
			return rs;
		}

		protected void done() {
			try {
				parent.ep.raise(new AdvancedFileInfo(get(), parent.ep), 80);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

	}

	public void info(String name) {
		new InfoWorker(name).execute();
	}

	public void delete(ArrayList<FileItem> f) {
		ArrayList<String> targets = new ArrayList<String>();
		for (FileItem fi : f) {
			targets.add(pwd.getPwd() + "/" + fi.getIcon().getDescription());
		}
		parent.ep.raise(new DeleteConfirmation(this, cid, targets, type), 100);

	}

	public class RefreshWorker extends SwingWorker<ArrayList<FileItem>, Void> {

		private String pwd;

		@Override
		protected ArrayList<FileItem> doInBackground() throws Exception {

			ArrayList<FileItem> items = new ArrayList<FileItem>();
			switch (type) {
			case CLIENT:
			case SERVER:
				RS_FileListing rs = FileManagerCom.fm_list(cid, fmid, true, true);
				if (rs == null) {
					throw new RequestTimeoutException();
				} else {
					pwd = rs.getPath();
					for (FileListlet fl : rs.getListingList()) {
						items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
					}
				}
				break;
			case VIEWER:
				pwd = lf.pwd();
				for (FileListlet fl : lf.list()) {
					items.add(new FileItem(fl.getName(), fl.getDir(), fl.getSize(), fl.getMtime()));
				}
				break;

			}

			return items;
		}

		@Override
		protected void done() {
			try {
				ft.setFiles(get());
				ft.pane.pwd.setPwd(pwd);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// parent.console.addLine("Request timed out", LineType.ORANGE);
			}

			stopLoading();
		}
	}

	public void refresh() {
		beginLoading();
		new RefreshWorker().execute();
	}

	public void beginLoading() {
		loading = true;
		pwd.beginLoading();
		ft.setEnabled(false);
		typeBox.setEnabled(false);
	}

	public void stopLoading() {
		loading = false;
		pwd.stopLoading();
		ft.setEnabled(true);
		typeBox.setEnabled(true);
	}

}
