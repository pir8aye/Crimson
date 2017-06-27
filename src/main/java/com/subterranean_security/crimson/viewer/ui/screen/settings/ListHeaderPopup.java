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
import java.util.NoSuchElementException;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_JVM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LIN;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_RAM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_USER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_WIN;
import com.subterranean_security.crimson.core.storage.BasicStorageFacility;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.screen.main.HostList;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

/**
 * A dynamic checkbox which can alter the headers in the host table.
 * 
 * @author cilki
 * @since 4.0.0
 */
public class ListHeaderPopup extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private static final Font boxFont = new Font("Dialog", Font.BOLD, 10);

	private AttributeKey[] headers;

	private BasicStorageFacility db;

	public ListHeaderPopup(BasicStorageFacility db) {
		this.db = db;
		try {
			headers = (AttributeKey[]) db.getObject("hostlist.headers");
		} catch (NoSuchElementException e) {
			headers = HostList.defaultHeaders;
		}
		init();
	}

	private boolean checkHeader(AttributeKey aa) {
		for (AttributeKey ab : headers) {
			if (ab == aa) {
				return true;
			}
		}
		return false;
	}

	private void addHeader(AttributeKey aa) {
		AttributeKey[] h = new AttributeKey[headers.length + 1];

		for (int i = 0; i < headers.length; i++) {
			h[i] = headers[i];
		}
		h[h.length - 1] = aa;

		headers = h;
		refreshHeaders();
	}

	private void removeHeader(AttributeKey aa) {
		ArrayList<AttributeKey> r = new ArrayList<AttributeKey>();
		for (AttributeKey ab : headers) {
			if (ab != aa) {
				r.add(ab);
			}
		}
		AttributeKey[] h = new AttributeKey[r.size()];
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

		for (AK_CLIENT sa : AK_CLIENT.values()) {
			client.add(fabricateCheckbox(sa));
		}

		// User category
		JMenu user = new JMenu("User");
		user.setIcon(UIUtil.getIcon("icons16/general/user.png"));
		add(user);

		for (AK_USER sa : AK_USER.values()) {
			user.add(fabricateCheckbox(sa));
		}

		// Java category
		JMenu java = new JMenu("Java");
		java.setIcon(UIUtil.getIcon("icons16/general/java.png"));
		add(java);

		for (AK_JVM sa : AK_JVM.values()) {
			java.add(fabricateCheckbox(sa));
		}

		// RAM category
		JMenu ram = new JMenu("RAM");
		ram.setIcon(UIUtil.getIcon("icons16/general/ram.png"));
		add(ram);

		for (AK_RAM sa : AK_RAM.values()) {
			ram.add(fabricateCheckbox(sa));
		}

		// Network category
		JMenu network = new JMenu("Network");
		network.setIcon(UIUtil.getIcon("icons16/general/network_ethernet.png"));
		add(network);

		for (AK_NET sa : AK_NET.values()) {
			network.add(fabricateCheckbox(sa));
		}

		// Network category
		JMenu iploc = new JMenu("IP Location");
		iploc.setIcon(UIUtil.getIcon("icons16/general/ip.png"));
		add(iploc);

		for (AK_LOC sa : AK_LOC.values()) {
			iploc.add(fabricateCheckbox(sa));
		}

		// OS category
		JMenu os = new JMenu("Operating System");
		os.setIcon(UIUtil.getIcon("icons16/general/users_3.png"));
		add(os);

		for (AK_OS sa : AK_OS.values()) {
			os.add(fabricateCheckbox(sa));
		}

		// Linux category
		JMenu linux = new JMenu("Linux");
		linux.setIcon(UIUtil.getIcon("icons16/platform/linux.png"));
		add(linux);

		for (AK_LIN sa : AK_LIN.values()) {
			linux.add(fabricateCheckbox(sa));
		}

		// Windows category
		JMenu win = new JMenu("Windows");
		win.setIcon(UIUtil.getIcon("icons16/platform/windows_7.png"));
		add(win);

		for (AK_WIN sa : AK_WIN.values()) {
			win.add(fabricateCheckbox(sa));
		}
	}

	private JCheckBox fabricateCheckbox(AttributeKey key) {
		JCheckBox jcb = new JCheckBox(key.toString());
		jcb.addActionListener((ActionEvent e) -> {
			if (jcb.isSelected())
				addHeader(key);
			else
				removeHeader(key);
		});
		jcb.setSelected(checkHeader(key));
		jcb.setFont(boxFont);

		return jcb;
	}

}
