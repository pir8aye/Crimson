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
import com.subterranean_security.crimson.core.platform.collect.singular.LIN;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Linux attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_LIN implements SingularKey {
	DISTRO, KERNEL_VERSION, PACKAGES, SHELL, TERMINAL, WM;

	@Override
	public String toString() {
		switch (this) {
		case DISTRO:
			return "Linux Distribution";
		case KERNEL_VERSION:
			return "Kernel Version";
		case PACKAGES:
			return "Packages";
		case SHELL:
			return "Shell";
		case WM:
			return "Window Manager/Desktop Environment";
		case TERMINAL:
			return "Terminal";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case DISTRO:
			return LIN.getDistro();
		case KERNEL_VERSION:
			return LIN.getKernel();
		case PACKAGES:
			return LIN.getPackages();
		case SHELL:
			return LIN.getShell();
		case TERMINAL:
			return LIN.getTerminal();
		case WM:
			return LIN.getWM();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case DISTRO:
		case KERNEL_VERSION:
		case PACKAGES:
		case SHELL:
		case WM:
		case TERMINAL:
			return os == OSFAMILY.LIN;
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
		return TypeIndex.LIN.ordinal();
	}

}
