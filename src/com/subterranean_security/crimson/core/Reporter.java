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

import java.util.Date;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.proto.Report.MI_Report;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.services.Services;

/**
 * @author subterranean Sends a report using the Services library
 *
 */
public class Reporter {

	private static final Logger log = CUtil.Logging.getLogger(Reporter.class);

	public static void report(final MI_Report r) {
		// add report to buffer TODO
		new Thread(new Runnable() {

			@Override
			public void run() {
				log.debug("Reporting event");
				System.out.println(r.getStackTrace());
				Services.sendReport(r);

			}

		}).start();

	}

	public static MI_Report.Builder newReport() {
		MI_Report.Builder rb = MI_Report.newBuilder();
		rb.setInitDate(new Date().getTime());
		rb.setCrVersion(Common.version);
		rb.setJreVersion(System.getProperty("java.version"));
		rb.setInstance(Common.instance.toString());
		rb.setOsFamily(Platform.os.toString());
		// rb.setSysArch(PlatformInfo.sysArch.toString());
		// rb.setJreArch(PlatformInfo.jreArch.toString());

		log.debug(
				"Generated base report with values: Initialization: {}, Crimson Version: {}, JRE Version: {}, Instance: {}, OS Family: {}",
				new Object[] { rb.getInitDate(), rb.getCrVersion(), rb.getJreVersion(), rb.getInstance(),
						rb.getOsFamily() });

		return rb;
	}

}
