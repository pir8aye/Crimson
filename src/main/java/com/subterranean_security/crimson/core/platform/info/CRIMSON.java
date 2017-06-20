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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class CRIMSON {
	private static final Logger log = LoggerFactory.getLogger(CRIMSON.class);

	private CRIMSON() {
	}

	/*
	 * SIGAR objects
	 */

	private static long pID = 0;
	private static ProcMem processMem;
	private static ProcCpu processCpu;

	public static void initialize() {

		pID = SigarStore.getSigar().getPid();
		try {
			processMem = SigarStore.getSigar().getProcMem(pID);
		} catch (SigarException e1) {
			log.error("Failed to obtain process memory collector");
		}

		try {
			processCpu = SigarStore.getSigar().getProcCpu(pID);
		} catch (SigarException e) {
			log.error("Failed to obtain process CPU collector");
		}
	}

	public static long getPID() {
		return pID;
	}

	public static void refreshProcessMem() {
		try {
			processMem.gather(SigarStore.getSigar(), pID);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void refreshProcessCpu() {
		try {
			processCpu.gather(SigarStore.getSigar(), pID);
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

	/*
	 * Crimson status
	 */

	private static ArrayList<String> status = new ArrayList<String>();

	public static String getStatus() {
		if (status.size() > 0) {
			String stat = "";
			for (String s : status) {
				stat += ";" + s;
			}
			return stat.substring(1);
		} else {
			return "IDLE";
		}
	}

	public static void addStatus(String s) {
		status.add(s);
	}

	public static void cancelStatus(String s) {
		Iterator<String> it = status.iterator();
		while (it.hasNext()) {
			if (it.next().equals(s)) {
				it.remove();
			}
		}
	}

	/*
	 * Information retrieval
	 */

	public static String getBasePath() {
		try {
			return CRIMSON.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			return "N/A";
		}
	}

	public static String getInstallDate() {
		try {
			return new Date(DatabaseStore.getDatabase().getLong("install.timestamp")).toString();
		} catch (Exception e) {
			return "N/A";
		}
	}

}
