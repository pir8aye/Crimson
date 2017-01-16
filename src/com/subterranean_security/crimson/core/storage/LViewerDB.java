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
package com.subterranean_security.crimson.core.storage;

import java.io.File;
import java.util.ArrayList;

import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.JarUtil;

public class LViewerDB extends Database {
	public LViewerDB(File dfile) throws Exception {

		if (!dfile.exists()) {
			// copy the template
			JarUtil.extract("com/subterranean_security/crimson/core/storage/lviewer-template.db",
					dfile.getAbsolutePath());
		}
		init(dfile);
		if (isFirstRun()) {
			this.hardReset();
		}

	}

	@Override
	public void softReset() {

		this.storeObject("close_on_tray", false);
		this.storeObject("show_eula", true);
		this.storeObject("show_helps", true);
		this.storeObject("show_detail", true);
		this.storeObject("detail.processor", true);
		this.storeObject("detail.nic", true);
		this.storeObject("detail.preview", false);
		this.storeObject("detail.map", false);
		this.storeObject("view.last", "list");
		this.storeObject("login.recents", new ArrayList<String>());
		this.storeObject("profiles.clients", new MemList<ClientProfile>());
		this.storeObject("keylog.treeview", false);
		super.softReset();
	}

	@Override
	public void hardReset() {
		this.softReset();
		super.hardReset();
	}
}
