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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;

import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Native;

public enum Platform {
	;

	private static final Logger log = CUtil.Logging.getLogger(Platform.class);

	public static final ARCH sysArch = null;
	public static final ARCH javaArch = getJVMArch();
	public static final OSFAMILY osFamily = getFamily();
	public static final String osName = getOsName();

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
	}

	public enum ARCH {
		X86, X64, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
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

	public static String getOsName() {
		switch (osFamily) {
		case LIN:
			String distro = "";// TODO get output (cat /etc/issue)
			break;
		default:
			return System.getProperty("os.name");

		}
		return null;
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
		private static CpuPerc cpuPerc;
		private static CpuInfo[] cpuInfo;
		private static NetInfo netInfo;
		private static ProcMem crimsonProcess;
		private static ProcCpu crimsonProcessCpu;

		private static long pid = 0;

		public static void loadSigar() {
			System.setProperty("java.library.path",
					new File(Common.base.getAbsolutePath() + "/lib/jni/" + osFamily.toString()).getAbsolutePath());
			log.debug("Loading SIGAR native library");

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
			log.debug("Loading LAPIS native library");

			try {
				switch (Platform.javaArch) {
				case X64:
					System.load(new File(
							Common.base.getAbsolutePath() + "/lib/jni/" + osFamily.toString() + "/crimson64.dll")
									.getAbsolutePath());
					break;
				case X86:
					System.load(new File(
							Common.base.getAbsolutePath() + "/lib/jni/" + osFamily.toString() + "/crimson32.dll")
									.getAbsolutePath());
					break;
				case SPARC:
					System.load(new File(
							Common.base.getAbsolutePath() + "/lib/jni/" + osFamily.toString() + "/crimsonSPARC.dll")
									.getAbsolutePath());
					break;
				default:
					break;
				}
			} catch (Throwable e) {
				log.error("Failed to load lapis!");
				e.printStackTrace();
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

		public static String getExtIp() {
			// if resolution is enabled
			return "0.0.0.0";// TODO
		}

		public static String getCPUModel() {
			String model = cpuInfo[0].getVendor();
			model += " " + cpuInfo[0].getModel().replaceAll("\\(.+?\\)", "");
			return model.substring(0, model.indexOf("CPU @")).trim();
		}

		public static int[] getCPUSpeed() {
			try {
				cpuInfo = sigar.getCpuInfoList();
			} catch (SigarException e) {
				return null;
			}
			int[] speeds = new int[cpuInfo.length];
			for (int i = 0; i < cpuInfo.length; i++) {
				speeds[i] = cpuInfo[i].getMhz();
			}
			// log.debug("CPU Speeds: {}", Arrays.toString(speeds));
			return speeds;
		}

		public static double getCPUUsage() {
			try {
				cpuPerc = sigar.getCpuPerc();
			} catch (SigarException e) {
				return 0;
			}

			return cpuPerc.getCombined();
		}

		public static long getCPUTemp() {
			return Native.getCpuTemp();

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

			info.setOsName(osName);
			info.setOsFamily(osFamily.toName());
			info.setUserName(getUsername());
			info.setHostname(getHostname());
			info.setUserHome(getUserHome());
			info.setLanguage(getLanguage());
			info.setJavaVersion(getJavaVersion());
			info.setJavaVendor(getJavaVersion());
			info.setJavaArch(getJavaArch());
			info.setCpuModel(getCPUModel());
			info.setExtIp(getExtIp());

			return info.build();
		}
	}

	public static class TEMP {

		// TODO move this!
		private static ArrayList<RandomAccessFile> cores = new ArrayList<RandomAccessFile>();
		private static int maxCores = 64;

		public static double[] getLinuxCPUTemps() {

			if (cores.size() == 0) {
				for (int i = 2; i < maxCores + 2; i++) {
					File f = new File("/sys/class/hwmon/hwmon1/temp" + i + "_input");
					if (f.exists()) {
						try {
							cores.add(new RandomAccessFile(f, "r"));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
			}
			double[] temps = new double[cores.size()];
			for (int i = 0; i < cores.size(); i++) {
				try {
					cores.get(i).seek(0);
					temps[i] = cores.get(i).readDouble();
					System.out.println("Core " + i + " temp: " + temps[i]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;
		}

	}
}
