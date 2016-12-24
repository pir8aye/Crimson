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

import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.profile.group.GroupAttributeType;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Native;

public final class CPU {
	private CPU() {
	}

	public static String getClientUsage() {
		SigarStore.refreshProcessCpu();
		return String.format("%5.2f", SigarStore.getProcessCpu().getPercent() * 100);
	}

	public static int getCount() {
		return SigarStore.getCpuInfos().length;
	}

	public static String getPrimaryVendor() {
		if (SigarStore.getCpuInfos().length > 0) {
			return getVendor(0);
		} else {
			return "";
		}
	}

	public static String getVendor(int i) {
		return SigarStore.getCpuInfos()[i].getVendor();
	}

	public static String getPrimaryModel() {
		if (SigarStore.getCpuInfos().length > 0) {
			return getModel(0);
		} else {
			return "";
		}
	}

	public static String getModel(int i) {
		String model = SigarStore.getCpuInfos()[i].getModel().replaceAll("\\(.+?\\)", "");
		try {
			model = model.substring(0, model.indexOf("CPU @")).trim();
		} catch (Exception e) {
			// ignore
		}
		return model;
	}

	public static String getMaxFrequency(int i) {
		return CUtil.UnitTranslator.translateCpuFrequency(SigarStore.getCpuInfos()[i].getMhz());
	}

	public static String getCores(int i) {
		return "" + SigarStore.getCpuInfos()[i].getTotalCores();
	}

	public static String getCache(int i) {
		return CUtil.UnitTranslator.translateCacheSize(SigarStore.getCpuInfos()[i].getCacheSize());
	}

	public static String getTotalUsage(int i) {
		SigarStore.refreshCpuPerc();
		return String.format("%5.2f", SigarStore.getCpuPercs()[i].getCombined() * 100);
	}

	public static String computeGID(int i) {
		return GroupAttributeType.CPU.ordinal() + getModel(i);
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

	public static ArrayList<AttributeGroupContainer> getAttributes() {
		ArrayList<AttributeGroupContainer> attributes = new ArrayList<AttributeGroupContainer>();
		for (int i = 0; i < SigarStore.getCpuInfos().length; i++) {
			AttributeGroupContainer.Builder template = AttributeGroupContainer.newBuilder()
					.setGroupType(GroupAttributeType.CPU.ordinal()).setGroupId(computeGID(i));

			attributes.add(
					template.setAttributeType(AttributeGroupType.CPU_CORES.ordinal()).setValue(getCores(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.CPU_MODEL.ordinal()).setValue(getModel(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.CPU_VENDOR.ordinal()).setValue(getVendor(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.CPU_CACHE.ordinal()).setValue(getCache(i)).build());
			attributes.add(template.setAttributeType(AttributeGroupType.CPU_FREQUENCY_MAX.ordinal())
					.setValue(getMaxFrequency(i)).build());
		}
		return attributes;
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
