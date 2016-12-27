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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.Platform.ARCH;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.Native;

public final class OS {

	private OS() {
	}

	public static String getLanguage() {
		return new Locale(System.getProperty("user.language")).getDisplayName();
	}

	public static String getTimezone() {
		return Calendar.getInstance().getTimeZone().getDisplayName();
	}

	public static String getArch() {
		return SigarStore.getOperatingSystem().getArch();
	}

	public static String getActiveWindow() {
		return Native.getActiveWindow();
	}

	public static String getEndian() {
		return SigarStore.getOperatingSystem().getCpuEndian();
	}

	public static String getVirtualization() {
		// TODO Auto-generated method stub
		return "";
	}

	public static String getStartTime() {
		return new Date(System.currentTimeMillis() - (long) (SigarStore.getUptime().getUptime() * 1000)).toString();
	}

	public static OSFAMILY getFamily() {
		String name = System.getProperty("os.name").toLowerCase();
		if (name.endsWith("bsd")) {
			return OSFAMILY.BSD;
		} else if (name.equals("mac os x")) {
			return OSFAMILY.OSX;
		} else if (name.equals("solaris") || name.equals("sunos")) {
			return OSFAMILY.SOL;
		} else if (name.equals("linux")) {
			return OSFAMILY.LIN;
		} else if (name.startsWith("windows")) {
			return OSFAMILY.WIN;
		} else {
			return OSFAMILY.UNSUPPORTED;
		}
	}

	public static String getName() {

		switch (Platform.osFamily) {
		case LIN:
			return Linux.getDistro();
		default:
			return System.getProperty("os.name");

		}
	}

	public enum OSFAMILY {
		BSD, OSX, SOL, LIN, WIN, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public String toName() {
			switch (this) {
			case BSD:
				return "BSD";
			case LIN:
				return "Linux";
			case OSX:
				return "OS X";
			case SOL:
				return "Solaris";
			case WIN:
				return "Windows";
			default:
				return null;
			}
		}

		public String getLapisName(ARCH arch) {
			switch (this) {
			case SOL:
			case BSD:
			case LIN:
				switch (arch) {
				case X64:
					return "libcrimson64.so";
				case X86:
					return "libcrimson32.so";
				default:
					return null;
				}
			case OSX:
				switch (arch) {
				case X64:
					return "libcrimson64.dylib";
				case X86:
					return "libcrimson32.dylib";
				default:
					return null;
				}
			case WIN:
				switch (arch) {
				case X64:
					return "crimson64.dll";
				case X86:
					return "crimson32.dll";
				default:
					return null;
				}
			default:
				return null;
			}
		}

		public String getJDBCName(ARCH arch) {
			switch (this) {
			case SOL:
			case BSD:
			case LIN:
				switch (arch) {
				case X64:
					return "libjdbc64.so";
				case X86:
					return "libjdbc32.so";
				default:
					return null;
				}
			case OSX:
				switch (arch) {
				case X64:
					return "libjdbc64.jnilib";
				case X86:
					return "libjdbc32.jnilib";
				default:
					return null;
				}
			case WIN:
				switch (arch) {
				case X64:
					return "jdbc64.dll";
				case X86:
					return "jdbc32.dll";
				default:
					return null;
				}
			default:
				return null;
			}
		}

	}

}
