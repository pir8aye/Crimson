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

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.ListDataListener;

import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class HostBoxModel extends AbstractListModel<Profile> implements ComboBoxModel<Profile> {

	private static final long serialVersionUID = 1L;

	ImageIcon viewer = UIUtil.getIcon("icons16/general/viewer.png");
	ImageIcon server = UIUtil.getIcon("icons16/general/server.png");

	private List<Profile> profiles;

	private Object selected = null;

	public HostBoxModel() {
		profiles = new ArrayList<Profile>();
		profiles.addAll(ViewerProfileStore.getClients());
	}

	@Override
	public void addListDataListener(ListDataListener arg0) {
		super.addListDataListener(arg0);
	}

	@Override
	public Profile getElementAt(int arg0) {
		return profiles.get(arg0);
	}

	@Override
	public int getSize() {
		return profiles.size();
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		super.removeListDataListener(arg0);
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
