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
package com.subterranean_security.crimson.core.platform.collect.plural;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.platform.collect.Collector;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.Native;

/**
 * A {@code Collector} for a CPU.
 * 
 * @author cilki
 * @since 4.0.0
 */
public final class CPU extends Collector {
	private static final Logger log = LoggerFactory.getLogger(CPU.class);

	private Cpu timing;
	private CpuInfo info;
	private CpuPerc percentage;

	public CPU(Cpu timing, CpuInfo cpuInfo, CpuPerc cpuPerc) {
		this.timing = timing;
		this.info = cpuInfo;
		this.percentage = cpuPerc;
	}

	public void refreshPercentages() {
		try {
			percentage = SigarStore.getSigar().getCpuPercList();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the CPU vendor.
	 * 
	 * @return A {@code String} containing the CPU vendor's name
	 */
	public String getVendor() {
		return info.getVendor();
	}

	/**
	 * Get the CPU model.
	 * 
	 * @return A {@code String} containing the CPU's model
	 */
	public String getModel() {
		String model = info.getModel().replaceAll("\\(.+?\\)", "");
		try {
			model = model.substring(0, model.indexOf("CPU @")).trim();
		} catch (Exception e) {
			// ignore
		}
		return model;
	}

	/**
	 * Get the CPU's reported clock speed. The actual clock speed is less than or
	 * equal to this value.
	 * 
	 * @return The clock speed in MegaHertz
	 */
	public int getMaxClockSpeed() {
		return info.getMhz();
	}

	/**
	 * Get the number of physical cores on this CPU
	 * 
	 * @return
	 */
	public int getCores() {
		return info.getTotalCores();
	}

	public long getCache() {
		return info.getCacheSize();
	}

	public double getTotalUsage() {
		refreshPercentages();
		return percentage.getCombined() * 100;
	}

	public String getTemp() {
		switch (Platform.osFamily) {
		case LIN:
			double[] coretemps = LinuxTemperature.query();
			double average = 0;
			for (double d : coretemps) {
				average += d;
			}
			average /= coretemps.length;

			return average + " C";

		default:
			return Native.getCpuTemp() + " C";

		}

	}

	public static class LinuxTemperature {

		private static ArrayList<RandomAccessFile> cores = new ArrayList<RandomAccessFile>();
		private static int maxCores = 512;

		public static double[] query() {

			if (cores.size() == 0) {

				File core = null;
				// get core hwmon directory
				for (File probe : new File("/sys/class/hwmon").listFiles()) {
					try {
						if (FileUtil.readFileString(new File(probe.getAbsolutePath() + "/name")).contains("coretemp")) {
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
