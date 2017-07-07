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

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Server attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_SERVER implements SingularKey {
	ACTIVE_LISTENERS, CONNECTED_CLIENTS, CONNECTED_VIEWERS, INACTIVE_LISTENERS, TOTAL_CLIENTS, TOTAL_VIEWERS;

	@Override
	public String toString() {
		switch (this) {
		}
		return super.toString();
	}

	@Override
	public boolean isHeaderable() {
		switch (this) {
		case CONNECTED_CLIENTS:
		case CONNECTED_VIEWERS:
		case TOTAL_CLIENTS:
		case TOTAL_VIEWERS:
		case ACTIVE_LISTENERS:
		case INACTIVE_LISTENERS:
			return false;
		default:
			return true;
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case CONNECTED_CLIENTS:
		case CONNECTED_VIEWERS:
		case TOTAL_CLIENTS:
		case TOTAL_VIEWERS:
		case ACTIVE_LISTENERS:
		case INACTIVE_LISTENERS:
			return instance == Instance.SERVER || instance == Instance.VIEWER;
		default:
			return SingularKey.super.isCompatible(os, instance);
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
		return TypeIndex.SERVER.ordinal();
	}

	@Override
	public Object query() {
		switch (this) {
		case ACTIVE_LISTENERS:
			break;
		case CONNECTED_CLIENTS:
			break;
		case CONNECTED_VIEWERS:
			break;
		case INACTIVE_LISTENERS:
			break;
		case TOTAL_CLIENTS:
			break;
		case TOTAL_VIEWERS:
			break;
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);

		}
		return null;
	}
}
