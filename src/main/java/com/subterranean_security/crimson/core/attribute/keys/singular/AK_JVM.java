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
import com.subterranean_security.crimson.core.platform.collect.singular.JVM;

/**
 * Java attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_JVM implements SingularKey {
	ARCH, PATH, START_TIME, VENDOR, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case VERSION:
			return "Java Version";
		case ARCH:
			return "Java Architecture";
		case PATH:
			return "Java Path";
		case START_TIME:
			return "Java Start Time";
		case VENDOR:
			return "Java Vendor";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case ARCH:
			return JVM.getArch();
		case PATH:
			return JVM.getHome();
		case START_TIME:
			return JVM.getStartTime();
		case VENDOR:
			return JVM.getVendor();
		case VERSION:
			return JVM.getVersion();
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
		return TypeIndex.JVM.ordinal();
	}

}