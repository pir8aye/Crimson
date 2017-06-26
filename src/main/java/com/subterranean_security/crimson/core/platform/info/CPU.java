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

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_CPU;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;

public final class CPU {
	private static final Logger log = LoggerFactory.getLogger(CPU.class);

	private CPU() {
	}

	/*
	 * SIGAR objects
	 */

	private static Cpu timing;
	private static CpuInfo[] general;
	private static CpuPerc[] percentage;

	public static void initialize() {

		try {
			timing = SigarStore.getSigar().getCpu();
		} catch (SigarException e) {
			log.error("Failed to collect CPU timing information");
		}

		try {
			general = SigarStore.getSigar().getCpuInfoList();
		} catch (SigarException e) {
			log.error("Failed to collect general CPU information");
		}

		try {
			percentage = SigarStore.getSigar().getCpuPercList();
		} catch (SigarException e) {
			log.error("Failed to collect CPU percentage information");
		}
	}

	public static void refreshPercentages() {
		try {
			percentage = SigarStore.getSigar().getCpuPercList();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Information retrieval
	 */

	public static String getClientUsage() {
		CRIMSON.refreshProcessCpu();
		return String.format("%5.2f", CRIMSON.getProcessCpu().getPercent() * 100);
	}

	public static int getCount() {
		return general.length;
	}

	public static String getPrimaryVendor() {
		if (general.length > 0) {
			return getVendor(0);
		} else {
			return "";
		}
	}

	public static String getVendor(int i) {
		return general[i].getVendor();
	}

	public static String getPrimaryModel() {
		if (general.length > 0) {
			return getModel(0);
		} else {
			return "";
		}
	}

	public static String getModel(int i) {
		String model = general[i].getModel().replaceAll("\\(.+?\\)", "");
		try {
			model = model.substring(0, model.indexOf("CPU @")).trim();
		} catch (Exception e) {
			// ignore
		}
		return model;
	}

	public static String getMaxFrequency(int i) {
		return UnitTranslator.translateCpuFrequency(general[i].getMhz());
	}

	public static String getCores(int i) {
		return "" + general[i].getTotalCores();
	}

	public static String getCache(int i) {
		return UnitTranslator.translateCacheSize(general[i].getCacheSize());
	}

	public static String getTotalUsage(int i) {
		refreshPercentages();
		return String.format("%5.2f", percentage[i].getCombined() * 100);
	}

	public static String computeGID(int i) {
		return AttributeKey.Type.CPU.ordinal() + getModel(i);
	}

	public static String getTemp() {
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

	public static String get(AK_CPU key, int i) {
		switch (key) {
		case CACHE:
			return getCache(i);
		case CORES:
			return getCores(i);
		case FREQUENCY:
			return null;
		case FREQUENCY_MAX:
			return getMaxFrequency(i);
		case MODEL:
			return getModel(i);
		case TEMP:
			return getTemp();
		case TOTAL_USAGE:
			return getTotalUsage(i);
		case VENDOR:
			return getVendor(i);
		default:
			break;

		}
		return null;
	}

	public static ArrayList<AttributeGroupContainer> getAttributes() {
		ArrayList<AttributeGroupContainer> a = new ArrayList<AttributeGroupContainer>();
		for (int i = 0; i < general.length; i++) {
			AttributeGroupContainer.Builder container = AttributeGroupContainer.newBuilder()
					.setGroupType(AttributeKey.Type.CPU.ordinal()).setGroupId(computeGID(i));

			container.putAttribute(AK_CPU.CORES.ordinal(), getCores(i));
			container.putAttribute(AK_CPU.MODEL.ordinal(), getModel(i));
			container.putAttribute(AK_CPU.VENDOR.ordinal(), getVendor(i));
			container.putAttribute(AK_CPU.CACHE.ordinal(), getCache(i));
			container.putAttribute(AK_CPU.FREQUENCY_MAX.ordinal(), getMaxFrequency(i));

			a.add(container.build());
		}
		return a;
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
