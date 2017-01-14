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
package com.subterranean_security.crimson.core.util;

public final class VersionUtil {
	private VersionUtil() {
	}

	/**
	 * Version format: X.X.X.X[-xxxx]
	 * 
	 * @param v1
	 * @param v2
	 * @return true if v1 is newer than v2
	 */
	public static boolean isNewerVersion(String v1, String v2) {
		String[] pv1 = v1.split("-");

		String[] pv2 = v2.split("-");

		if (pv1.length == 2 && pv2.length == 2) {
			// simply compare build numbers
			return Integer.parseInt(pv1[1]) > Integer.parseInt(pv2[1]);
		}
		String[] ppv1 = pv1[0].split("\\.");
		String[] ppv2 = pv2[0].split("\\.");

		for (int i = 0; i < 4; i++) {
			if (Integer.parseInt(ppv1[i]) > Integer.parseInt(ppv2[i])) {
				return true;
			} else if (Integer.parseInt(ppv1[i]) < Integer.parseInt(ppv2[i])) {
				return false;
			}
		}

		return false;
	}

}
