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

import com.subterranean_security.crimson.core.util.CUtil;

public class ClientDB extends Database {

	public String master;

	public ClientDB(File dfile) throws Exception {

		if (!dfile.exists()) {
			// copy the template
			CUtil.Files.extract("com/subterranean_security/crimson/core/storage/viewer-template.db",
					dfile.getAbsolutePath());
		}
		init(dfile);
		if (isFirstRun()) {
			this.hardReset();
		}

	}

	@Override
	public void softReset() {

		this.storeObject("login-times", new ArrayList<Long>());
		this.storeObject("login-ips", new ArrayList<String>());
		super.softReset();
	}

	@Override
	public void hardReset() {
		this.softReset();
		super.hardReset();
	}

}
