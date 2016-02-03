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

import com.subterranean_security.crimson.core.proto.msg.Reports.Report;
import com.subterranean_security.crimson.core.util.PlatformInfo;
import com.subterranean_security.services.Services;

public class Reporter {

	public static void report(final Report r) {
		// add report to buffer TODO
		new Thread(new Runnable() {

			@Override
			public void run() {
				Services.sendReport(r);

			}

		}).start();

	}

	public static Report.Builder newReport() {
		Report.Builder rb = Report.newBuilder();
		rb.setInitDate(new Date().getTime());
		rb.setCrVersion(Common.version);
		rb.setJreVersion(System.getProperty("java.version"));
		rb.setInstance(Common.instance.toString());
		rb.setOsFamily(PlatformInfo.os.toString());
		//rb.setSysArch(PlatformInfo.sysArch.toString());
		//rb.setJreArch(PlatformInfo.jreArch.toString());

		return rb;
	}

}
