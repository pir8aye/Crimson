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
package com.subterranean_security.crimson.core.attribute.keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_CPU;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_DISP;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_GPU;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_NIC;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_TORRENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

public interface AttributeKey {

	public enum Type {
		GENERAL, CPU, GPU, DISP, NIC, TORRENT;

		public static final Type[] values = Type.values();

		public static final Map<Integer, AttributeKey> keyList = getKeyList();

		private static Map<Integer, AttributeKey> getKeyList() {
			Map<Integer, AttributeKey> list = new HashMap<Integer, AttributeKey>();
			for (AttributeKey key : AKeySimple.values())
				list.put(key.getFullID(), key);
			for (AttributeKey key : AK_CPU.values())
				list.put(key.getFullID(), key);
			for (AttributeKey key : AK_GPU.values())
				list.put(key.getFullID(), key);
			for (AttributeKey key : AK_NIC.values())
				list.put(key.getFullID(), key);
			for (AttributeKey key : AK_TORRENT.values())
				list.put(key.getFullID(), key);
			return list;
		}
	}

	static final int ORDINAL_SPACE = 6;

	/**
	 * Gets the nicely formatted title of this key
	 * 
	 * @return
	 */
	public String toString();

	/**
	 * Gets the general type of the key
	 * 
	 * @return
	 */
	public int getGroupType();

	/**
	 * Gets the ordinal of the key
	 * 
	 * @return
	 */
	public int getOrdinal();

	default public int getFullID() {
		return (getGroupType() << ORDINAL_SPACE) + getOrdinal();
	}

	public Attribute getNewAttribute();

	default public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	/**
	 * Whether the current key can be used as a header in a host-list or
	 * host-graph
	 * 
	 * @return
	 */

	default public boolean isHeaderable() {
		return true;
	}

	public static List<AttributeKey> getAllGroupKeys() {
		ArrayList<AttributeKey> list = new ArrayList<AttributeKey>();
		list.addAll(Arrays.asList(AK_CPU.values()));
		list.addAll(Arrays.asList(AK_GPU.values()));
		list.addAll(Arrays.asList(AK_DISP.values()));
		list.addAll(Arrays.asList(AK_NIC.values()));
		list.addAll(Arrays.asList(AK_TORRENT.values()));
		return list;
	}

	public static AttributeKey getKey(int keyID) {
		return Type.keyList.get(keyID);
	}

}
