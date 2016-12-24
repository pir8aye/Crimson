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

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.CUtil;

public final class Net {

	private Net() {
	}

	public static String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "unknown";
		}
	}

	public static String getFQDN() {
		return SigarStore.getNetInfo().getDomainName();
	}

	public static String getDNS1() {
		return SigarStore.getNetInfo().getPrimaryDns();
	}

	public static String getDNS2() {
		return SigarStore.getNetInfo().getSecondaryDns();
	}

	public static String getDefaultGateway() {
		return SigarStore.getNetInfo().getDefaultGateway();
	}

	public static String getExternalIP() {
		return CUtil.Network.getEIP();
	}

}
