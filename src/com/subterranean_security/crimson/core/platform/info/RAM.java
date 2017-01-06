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

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.CUtil;

public final class RAM {
	private static final Logger log = LoggerFactory.getLogger(RAM.class);

	private RAM() {
	}

	/*
	 * SIGAR objects
	 */

	private static Mem mem;

	public static void initialize() {
		try {
			mem = SigarStore.getSigar().getMem();
		} catch (SigarException e) {
			log.error("Failed to collect memory information");
		}

	}

	public static void refreshMem() {
		try {
			mem.gather(SigarStore.getSigar());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Information retrieval
	 */

	// Try biosdecode/dmidecode
	public static String getFrequency() {
		return "N/A";
	}

	public static String getTemperature() {
		return "N/A";
	}

	public static String getSize() {
		return CUtil.UnitTranslator.translateMemSize(mem.getTotal());
	}

	public static String getUsage() {
		refreshMem();
		return CUtil.UnitTranslator.translateMemSize(mem.getActualUsed());
	}

	public static String getClientUsage() {
		CRIMSON.refreshProcessMem();
		return CUtil.UnitTranslator.translateMemSize(CRIMSON.getProcessMem().getResident());
	}

}
