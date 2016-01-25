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

import java.util.ArrayList;

import com.subterranean_security.crimson.core.proto.msg.Gen.Group;
import com.subterranean_security.crimson.core.proto.msg.Reports.Report;
import com.subterranean_security.crimson.sv.Profile;

public enum Defaults {
	;

	public static void softReset(ServerDB db) {
		softResetUniversal(db);

		db.storeObject("groups", new ArrayList<Group>());
		db.storeObject("clients", new MemMap<Integer, Profile>());
	}

	public static void hardReset(ServerDB db) {
		softReset(db);
		hardResetUniversal(db);

	}

	public static void softReset(ViewerDB db) {
		softResetUniversal(db);

		db.storeObject("login-times", new ArrayList<Long>());
		db.storeObject("login-ips", new ArrayList<String>());
	}

	public static void hardReset(ViewerDB db) {
		softReset(db);
		hardResetUniversal(db);

		db.storeObject("MAGIC", "subterranean");
	}

	public static void softReset(LViewerDB db) {
		softResetUniversal(db);

		db.storeObject("close_on_tray", false);
		db.storeObject("show_eula", true);
		db.storeObject("show_detail", true);
		db.storeObject("list_headers", new String[] { "Location", "Username", "Hostname", "Language", "Java Version" });
	}

	public static void hardReset(LViewerDB db) {
		softReset(db);
		hardResetUniversal(db);
	}

	private static void softResetUniversal(Database db) {
		db.storeObject("error_reporting", true);
		db.storeObject("reports_sent", 0);
		db.storeObject("language", "en");
	}

	private static void hardResetUniversal(Database db) {
		db.storeObject("report_buffer", new ArrayList<Report>());
	}
}
