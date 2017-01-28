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
package com.subterranean_security.crimson.core.platform.info;

import com.subterranean_security.crimson.core.platform.Platform.ARCH;
import com.subterranean_security.crimson.universal.Universal;

public final class JAVA {

	private JAVA() {
	}

	public static String getVersion() {
		return System.getProperty("java.version");
	}

	public static String getVendor() {
		return System.getProperty("java.vendor");
	}

	public static String getHome() {
		return System.getProperty("java.home");
	}

	public static String getStartTime() {
		return Universal.start.toString();
	}

	public static String getArch() {
		return System.getProperty("os.arch");
	}

	public static ARCH getARCH() {
		String arch = getArch().toLowerCase();

		if (arch.equals("sparc")) {
			return ARCH.SPARC;
		} else if (arch.equals("x86") || arch.equals("i386") || arch.equals("i486") || arch.equals("i586")
				|| arch.equals("i686")) {
			return ARCH.X86;
		} else if (arch.equals("x86_64") || arch.equals("amd64") || arch.equals("k8")) {
			return ARCH.X64;
		} else {
			return ARCH.UNSUPPORTED;
		}
	}

}
