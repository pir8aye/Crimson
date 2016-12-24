package com.subterranean_security.crimson.core.platform;

import java.io.File;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Uptime;

import com.subterranean_security.crimson.core.Common;

public final class SigarStore {

	private SigarStore() {
	}

	private static Sigar sigar;

	// SIGAR objects
	private static OperatingSystem os;
	private static Uptime uptime;
	private static Mem mem;
	private static CpuInfo[] cpuInfo;
	private static CpuPerc[] cpuPerc;
	private static NetInfo netInfo;

	public static void refreshOperatingSystem() {
		try {
			os.gather(sigar);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static OperatingSystem getOperatingSystem() {
		return os;
	}

	public static Uptime getUptime() {
		try {
			uptime.gather(sigar);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uptime;
	}

	public static void refreshMem() {
		try {
			mem.gather(sigar);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Mem getMem() {
		return mem;
	}

	public static CpuInfo[] getCpuInfos() {
		return cpuInfo;
	}

	public static void refreshCpuPerc() {
		try {
			cpuPerc = sigar.getCpuPercList();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static CpuPerc[] getCpuPercs() {
		return cpuPerc;
	}

	public static void refreshNetInfo() {
		try {
			netInfo.gather(sigar);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static NetInfo getNetInfo() {
		return netInfo;
	}

	// Crimson Process
	private static long pID = 0;
	private static ProcMem processMem;
	private static ProcCpu processCpu;

	public static long getPID() {
		return pID;
	}

	public static void refreshProcessMem() {
		try {
			processMem.gather(sigar, pID);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void refreshProcessCpu() {
		try {
			processCpu.gather(sigar, pID);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ProcMem getProcessMem() {
		return processMem;
	}

	public static ProcCpu getProcessCpu() {
		return processCpu;
	}

	public static void loadSigar() {
		System.setProperty("java.library.path",
				new File(Common.Directories.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString())
						.getAbsolutePath());

		sigar = new Sigar();

		// Initialize memory information
		try {
			uptime = sigar.getUptime();
			mem = sigar.getMem();
			pID = sigar.getPid();
			processMem = sigar.getProcMem(pID);
			processCpu = sigar.getProcCpu(pID);
			os = OperatingSystem.getInstance();
			cpuInfo = sigar.getCpuInfoList();
			cpuPerc = sigar.getCpuPercList();
		} catch (SigarException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Initialize general network information
		try {
			netInfo = sigar.getNetInfo();
		} catch (SigarException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static Sigar getSigar() {
		return sigar;
	}

}