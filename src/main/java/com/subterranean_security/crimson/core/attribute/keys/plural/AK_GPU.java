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
package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.PluralKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.platform.collect.plural.GPU;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.core.store.CollectorStore;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * GPU attribute keys
 */
public enum AK_GPU implements PluralKey {
	MODEL, RAM, RAM_TYPE, TEMP, VENDOR;

	@Override
	public Attribute fabricate() {
		return new UntrackedAttribute();
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	@Override
	public boolean isHeaderable() {
		return true;
	}

	@Override
	public String toSuperString() {
		return super.toString();
	}

	@Override
	public int getConstID() {
		return this.ordinal();
	}

	@Override
	public int getTypeID() {
		return TypeIndex.GPU.ordinal();
	}

	@Override
	public int getGroupID() {
		return groupID;
	}

	@Override
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	private int groupID;

	@Override
	public Object query() {
		if (groupID == 0)
			throw new IllegalStateException(
					"Failed to query attribute because groupID cannot be 0 for plural attributes");

		GPU collector = (GPU) CollectorStore.getCollector(getGTID());

		if (collector == null)
			throw new IllegalStateException(
					"Failed to query attribute because GTID (" + getGTID() + ") does not exist");
		return null;
	}
}
