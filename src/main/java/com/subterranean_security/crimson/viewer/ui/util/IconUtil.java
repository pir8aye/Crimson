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
package com.subterranean_security.crimson.viewer.ui.util;

public final class IconUtil {
	public static String getOsIconPath(String os, int size) {
		return String.format("icons%d/platform/%s.png", size, findOs(os));
	}

	public static String getMonitorIconPath(String os, int size) {
		return String.format("icons%d/platform/monitors/%s.png", size, findOs(os));
	}

	private static String findOs(String os) {
		os = os.replaceAll(" ", "_").toLowerCase();

		return os;
	}
}
