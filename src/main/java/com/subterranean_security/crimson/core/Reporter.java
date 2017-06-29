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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.JAVA;
import com.subterranean_security.crimson.core.platform.info.OS;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedList;
import com.subterranean_security.crimson.core.util.DateUtil;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Report.MI_Report;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

/**
 * Securely send an error report to Subterranean Security.
 * 
 * @author cilki
 * @since 3.0.0
 */
public final class Reporter {
	private static final Logger log = LoggerFactory.getLogger(Reporter.class);

	private Reporter() {
	}

	private static MI_Report last;

	public static void report(MI_Report report) {
		if (report == null)
			throw new IllegalArgumentException();

		// ignore duplicates
		if (last != null && last.getCrStackTrace().equals(report.getCrStackTrace())) {
			return;
		} else {
			last = report;
		}

		new Thread(() -> {
			try {
				if (!sendReport(report)) {
					// buffer the report
					if (Universal.instance != Instance.INSTALLER) {
						log.debug("Buffering report");
						try {
							((CachedList<MI_Report>) DatabaseStore.getDatabase().getCachedCollection("reports.buffer"))
									.add(report);
						} catch (Throwable e) {
							log.warn("Failed to buffer unsent report");
						}
					}
				}
			} catch (Exception e) {
				// ignore to prevent amplification
			}
		}).start();
	}

	public static void flushBuffer() {
		if (Universal.instance == Instance.INSTALLER) {
			return;
		}

		try {
			List<MI_Report> buffer = (CachedList<MI_Report>) DatabaseStore.getDatabase()
					.getCachedCollection("reports.buffer");

			for (Iterator<MI_Report> it = buffer.iterator(); it.hasNext();) {
				sendReport(it.next());
				it.remove();
			}
		} catch (Throwable e) {
			// ignore
		}

	}

	public static boolean sendReport(MI_Report report) {
		if (ConnectionStore.connectViridian()) {
			NetworkStore.route(Message.newBuilder()
					.setRid(com.subterranean_security.crimson.core.util.IDGen.Reserved.VIRIDIAN).setMiReport(report));
			return true;
		}
		return false;

	}

	/**
	 * 
	 * Gathers system info in a fail-safe manner to prevent error report
	 * amplification
	 * 
	 */
	public static MI_Report.Builder newReport() {
		MI_Report.Builder rb = MI_Report.newBuilder();
		rb.setInitDate(new Date().getTime());

		// Crimson version
		try {
			rb.setCrVersion(Universal.version);
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson version: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// build number
		try {
			rb.setCrBuild("" + Universal.build);
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson build number: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Crimson install directory
		try {
			rb.setCrBaseDir(Environment.base.getAbsolutePath());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson base directory: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Java version
		try {
			rb.setJreVersion(JAVA.getVersion());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java version: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Java vendor
		try {
			rb.setJreVendor(JAVA.getVendor());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java vendor: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Crimson instance
		try {
			rb.setCrInstance(Universal.instance.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson instance: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// System architecture
		try {
			rb.setSysArch(OS.getArch());
		} catch (Exception e) {
			rb.setCrComment("Failed to query system architecture: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Java architecture
		try {
			rb.setJreArch(Platform.javaArch.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java architecture: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// Java uptime
		try {
			rb.setJreUptime(DateUtil.timeBetween(Universal.start, new Date()));
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java uptime: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// System language
		try {
			rb.setSysLang(OS.getLanguage());
		} catch (Exception e) {
			rb.setCrComment("Failed to query system language: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// OS name
		try {
			rb.setOsName(OS.getName());
		} catch (Exception e) {
			rb.setCrComment("Failed to query operating system name: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// OS family
		try {
			rb.setOsFamily(Platform.osFamily.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query OS family: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		// CPU model
		try {
			rb.setSysCpuModel(CPU.getPrimaryModel());
		} catch (Exception e) {
			rb.setCrComment("Failed to query CPU model: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setLogCrimson(FileUtil.readFileString(new File(
					Environment.log.getAbsolutePath() + "/" + Universal.instance.toString().toLowerCase() + ".log")));
		} catch (Exception e) {
			rb.setCrComment("Failed to query instance log: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setLogNetty(FileUtil.readFileString(new File(Environment.log.getAbsolutePath() + "/netty.log")));
		} catch (Exception e) {
			rb.setCrComment("Failed to query netty log: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		return rb;
	}

}
