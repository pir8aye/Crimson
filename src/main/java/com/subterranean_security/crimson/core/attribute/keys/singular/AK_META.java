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
package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.universal.Universal;

/**
 * Meta attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_META implements SingularKey {
	CVID, ONLINE, FIRST_CONTACT, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case CVID:
			return "Client/Viewer ID";
		case FIRST_CONTACT:
			return "First Contact";
		case ONLINE:
			return "Online";
		case VERSION:
			return "Version";
		default:
			return super.toString();
		}
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
		return TypeIndex.META.ordinal();
	}

	@Override
	public Object query() {
		switch (this) {
		case CVID:
			return LcvidStore.cvid;
		case FIRST_CONTACT:
			return null;
		case ONLINE:
			return ConnectionStore.connectedDirectly(Reserved.SERVER);
		case VERSION:
			return Universal.version;
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

}
