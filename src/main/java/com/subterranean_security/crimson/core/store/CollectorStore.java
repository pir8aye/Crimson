package com.subterranean_security.crimson.core.store;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.platform.collect.Collector;
import com.subterranean_security.crimson.core.platform.collect.plural.CPU;
import com.subterranean_security.crimson.core.platform.collect.plural.DISP;
import com.subterranean_security.crimson.core.platform.collect.plural.NIC;

/**
 * @author cilki
 * @since 5.0.0
 */
public final class CollectorStore {

	private CollectorStore() {
	}

	private static Map<Integer, Collector> collectors;

	static {
		collectors = new HashMap<>();
	}

	public static void scanCPU() throws SigarException {

		Cpu timing = SigarStore.getSigar().getCpu();
		CpuInfo[] info = SigarStore.getSigar().getCpuInfoList();
		CpuPerc[] percentage = SigarStore.getSigar().getCpuPercList();

		if (info.length != percentage.length)
			throw new SigarException("Inconsistent CPU data");

		for (int i = 0; i < info.length; i++) {
			CPU cpu = new CPU(timing, info[i], percentage[i]);
		}

	}

	public static void scanNIC() throws SigarException {

		for (String iface : SigarStore.getSigar().getNetInterfaceList()) {
			NetInterfaceConfig config = new NetInterfaceConfig();
			NetInterfaceStat stat = new NetInterfaceStat();

			config.gather(SigarStore.getSigar(), iface);
			stat.gather(SigarStore.getSigar(), iface);

			// TODO unconnected interfaces may also be 0.0.0.0
			// figure out how to filter non-physical interfaces
			if (config.getAddress().equals("0.0.0.0")) {
				continue;
			}

			NIC nic = new NIC(config, stat);

		}

	}

	public static void scanDISP() {
		for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			DISP disp = new DISP(device);
		}
	}

	public static Collector getCollector(int gtid) {
		return collectors.get(gtid);
	}

}
