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
import java.util.Set;
import java.util.TreeSet;

import com.subterranean_security.crimson.core.util.IDGen.Reserved;

/**
 * Both server and client permissions are handled by a ViewerPermissions
 * object.<br>
 * <br>
 * 
 * A permission flag is encoded in a 64 bit long:<br>
 * [32 bits] CVID/AuthID<br>
 * [16 bits] Reserved<br>
 * [16 bits] Permission Identifier<br>
 * 
 */
public class ViewerPermissions implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<Long> flags;

	public ViewerPermissions() {
		flags = new TreeSet<Long>();
	}

	public ViewerPermissions(ViewerPermissions base) {
		this();
		flags.addAll(base.flags);
	}

	public ViewerPermissions(List<Long> data) {
		this();
		flags.addAll(data);
	}

	public void add(List<Long> data) {
		flags.addAll(data);
	}

	public void intersect(List<Long> data) {

	}

	public void subtract(List<Long> data) {

	}

	/**
	 * Set a permission
	 * 
	 * @param cid
	 * @param perm
	 * @param b
	 * @return this
	 */
	public ViewerPermissions setFlag(int cid, short perm, boolean b) {
		if (b)
			return addFlag(cid, perm);
		else
			return delFlag(cid, perm);
	}

	/**
	 * Set a server permission
	 * 
	 * @param perm
	 * @param b
	 * @return
	 */
	public ViewerPermissions setFlag(short perm, boolean b) {
		return setFlag(Reserved.SERVER, perm, b);
	}

	/**
	 * Add a permission
	 * 
	 * @param cid
	 * @param perm
	 * @return
	 */
	public ViewerPermissions addFlag(int cid, short perm) {
		long flag = translateFlag(cid, perm);
		if (!flags.contains(flag)) {
			flags.add(flag);
		}
		return this;
	}

	/**
	 * Add a server permission
	 * 
	 * @param perm
	 * @return
	 */
	public ViewerPermissions addFlag(short perm) {
		return addFlag(Reserved.SERVER, perm);
	}

	/**
	 * Remove a permission
	 * 
	 * @param cid
	 * @param perm
	 * @return this
	 */
	public ViewerPermissions delFlag(int cid, short perm) {
		flags.remove(translateFlag(cid, perm));
		return this;
	}

	/**
	 * Remove a server permission
	 * 
	 * @param perm
	 * @return
	 */
	public ViewerPermissions delFlag(short perm) {
		return delFlag(Reserved.SERVER, perm);
	}

	/**
	 * Query a permission
	 * 
	 * @param cid
	 * @param perm
	 * @return
	 */
	public boolean getFlag(int cid, short perm) {
		return (flags.contains((long) Perm.Super) || flags.contains(translateFlag(cid, perm)));
	}

	/**
	 * Query a server permission
	 * 
	 * @param perm
	 * @return
	 */
	public boolean getFlag(short perm) {
		return getFlag(Reserved.SERVER, perm);
	}

	/**
	 * Get all permissions for a specific CVID/AuthID
	 * 
	 * @param cid
	 * @return
	 */
	public List<Short> getPermissions(int cid) {
		List<Short> list = new ArrayList<>();
		for (long l : flags) {
			if ((l >> 32) == cid) {
				list.add((short) (l & 0xFFFF));
			}
		}
		return list;
	}

	/**
	 * Get all flags
	 * 
	 * @return
	 */
	public List<Long> getFlags() {
		List<Long> list = new ArrayList<>();
		list.addAll(flags);
		return list;
	}

	/**
	 * Transform a CVID/AuthID and Permission into a flag using the default
	 * encoding scheme.
	 * 
	 * @param cid
	 * @param perm
	 * @return
	 */
	public static long translateFlag(int cid, short perm) {
		// the CVID/AuthID exists in the upper 32 bits
		long flag = ((long) cid) << 32;

		// the permission identifier occupies the lower 16 bits
		flag += ((long) perm);

		return flag;
	}

}