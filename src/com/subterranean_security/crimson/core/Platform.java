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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;

public enum Platform {
	;

	public static final ARCH sysArch = null;
	public static final ARCH javaArch = getJVMArch();
	public static final OSFAMILY os = getFamily();
	public static final OSVERSION sos = null;

	public enum OSFAMILY {
		BSD, OSX, SOLARIS, LINUX, WINDOWS, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum ARCH {
		X86, X64, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum OSVERSION {
		LINUX_UBUNTU, LINUX_SLACKWARE, LINUX_ARCH, LINUX_MINT, LINUX_FEDORA, WINDOWS_XP, WINDOWS_VISTA, WINDOWS_7, WINDOWS_8, WINDOWS_10,

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
		} else if (osName.contains("windows")) {
			return OSFAMILY.WINDOWS;
		} else {
			return OSFAMILY.UNSUPPORTED;
		}
	}

	public static ARCH getJVMArch() {
		String osArch = System.getProperty("os.arch").toLowerCase();

		if (osArch.equals("sparc")) {
			return ARCH.SPARC;
		} else if (osArch.equals("x86") || osArch.equals("i386") || osArch.equals("i486") || osArch.equals("i586")
				|| osArch.equals("i686")) {
			return ARCH.X86;
		} else if (osArch.equals("x86_64") || osArch.equals("amd64") || osArch.equals("k8")) {
			return ARCH.X64;
		} else {
			return ARCH.UNSUPPORTED;
		}
	}

	public static enum Advanced {
		;

		private static Sigar sigar;

		private static Cpu cpu;
		private static CpuInfo[] cpuInfo;
		private static NetInfo netInfo;
		private static ProcMem crimsonProcess;
		private static ProcCpu crimsonProcessCpu;

		private static long pid = 0;

		public static void setLibraryPath() {
			switch (Platform.os) {
			case BSD:
				System.setProperty("java.library.path", Common.base.getAbsolutePath() + "/lib/jni/bsd");
				break;
			case LINUX:
				System.setProperty("java.library.path", Common.base.getAbsolutePath() + "/lib/jni/osx");
				break;
			case OSX:
				System.setProperty("java.library.path", Common.base.getAbsolutePath() + "/lib/jni/osx");
				break;
			case SOLARIS:
				System.setProperty("java.library.path", Common.base.getAbsolutePath() + "/lib/jni/sol");
				break;
			case WINDOWS:
				System.setProperty("java.library.path", Common.base.getAbsolutePath() + "/lib/jni/win");
				break;
			default:
				break;
			}
		}

		public static void loadSigar() {
			setLibraryPath();
			sigar = new Sigar();
			cpu = new Cpu();
			netInfo = new NetInfo();
			crimsonProcess = new ProcMem();
			crimsonProcessCpu = new ProcCpu();

			try {
				pid = sigar.getPid();
				cpu.gather(sigar);
				cpuInfo = sigar.getCpuInfoList();
				netInfo.gather(sigar);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static void loadLapis() {
			setLibraryPath();
			switch (Platform.javaArch) {
			case X64:
				System.loadLibrary("crimson64");
				break;
			case X86:
				System.loadLibrary("crimson32");
				break;
			case SPARC:
				System.loadLibrary("crimsonSPARC");
				break;
			default:
				break;
			}

		}

		public static String getHostname() {
			try {
				return InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				return "unknown";
			}
		}

		public static String getUsername() {
			return System.getProperty("user.name");
		}

		public static String getUserDirectory() {
			return System.getProperty("user.dir");
		}

		public static String getUserHome() {
			return System.getProperty("user.home");
		}

		public static String getLanguage() {
			return System.getProperty("user.language");
		}

		public static String getJavaVersion() {
			return System.getProperty("java.version");
		}

		public static String getJavaVendor() {
			return System.getProperty("java.vendor");
		}

		public static String getJavaHome() {
			return System.getProperty("java.home");
		}

		public static String getJavaArch() {
			return System.getProperty("os.arch");
		}

		public static String getCPUModel() {
			String model = cpuInfo[0].getModel().replaceAll("\\(.+?\\)", "");
			return model.substring(0, model.indexOf("CPU @")).trim();
		}

		public static float[] getCPUSpeed() {
			try {
				cpuInfo = sigar.getCpuInfoList();
			} catch (SigarException e) {
				return null;
			}
			float[] speeds = new float[cpuInfo.length];
			for (int i = 0; i < cpuInfo.length; i++) {
				speeds[i] = cpuInfo[i].getMhz();
			}
			return speeds;
		}

		public static float getAverageCPUSpeed() {
			float[] speeds = getCPUSpeed();
			float speed = 0;
			for (float f : speeds) {
				speed += f;
			}
			return speed / speeds.length;
		}

		public static long getCrimsonMemoryUsage() {

			try {
				crimsonProcess.gather(sigar, pid);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return crimsonProcess.getResident();
		}

		public static double getCrimsonCpuUsage() {

			try {
				crimsonProcessCpu.gather(sigar, pid);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return crimsonProcessCpu.getPercent();
		}

		public static EV_ProfileDelta getFullProfile() {
			EV_ProfileDelta.Builder info = EV_ProfileDelta.newBuilder();

			try {
				info.setCvid(Common.cvid);
			} catch (Exception e1) {
				// TODO handle
				info.setCvid(0);
			}

			info.setCrimsonVersion(Common.version);

			info.setUserName(getUsername());
			info.setNetHostname(getHostname());
			info.setUserDir(getUserDirectory());
			info.setUserHome(getUserHome());
			info.setLanguage(getLanguage());
			info.setJavaVersion(getJavaVersion());
			info.setJavaVendor(getJavaVersion());
			info.setJavaHome(getJavaHome());
			info.setJavaArch(getJavaArch());
			info.setCpuModel(getCPUModel());

			return info.build();
		}
	}
}