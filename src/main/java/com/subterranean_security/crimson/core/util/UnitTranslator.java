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
package com.subterranean_security.crimson.core.util;

public class UnitTranslator {

	public static String familiarize(long size, String[] units) {
		int measureQuantity = 1024;

		if (size <= 0) {
			return null;
		}

		if (size < measureQuantity) {
			return size + units[0];
		}

		int i = 1;
		double d = size;
		while ((d = d / measureQuantity) > (measureQuantity - 1)) {
			i++;
		}

		long l = (long) (d * 100);
		d = (double) l / 100;

		if (i < units.length) {
			return d + units[i];
		}
		return String.valueOf(size);
	}

	public static long defamiliarize(String s, String[] units) {
		for (int i = 0; i < units.length; i++) {
			if (s.toLowerCase().endsWith(units[i].toLowerCase())) {
				return (long) (Double.parseDouble(s.substring(0, s.indexOf(' '))) * (Math.pow(1024, i)));
			}
		}
		return Long.parseLong(s.substring(0, s.indexOf(' ')));
	}

	private static double parseNumeric(String s) throws NumberFormatException, IndexOutOfBoundsException {
		return Double.parseDouble(s.substring(0, s.indexOf(' ')));
	}

	private static long parseUnitMultiplier(String s) {
		switch (s.substring(1 + s.lastIndexOf(' ')).toLowerCase()) {
		case "b":
		case "b/s":
		case "hz":
			return 1L;

		case "kb":
		case "kb/s":
		case "khz":
			return 1000L;
		case "kib":
		case "kib/s":
			return 1024L;

		case "mb":
		case "mb/s":
		case "mhz":
			return 1000000L;
		case "mib":
		case "mib/s":
			return 1048576L;

		case "gb":
		case "gb/s":
		case "ghz":
			return 1000000000L;
		case "gib":
		case "gib/s":
			return 1073741824L;

		case "tb":
		case "tb/s":
		case "thz":
			return 1000000000000L;
		case "tib":
		case "tib/s":
			return 1099511627776L;

		}
		return 0;
	}

	public static String translateNicOutput(long l) {
		if (l < 1024) {
			return String.format("%.2f  B", l / 1.0);
		} else if (l < 1048576) {
			return String.format("%.2f KB", l / 1024.0);
		} else if (l < 1073741824) {
			return String.format("%.2f MB", l / (1048576.0));
		} else if (l < 1099511627776L) {
			return String.format("%.2f GB", l / (1073741824.0));
		} else {
			return String.format("%.2f TB", l / (1099511627776L));
		}
	}

	public static double nicSpeed(String l) {
		double d = 0;

		// parse numeric
		try {
			d = parseNumeric(l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// parse unit
		long multiplier = 0;

		try {
			multiplier = parseUnitMultiplier(l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return d * multiplier;
	}

	public static String nicSpeed(double l) {
		if (l < 1024) {
			return String.format("%.2f   B/s", l / 1.0);
		} else if (l < 1048576) {
			return String.format("%.2f KiB/s", l / 1024.0);
		} else if (l < 1073741824) {
			return String.format("%.2f MiB/s", l / (1048576.0));
		} else {
			return String.format("%.2f GiB/s", l / (1073741824.0));
		}
	}

	public static String translateDispMemSize(int l) {
		if (l < 1024) {
			return String.format("%.2f  B", l / 1.0);
		} else if (l < 1048576) {
			return String.format("%.2f KB", l / 1024.0);
		} else if (l < 1073741824) {
			return String.format("%.2f MB", l / (1048576.0));
		} else {
			return String.format("%.2f GB", l / (1073741824.0));
		}
	}

	public static String translateMemSize(long l) {
		if (l < 1024 * 1024) {
			return String.format("%.2f KB", l / 1024.0);
		} else if (l < 1024 * 1024 * 1024) {
			return String.format("%.2f MB", l / (1024.0 * 1024.0));
		} else {
			return String.format("%.2f GB", l / (1024.0 * 1024.0 * 1024.0));
		}
	}

	public static String translateCacheSize(long l) {
		if (l < 1024 * 1024) {
			return String.format("%f KB", l / 1024.0);
		} else {
			return String.format("%f MB", l / (1024.0 * 1024.0));
		}
	}

	public static String translateCpuFrequency(int l) {
		if (l < 1000) {
			return String.format("%.2f MHz", l);
		} else {
			return String.format("%.2f GHz", l / 1000.0);
		}
	}

	public static final String[] BYTES = { " B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB" };
	public static final String[] BYTES_PER_SECOND = { " B/s", " KB/s", " MB/s", " GB/s", " TB/s", " PB/s", " EB/s",
			" ZB/s", " YB/s" };

}