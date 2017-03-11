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

import java.util.ArrayList;

import com.subterranean_security.crimson.core.platform.LocalFS;

public final class FileManagerStore {
	private FileManagerStore() {
	}

	private static ArrayList<LocalFS> lfs = new ArrayList<LocalFS>();

	/**
	 * Store a new LocalFS in this store
	 * 
	 * @param l
	 * @return FS object ID for convenience
	 */
	public static int add(LocalFS l) {
		lfs.add(l);
		return l.getFmid();
	}

	/**
	 * Get a LocalFS object from store
	 * 
	 * @param fmid
	 * @return LocalFS object with ID fmid or null
	 */
	public static LocalFS get(int fmid) {
		for (LocalFS l : lfs) {
			if (l.getFmid() == fmid) {
				return l;
			}
		}
		return null;
	}

	/**
	 * Clear this store
	 */
	public static void clear() {
		lfs.clear();
	}
}