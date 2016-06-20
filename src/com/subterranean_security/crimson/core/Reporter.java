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
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Report.MI_Report;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.services.Services;

/**
 * @author subterranean Sends a report using the Services library
 *
 */
public class Reporter {

	private static final Logger log = LoggerFactory.getLogger(Reporter.class);

	public static void report(final MI_Report r) {
		// add report to buffer TODO
		new Thread(new Runnable() {

			@Override
			public void run() {
				Services.sendReport(r);
			}

		}).start();

	}

	public static MI_Report.Builder newReport() {
		MI_Report.Builder rb = MI_Report.newBuilder();
		rb.setInitDate(new Date().getTime());
		rb.setCrVersion(Common.version);
		rb.setCrBuild("" + Common.build);
		rb.setJreVersion(Platform.Advanced.getJavaVersion());
		rb.setInstance(Common.instance.toString());
		rb.setOsFamily(Platform.osFamily.toString());
		// rb.setSysArch(Platform.sysArch.toString());
		rb.setJreArch(Platform.javaArch.toString());
		rb.setJreUptime(CUtil.Misc.datediff(Common.start, new Date()));
		rb.setSysLang(Platform.Advanced.getLanguage());
		rb.setOsName(Platform.osName);
		try {
			rb.setCrLog(CUtil.Files.readFileString(new File(Common.Directories.varLog.getAbsolutePath() + "/"
					+ Common.instance.toString().toLowerCase() + ".log")));
		} catch (IOException e) {

		}
		try {
			rb.setNettyLog(
					CUtil.Files.readFileString(new File(Common.Directories.varLog.getAbsolutePath() + "/netty.log")));
		} catch (IOException e) {

		}

		return rb;
	}

}
