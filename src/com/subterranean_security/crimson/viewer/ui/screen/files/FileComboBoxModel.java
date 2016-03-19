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

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.ListDataListener;

import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class FileComboBoxModel extends AbstractListModel implements ComboBoxModel {

	ImageIcon viewer = UUtil.getIcon("icons16/general/viewer.png");

	ImageIcon server = UUtil.getIcon("icons16/general/server.png");

	ImageIcon client = UUtil.getIcon("icons16/general/clients.png");

	private Object selected = null;

	public FileComboBoxModel() {
		viewer.setDescription("Viewer");
		server.setDescription("Server");
	}

	@Override
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getElementAt(int arg0) {
		if (arg0 == 0) {
			return viewer;
		}
		if (arg0 == 1) {
			return server;
		}

		client.setDescription(ViewerStore.Profiles.profiles.get(arg0 - 2).getHostname());
		return client;

	}

	@Override
	public int getSize() {

		return 2 + ViewerStore.Profiles.profiles.size();
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object arg0) {
		selected = arg0;

	}

}
