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
import com.subterranean_security.crimson.core.platform.collect.singular.WIN;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Windows attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_WIN implements SingularKey {
	IE_VERSION, INSTALL_DATE, POWERSHELL_VERSION, SERIAL;

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return os == OSFAMILY.WIN;
	}

	@Override
	public String toString() {
		switch (this) {
		case IE_VERSION:
			return "Internet Explorer Version";
		case INSTALL_DATE:
			return "Install Date";
		case POWERSHELL_VERSION:
			return "Powershell version";
		case SERIAL:
			return "Windows Serial Number";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case IE_VERSION:
			return WIN.getIEVersion();
		case INSTALL_DATE:
			return WIN.getInstallTime();
		case POWERSHELL_VERSION:
			return WIN.getPowerShellVersion();
		case SERIAL:
			return WIN.getSerial();
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
		return TypeIndex.WIN.ordinal();
	}

}
