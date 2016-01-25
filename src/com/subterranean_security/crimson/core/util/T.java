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
package com.subterranean_security.crimson.core.utility;

import java.util.Locale;
import java.util.ResourceBundle;

public class T {

	private static ResourceBundle core; // 0 to 10000
	private static ResourceBundle server; // 10001 to 15000
	private static ResourceBundle client; // 15001 to 20000
	private static ResourceBundle ui; // 20001 to INTEGER.MAX

	private static final String coredir = "com/subterranean_security/crimson/core/res/language/lang";
	private static final String serverdir = "com/subterranean_security/crimson/server/res/language/lang";
	private static final String clientdir = "com/subterranean_security/crimson/client/stage2/res/language/lang";
	private static final String uidir = "com/subterranean_security/crimson/ui/res/language/lang";

	private static String lang = "";

	public static String t(int i) {
		if (lang.isEmpty()) {
			loadTranslation("en");
		}
		try {
			if (i <= 10000) {
				return core.getString("" + i);
			} else if (i <= 15000) {
				return server.getString("" + i);
			} else if (i <= 20000) {
				return client.getString("" + i);
			} else {
				return ui.getString("" + i);
			}

		} catch (Throwable e) {

			return "trans error";
		}

	}

	public static void loadTranslation(String l) {

		lang = l.toLowerCase();
		switch (lang) {
		case "en": {
			core = ResourceBundle.getBundle(coredir, Locale.ENGLISH);
			server = ResourceBundle.getBundle(serverdir, Locale.ENGLISH);
			client = ResourceBundle.getBundle(clientdir, Locale.ENGLISH);
			ui = ResourceBundle.getBundle(uidir, Locale.ENGLISH);
			return;
		}

		}

	}

}
