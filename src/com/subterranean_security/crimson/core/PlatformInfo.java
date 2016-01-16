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

package com.subterranean_security.crimson.core;

public enum PlatformInfo {
	;

	public static final ARCH		sysArch	= null;
	public static final ARCH		jreArch	= null;
	public static final OSFAMILY	os		= getFamily();
	public static final SVERSION	sos		= null;

	public enum OSFAMILY {
		BSD, OSX, SOLARIS, LINUX, WINDOWS, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum ARCH {
		X86, X64, ARM, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum SVERSION {
		LINUX_UBUNTU, LINUX_SLACKWARE, LINUX_ANDROID, LINUX_ARCH, LINUX_MINT, LINUX_FEDORA, WINDOWS_XP, WINDOWS_VISTA, WINDOWS_7, WINDOWS_8, WINDOWS_10,

	}

	public static OSFAMILY getFamily() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.endsWith("bsd")) {
			return OSFAMILY.BSD;
		} else if (osName.equals("mac os x")) {
			return OSFAMILY.OSX;
		} else if (osName.equals("solaris") || osName.equals("sunos")) {
			return OSFAMILY.SOLARIS;
		} else if (osName.equals("linux")) {
			return OSFAMILY.LINUX;
		} else if (osName.startsWith("windows")) {
			return OSFAMILY.WINDOWS;
		} else {
			return OSFAMILY.UNSUPPORTED;
		}
	}

	public static ARCH getArchitecture() {
		String osArch = System.getProperty("os.arch").toLowerCase();

		if (osArch.equals("arm")) {
			return ARCH.ARM;
		} else if (osArch.equals("sparc")) {
			return ARCH.SPARC;
		} else if (osArch.equals("x86") || osArch.equals("i386") || osArch.equals("i486") || osArch.equals("i586") || osArch.equals("i686")) {
			return ARCH.X86;
		} else if (osArch.equals("x86_64") || osArch.equals("amd64") || osArch.equals("k8")) {
			return ARCH.X64;
		} else {
			return ARCH.UNSUPPORTED;
		}
	}

}
