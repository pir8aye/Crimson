/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.universal.stores;

import java.io.IOException;

import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.misc.PreferenceWrapper;

public final class PrefStore {
	private PrefStore() {
	}

	private static PreferenceWrapper preferences;

	public static PreferenceWrapper getPref() {
		return preferences;
	}

	public static void loadPreferences(Instance instance) {
		preferences = new PreferenceWrapper(instance);
	}

	public static void close() throws IOException {
		// close preferences
		if (preferences != null) {
			try {
				preferences.close();
			} finally {
				preferences = null;
			}
		}
	}

	public enum PTag {
		GENERAL_EULA_SHOW, GENERAL_HELP_SHOW, GENERAL_TRAY_MINIMIZE, VIEW_KEYLOG_FLAT, VIEW_MAIN_LAST, VIEW_DETAIL_NIC, VIEW_DETAIL_PROCESSOR, VIEW_DETAIL_PREVIEW, VIEW_DETAIL_MAP, LOGIN_RECENT;

		public boolean defaultBoolean() {
			switch (this) {
			case GENERAL_EULA_SHOW:
				return true;
			case GENERAL_HELP_SHOW:
				return true;
			case GENERAL_TRAY_MINIMIZE:
				return false;
			case VIEW_DETAIL_MAP:
				return false;
			case VIEW_DETAIL_NIC:
				return true;
			case VIEW_DETAIL_PREVIEW:
				return false;
			case VIEW_DETAIL_PROCESSOR:
				return true;
			case VIEW_KEYLOG_FLAT:
				return true;
			default:
				break;
			}
			return false;
		}

		public String defaultString() {
			switch (this) {
			case VIEW_MAIN_LAST:
				return "list";
			case LOGIN_RECENT:
				return "";
			default:
				break;

			}
			return null;
		}

		public int defaultInt() {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
