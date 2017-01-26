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
package com.subterranean_security.crimson.viewer.ui.screen.settings;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import com.subterranean_security.crimson.core.profile.AbstractAttribute;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.storage.StorageFacility;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.screen.main.HostList;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class ListHeaderPopup extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private static final Font boxFont = new Font("Dialog", Font.BOLD, 10);

	private AbstractAttribute[] headers;

	private StorageFacility db;

	public ListHeaderPopup(StorageFacility db) {
		this.db = db;
		try {
			headers = (AbstractAttribute[]) db.getObject("hostlist.headers");
		} catch (Exception e) {
			headers = HostList.defaultHeaders;
		}
		init();
	}

	private boolean checkHeader(AbstractAttribute aa) {
		for (AbstractAttribute ab : headers) {
			if (ab == aa) {
				return true;
			}
		}
		return false;
	}

	private void addHeader(AbstractAttribute aa) {
		AbstractAttribute[] h = new AbstractAttribute[headers.length + 1];

		for (int i = 0; i < headers.length; i++) {
			h[i] = headers[i];
		}
		h[h.length - 1] = aa;

		headers = h;
		refreshHeaders();
	}

	private void removeHeader(AbstractAttribute aa) {
		ArrayList<AbstractAttribute> r = new ArrayList<AbstractAttribute>();
		for (AbstractAttribute ab : headers) {
			if (ab != aa) {
				r.add(ab);
			}
		}
		AbstractAttribute[] h = new AbstractAttribute[r.size()];
		for (int i = 0; i < h.length; i++) {
			h[i] = r.get(i);
		}

		headers = h;
		refreshHeaders();
	}

	private void refreshHeaders() {
		db.store("hostlist.headers", headers);

		// refresh list headers
		MainFrame.main.panel.list.refreshHeaders();
	}

	private void init() {

		// Client category
		JMenu client = new JMenu("Client");
		client.setIcon(UIUtil.getIcon("icons16/general/users_3.png"));
		add(client);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("CLIENT")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				client.add(jcb);
			}
		}

		// User category
		JMenu user = new JMenu("User");
		user.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		add(user);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("USER")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				user.add(jcb);
			}
		}

		// Java category
		JMenu java = new JMenu("Java");
		java.setIcon(UIUtil.getIcon("icons16/general/java.png"));
		add(java);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("JAVA")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				java.add(jcb);
			}
		}

		// RAM category
		JMenu ram = new JMenu("RAM");
		ram.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		add(ram);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("RAM")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				ram.add(jcb);
			}
		}

		// Network category
		JMenu network = new JMenu("Network");
		network.setIcon(UIUtil.getIcon("icons16/general/network_ethernet.png"));
		add(network);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("NET")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				network.add(jcb);
			}
		}

		// Network category
		JMenu iploc = new JMenu("IP Location");
		iploc.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		add(iploc);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("IPLOC")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				iploc.add(jcb);
			}
		}

		// OS category
		JMenu os = new JMenu("Operating System");
		os.setIcon(UIUtil.getIcon("icons16/general/users_3.png"));
		add(os);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("OS")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				os.add(jcb);
			}
		}

		// Linux category
		JMenu linux = new JMenu("Linux");
		linux.setIcon(UIUtil.getIcon("icons16/platform/linux.png"));
		add(linux);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("LINUX")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				linux.add(jcb);
			}
		}

		// Windows category
		JMenu win = new JMenu("Windows");
		win.setIcon(UIUtil.getIcon("icons16/platform/windows_7.png"));
		add(win);

		for (SimpleAttribute sa : SimpleAttribute.values()) {
			if (sa.toSuperString().startsWith("WIN")) {
				JCheckBox jcb = new JCheckBox(sa.toString());
				jcb.addActionListener((ActionEvent e) -> {
					if (jcb.isSelected())
						addHeader(sa);
					else
						removeHeader(sa);
				});
				jcb.setSelected(checkHeader(sa));
				jcb.setFont(boxFont);
				win.add(jcb);
			}
		}
	}

}
