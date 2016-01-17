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

import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.utility.CUtil;

public class ViewerDB extends Database {

	public String master;

	public ViewerDB(File dfile) throws Exception {

		if (!dfile.exists()) {
			// copy the template
			Logger.debug("Copying database template to: " + dfile.getAbsolutePath());
			CUtil.Files.extract("com/subterranean_security/crimson/core/storage/viewer-template.db",
					dfile.getAbsolutePath());
		}
		init(dfile);
		if (isFirstRun()) {
			Defaults.hardReset(this);
		}

	}

}
