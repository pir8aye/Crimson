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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.CUtil;

public final class NET {
	private static final Logger log = LoggerFactory.getLogger(NET.class);

	private NET() {
	}

	/*
	 * SIGAR objects
	 */

	private static NetInfo netInfo;

	public static void initialize() {
		try {
			netInfo = SigarStore.getSigar().getNetInfo();
		} catch (SigarException e1) {
			log.error("Failed to open collection on network information");
		}
	}

	public static void refreshNetInfo() {
		try {
			netInfo.gather(SigarStore.getSigar());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Information retrieval
	 */

	public static String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "unknown";
		}
	}

	public static String getFQDN() {
		return netInfo.getDomainName();
	}

	public static String getDNS1() {
		return netInfo.getPrimaryDns();
	}

	public static String getDNS2() {
		return netInfo.getSecondaryDns();
	}

	public static String getDefaultGateway() {
		return netInfo.getDefaultGateway();
	}

	public static String getExternalIP() {
		return CUtil.Network.getEIP();
	}

}
