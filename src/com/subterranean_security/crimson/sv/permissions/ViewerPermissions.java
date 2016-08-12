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
package com.subterranean_security.crimson.sv.permissions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewerPermissions implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ViewerPermissions.class);

	private HashMap<Integer, Boolean> flags = new HashMap<Integer, Boolean>();

	public ViewerPermissions() {

	}

	public ViewerPermissions(int[] data) {
		load(data);
	}

	public ViewerPermissions(List<Integer> data) {
		load(data);
	}

	public ViewerPermissions addFlag(int perm, boolean bool) {
		flags.put(perm, bool);
		return this;
	}

	public ViewerPermissions addFlag(int perm) {
		return addFlag(perm, true);
	}

	public boolean getFlag(int perm) {
		if (flags.containsKey(Perm.Super) && flags.get(Perm.Super)) {
			return true;
		}
		if (flags.containsKey(perm)) {
			return flags.get(perm);
		} else {
			log.warn("Queried nonexistant flag: {}", perm);
			return false;
		}

	}

	public void load(int[] data) {
		for (int i = 0; i < data.length; i += 2) {
			addFlag(data[i], data[i + 1] == 1);
		}
	}

	public void load(List<Integer> data) {
		for (int i = 0; i < data.size(); i += 2) {
			addFlag(data.get(i), data.get(i + 1) == 1);
		}
	}

	public int[] extract() {
		int[] data = new int[flags.size() * 2];
		int r = 0;
		for (int i : flags.keySet()) {
			data[r++] = i;
			data[r++] = flags.get(i) ? 1 : 0;
		}
		return data;
	}

}