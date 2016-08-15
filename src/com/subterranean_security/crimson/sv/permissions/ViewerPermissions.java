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
import java.util.ArrayList;
import java.util.List;

public class ViewerPermissions implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Long> flags = new ArrayList<Long>();

	public ViewerPermissions() {

	}

	public ViewerPermissions(List<Long> data) {
		load(data);
	}

	public ViewerPermissions addFlag(int perm, boolean b) {
		if (b)
			return addFlag(0, perm);
		else
			return this;
	}

	public ViewerPermissions addFlag(int perm) {
		return addFlag(0, perm);
	}

	public ViewerPermissions addFlag(int cid, int perm) {
		flags.add(translateFlag(cid, perm));
		return this;
	}

	public boolean getFlag(int perm) {
		return getFlag(0, perm);
	}

	public boolean getFlag(int cid, int perm) {
		return (flags.contains((long) Perm.Super) || flags.contains(translateFlag(cid, perm)));
	}

	public void load(List<Long> data) {
		flags.addAll(data);
	}

	public List<Integer> listPermissions(int cid) {
		List<Integer> list = new ArrayList<Integer>();
		for (long l : flags) {
			if ((l & 0xFFFFFFFF) == cid) {
				list.add((int) (l >> 32));
			}
		}
		return list;
	}

	public List<Long> listPermissions() {
		return flags;
	}

	private long translateFlag(int cid, int perm) {
		// the permission identifier exists in the upper 32 bits
		long flag = ((long) perm) << 32;

		// the cid occupies the lower 32 bits
		flag += cid;
		return flag;
	}

}