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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.ClientAuth.Group;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.ServerProfile;
import com.subterranean_security.crimson.sv.ViewerProfile;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;

public enum Defaults {
	;

	public static void softReset(ServerDB db) {
		softResetUniversal(db);

		db.storeObject("groups", new ArrayList<Group>());
		db.storeObject("passwords", new ArrayList<String>());
		db.storeObject("profiles.clients", new MemMap<Integer, ClientProfile>());
		db.storeObject("profiles.viewers", new MemMap<Integer, ViewerProfile>());
		db.storeObject("profiles.idcount", 0);
	}

	public static void hardReset(ServerDB db) {
		softReset(db);
		hardResetUniversal(db);

	}

	public static void softReset(ClientDB db) {
		softResetUniversal(db);

		db.storeObject("login-times", new ArrayList<Long>());
		db.storeObject("login-ips", new ArrayList<String>());
	}

	public static void hardReset(ClientDB db) {
		softReset(db);
		hardResetUniversal(db);

		db.storeObject("MAGIC", "subterranean");
	}

	public static void softReset(LViewerDB db) {
		softResetUniversal(db);

		db.storeObject("close_on_tray", false);
		db.storeObject("show_eula", true);
		db.storeObject("show_helps", true);
		db.storeObject("show_detail", true);
		db.storeObject("hostlist.headers",
				new Headers[] { Headers.COUNTRY, Headers.CVID, Headers.USERNAME, Headers.HOSTNAME, Headers.LANGUAGE });
		db.storeObject("login.recents", new ArrayList<String>());
		db.storeObject("view.last", "list");
	}

	public static void hardReset(LViewerDB db) {
		softReset(db);
		hardResetUniversal(db);
		db.storeObject("viewer.profile", new ClientProfile());
		db.storeObject("server.profile", new ServerProfile());
	}

	private static void softResetUniversal(Database db) {
		db.storeObject("error_reporting", true);
		db.storeObject("reports_sent", 0);
		db.storeObject("language", "en");
	}

	private static void hardResetUniversal(Database db) {
		db.storeObject("cvid", 0);
		db.storeObject("report_buffer", new ArrayList<Report>());
		db.storeObject("crimson.version", Common.version);
	}
}
