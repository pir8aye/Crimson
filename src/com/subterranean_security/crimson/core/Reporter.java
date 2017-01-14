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

import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.JAVA;
import com.subterranean_security.crimson.core.platform.info.OS;
import com.subterranean_security.crimson.core.proto.Report.MI_Report;
import com.subterranean_security.crimson.core.util.DateUtil;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.services.Services;

/**
 * 
 * Securely sends an error report to Subterranean Security using the proprietary
 * Services library
 * 
 * @author Tyler Cook
 *
 */
public final class Reporter {
	private Reporter() {
	}

	private static MI_Report last = null;

	public static void report(MI_Report r) {

		// ignore duplicates
		if (last != null && last.getCrStackTrace().equals(r.getCrStackTrace())) {
			return;
		} else {
			last = r;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Services.sendReport(r);
				} catch (Exception e) {
					// ignore to prevent amplification
				}
			}
		}).start();
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
		try {
			rb.setCrVersion(Common.version);
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson version: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setCrBaseDir(Common.Directories.base.getAbsolutePath());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson base directory: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setCrBuild("" + Common.build);
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson build number: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setJreVersion(JAVA.getVersion());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java version: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setJreVendor(JAVA.getVendor());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java vendor: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setCrInstance(Common.instance.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Crimson instance: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setSysArch(OS.getArch());
		} catch (Exception e) {
			rb.setCrComment("Failed to query system architecture: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setJreArch(Platform.javaArch.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java architecture: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setJreUptime(DateUtil.datediff(Common.start, new Date()));
		} catch (Exception e) {
			rb.setCrComment("Failed to query Java uptime: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setSysLang(OS.getLanguage());
		} catch (Exception e) {
			rb.setCrComment("Failed to query system language: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setOsName(OS.getName());
		} catch (Exception e) {
			rb.setCrComment("Failed to query operating system name: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setOsFamily(Platform.osFamily.toString());
		} catch (Exception e) {
			rb.setCrComment("Failed to query OS family: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setSysCpuModel(CPU.getPrimaryModel());
		} catch (Exception e) {
			rb.setCrComment("Failed to query CPU model: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setLogCrimson(FileUtil.readFileString(new File(Common.Directories.varLog.getAbsolutePath() + "/"
					+ Common.instance.toString().toLowerCase() + ".log")));
		} catch (Exception e) {
			rb.setCrComment("Failed to query instance log: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}
		try {
			rb.setLogNetty(
					FileUtil.readFileString(new File(Common.Directories.varLog.getAbsolutePath() + "/netty.log")));
		} catch (Exception e) {
			rb.setCrComment("Failed to query netty log: " + e.getMessage() + "\n"
					+ (rb.hasCrComment() ? rb.getCrComment() : ""));
		}

		return rb;
	}

}
