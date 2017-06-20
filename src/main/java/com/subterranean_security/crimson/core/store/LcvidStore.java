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
package com.subterranean_security.crimson.core.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.subterranean_security.crimson.universal.stores.DatabaseStore;

/**
 * Provides easy access to the stored mapping between lcvids (long cvids) and
 * cvids (Client/Viewer IDs).
 *
 */
public final class LcvidStore {
	private LcvidStore() {
	}

	/**
	 * The current lcvid (for viewers and clients only)
	 */
	public static String lcvid;

	/**
	 * The current cvid (for viewers and clients only)
	 */
	public static int cvid;

	/**
	 * Maps long-cvids to short-cvids. In clients and viewers, short-cvids are
	 * not stored, so this map acts like a list
	 */
	private static Map<String, Integer> lcvidMap;

	static {
		lcvidMap = ((Map<String, Integer>) DatabaseStore.getDatabase().getObject("lcvid"));
		if (lcvidMap == null) {
			DatabaseStore.getDatabase().store("lcvid", new HashMap<String, Integer>());
			lcvidMap = ((Map<String, Integer>) DatabaseStore.getDatabase().getObject("lcvid"));
		}
	}

	public static void addLcvid(String lcvid) {
		addLcvid(lcvid, 0);
	}

	public static void addLcvid(String lcvid, int cvid) {
		lcvidMap.put(lcvid, cvid);
	}

	public static Set<String> getLcvidSet() {
		return lcvidMap.keySet();
	}

	public static boolean contains(String lcvid) {
		return lcvidMap.containsKey(lcvid);
	}

	public static Integer get(String lcvid) {
		return lcvidMap.get(lcvid);
	}

}
