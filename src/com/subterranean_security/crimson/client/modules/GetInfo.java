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
package com.subterranean_security.crimson.client.modules;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.net.Delta.ProfileDelta_EV;

public enum GetInfo {

	;

	public static ProfileDelta_EV getStatic() {
		ProfileDelta_EV.Builder info = ProfileDelta_EV.newBuilder();

		// TODO get the right one from the database
		info.setClientid(0);

		try {
			info.setNetHostname(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			info.setNetHostname("unknown");
		}

		info.setCrimsonVersion(Common.version);

		info.setUserName(System.getProperty("user.name"));
		info.setUserDir(System.getProperty("user.dir"));
		info.setUserHome(System.getProperty("user.home"));
		info.setJavaVersion(System.getProperty("java.version"));
		info.setJavaVendor(System.getProperty("java.vendor"));
		info.setJavaHome(System.getProperty("java.home"));
		info.setJavaArch(System.getProperty("os.arch"));

		return info.build();
	}

	public HashMap<String, Object> getDynamic() {
		HashMap<String, Object> info = new HashMap<String, Object>();
		// TODO dont do this
		// info.put("java.uptime",
		// ManagementFactory.getRuntimeMXBean().getUptime());

		return info;
	}

}
