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
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * CPU attribute keys
 */
public enum AK_CPU implements PluralKey {
	CACHE, CORES, FREQUENCY, FREQUENCY_MAX, MODEL, TEMP, TOTAL_USAGE, VENDOR;

	@Override
	public Attribute fabricate() {
		return new UntrackedAttribute();
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	@Override
	public String toString() {
		switch (this) {
		case CACHE:
			return "CPU Cache Size";
		case CORES:
			return "CPU Cores";
		case FREQUENCY:
			return "CPU Frequency";
		case FREQUENCY_MAX:
			return "CPU Frequency Limit";
		case MODEL:
			return "CPU Model";
		case TEMP:
			return "CPU Temperature";
		case TOTAL_USAGE:
			return "CPU Usage";
		case VENDOR:
			return "CPU Vendor";
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

	private static final int TYPE_ID = 1;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
