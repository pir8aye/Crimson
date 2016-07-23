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

import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Misc.GraphicsDisplay;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.ObjectTransfer;

public enum Platform {
	;

	private static final Logger log = LoggerFactory.getLogger(Platform.class);

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
				return null;

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
				return null;

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
			File os_release = new File("/etc/os-release");

			if (os_release.exists() && os_release.canRead()) {
				try {
					for (String s : CUtil.Files.readFileLines(os_release)) {
						if (s.startsWith("PRETTY_NAME")) {
							return s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
						}
					}
				} catch (IOException e) {
					log.warn("Failed to read os-release");
				}
			}

			File issue = new File("/etc/issue");

			if (issue.exists() && issue.canRead()) {
				try {
					for (String s : CUtil.Files.readFileLines(issue)) {
						return s.trim();
					}
				} catch (IOException e) {
					log.warn("Failed to read issue");
				}
			}

			return "Unknown Linux";

		default:
			return System.getProperty("os.name");

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

	public static GraphicsDisplay[] getDisplays() {
		if (GraphicsEnvironment.isHeadless()) {
			return new GraphicsDisplay[0];
		}
		GraphicsDevice[] dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDisplay[] disp = new GraphicsDisplay[dev.length];
		for (int i = 0; i < dev.length; i++) {
			disp[i] = GraphicsDisplay.newBuilder().setId(dev[i].getIDstring())
					.setWidth(dev[i].getDefaultConfiguration().getBounds().width)
					.setHeight(dev[i].getDefaultConfiguration().getBounds().height).build();
		}
		return disp;
	}

	public static ImageIcon getUserAvatar() {
		if (osFamily == OSFAMILY.WIN) {
			try {
				BufferedImage avatar = ImageIO
						.read(new File("C:/Users/" + getUsername() + "/AppData/Local/Temp/" + getUsername() + ".bmp"));
				BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
				Graphics g = resized.createGraphics();
				g.drawImage(avatar, 0, 0, 16, 16, null);
				g.dispose();
				return new ImageIcon(resized);
			} catch (IOException e) {
				log.warn("Failed to get user avatar: " + e.getMessage());
			}
		}
		return null;

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
					new File(Common.Directories.base.getAbsolutePath() + "/lib/jni/" + osFamily.toString())
							.getAbsolutePath());

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

		public static Sigar getSigar() {
			return sigar;
		}

		public static String getExtIp() {
			if (Common.instance == Instance.CLIENT && !Client.ic.getAllowMiscConnections()) {
				return "0.0.0.0";
			}
			return CUtil.Network.getEIP();
		}

		public static String getCPUModel() {
			String model = cpuInfo[0].getVendor();
			model += " " + cpuInfo[0].getModel().replaceAll("\\(.+?\\)", "");

			try {
				model = model.substring(0, model.indexOf("CPU @")).trim();
			} catch (Exception e) {
				// ignore
			}
			return model;
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
			info.setUserAvatar(new String(B64.encode(ObjectTransfer.Default.serialize(getUserAvatar()))));
			info.setHostname(getHostname());
			info.setUserHome(getUserHome());
			info.setLanguage(getLanguage());
			info.setJavaVersion(getJavaVersion());
			info.setJavaVendor(getJavaVersion());
			info.setJavaArch(getJavaArch());
			info.setCpuModel(getCPUModel());
			info.setExtIp(getExtIp());
			for (GraphicsDisplay gd : getDisplays()) {
				info.addDisplays(gd);
			}

			return info.build();
		}

		public static double[] getCPUTemps() {
			switch (osFamily) {
			case LIN:
				double[] temps = HWmon.query();
				if (temps.length > 0) {
					return temps;
				}
				return new double[] {};

			default:
				return new double[] { Native.getCpuTemp() };

			}

		}

		public static class HWmon {

			private static ArrayList<RandomAccessFile> cores = new ArrayList<RandomAccessFile>();
			private static int maxCores = 512;

			public static double[] query() {

				if (cores.size() == 0) {

					File core = null;
					// get core hwmon directory
					for (File probe : new File("/sys/class/hwmon").listFiles()) {
						try {
							if (CUtil.Files.readFileString(new File(probe.getAbsolutePath() + "/name"))
									.contains("coretemp")) {
								core = probe;
								break;
							}
						} catch (IOException e) {
							continue;
						}
					}

					if (core == null) {
						return new double[0];
					}

					// get handles on files
					for (int i = 2; i < maxCores + 2; i++) {
						File f = new File(core.getAbsolutePath() + "/temp" + i + "_input");
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

						temps[i] = Double.parseDouble(cores.get(i).readLine()) / 1000.0;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				return temps;
			}

		}

	}

}
