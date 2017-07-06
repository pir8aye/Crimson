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
import com.subterranean_security.crimson.core.platform.collect.singular.CRIMSON;
import com.subterranean_security.crimson.core.platform.collect.singular.RAM;

/**
 * Client attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_CLIENT implements SingularKey {
	AUTH_ID, BASE_PATH, CPU_USAGE, INSTALL_DATE, RAM_USAGE, STATUS, KEYLOG;

	@Override
	public String toString() {
		switch (this) {
		case BASE_PATH:
			return "Install Path";
		case CPU_USAGE:
			return "Client CPU Usage";
		case INSTALL_DATE:
			return "Install Date";
		case RAM_USAGE:
			return "Client RAM Usage";
		case STATUS:
			return "Client Status";
		default:
			return super.toString();
		}
	}

	@Override
	public boolean isHeaderable() {
		switch (this) {
		case KEYLOG:
		case AUTH_ID:
			return false;
		default:
			return SingularKey.super.isHeaderable();
		}
	}

	@Override
	public Object query() {
		switch (this) {
		case BASE_PATH:
			return CRIMSON.getBasePath();
		case CPU_USAGE:
			return CRIMSON.getClientUsage();
		case INSTALL_DATE:
			return CRIMSON.getInstallDate();
		case RAM_USAGE:
			return RAM.getClientUsage();
		case STATUS:
			return CRIMSON.getStatus();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
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
		return TypeIndex.CLIENT.ordinal();
	}

}
