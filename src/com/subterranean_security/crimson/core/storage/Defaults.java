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

import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.proto.msg.Reports.Report;

public enum Defaults {
	;

	public static class User {
		private static final String[] list_headers = new String[] { "Username", "Hostname" };
		private static final String language = "en";
		private static final boolean error_reporting = true;
		private static final boolean close_on_tray = false;
		private static final boolean show_eula = true;
		private static final boolean show_detail = true;

		public static void set_server(Database db, boolean empty) {
			ArrayList<String> headers = new ArrayList<String>();
			for (String s : list_headers) {
				headers.add(s);
			}
			db.storeObject("MAGIC", "subterranean");
			db.storeObject("list_headers", headers);
			db.storeObject("language", language);
			db.storeObject("error_reporting", error_reporting);
			db.storeObject("reports_sent", 0);
			db.storeObject("report_buffer", new ArrayList<Report>());
			db.storeObject("close_on_tray", close_on_tray);
			db.storeObject("show_eula", show_eula);
			db.storeObject("show_detail", show_detail);

		}

		public static void set_client(Database db, boolean empty) {

			db.storeObject("error_reporting", error_reporting);
			db.storeObject("reports_sent", 0);
			db.storeObject("report_buffer", new ArrayList<Report>());

		}
	}

	public static class System {

		public static void set(Database db, boolean empty) {
			if (empty) {
				Logger.debug("Setting first run system defaults");
				db.storeObject("runs", 0);
			}

		}

	}
}
