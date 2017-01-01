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
package com.subterranean_security.crimson.core.profile;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;

public enum SimpleAttribute implements AbstractAttribute {
	// Meta attributes
	META_FIRST_CONTACT,
	// Operating system attributes
	OS_LANGUAGE, OS_TIMEZONE, OS_START_TIME, OS_ARCH, OS_FAMILY, OS_NAME, OS_VIRTUALIZATION, OS_ACTIVE_WINDOW, OS_ENDIAN,
	// Motherboard attributes
	MOBO_MANUFACTURER, MOBO_MODEL, MOBO_TEMP,
	// RAM attributes
	RAM_SIZE, RAM_FREQUENCY, RAM_USAGE, RAM_TEMP,
	// Network attributes
	NET_HOSTNAME, NET_DNS1, NET_DNS2, NET_FQDN, NET_DEFAULT_GATEWAY, NET_EXTERNALIP,
	// IP location attributes
	IPLOC_LATITUDE, IPLOC_LONGITUDE, IPLOC_COUNTRY, IPLOC_COUNTRYCODE, IPLOC_REGION, IPLOC_CITY,
	// Java attributes
	JAVA_ARCH, JAVA_VERSION, JAVA_VENDOR, JAVA_PATH, JAVA_START_TIME,
	// User attributes
	USER_NAME, USER_HOME, USER_STATUS,
	// Client attributes
	CLIENT_CID, CLIENT_VERSION, CLIENT_INSTALL_DATE, CLIENT_BASE_PATH, CLIENT_STATUS, CLIENT_ONLINE, CLIENT_CPU_USAGE, CLIENT_RAM_USAGE,
	// Keylogger attributes
	KEYLOGGER_TRIGGER, KEYLOGGER_TRIGGER_VALUE, KEYLOGGER_STATE,
	// Linux specific attributes
	LINUX_WM, LINUX_DISTRO, LINUX_SHELL, LINUX_PACKAGES, LINUX_TERMINAL, LINUX_KERNEL,
	// Windows specific attributes
	WIN_IE_VERSION, WIN_POWERSHELL_VERSION, WIN_SERIAL, WIN_INSTALLDATE;

	public String toSuperString() {
		return super.toString();
	}

	@Override
	public String toString() {
		switch (this) {
		case OS_ACTIVE_WINDOW:
			return "Active Window";
		case IPLOC_COUNTRY:
			return "IP Location";
		case CLIENT_CID:
			return "Client ID";
		case CLIENT_VERSION:
			return "Crimson Version";
		case NET_EXTERNALIP:
			return "External IP";
		case NET_HOSTNAME:
			return "Hostname";
		case JAVA_VERSION:
			return "Java Version";
		case JAVA_ARCH:
			return "Java Architecture";
		case JAVA_PATH:
			return "Java Path";
		case JAVA_START_TIME:
			return "Java Start Time";
		case JAVA_VENDOR:
			return "Java Vendor";
		case RAM_FREQUENCY:
			return "RAM Frequency";
		case RAM_SIZE:
			return "RAM Size";
		case RAM_USAGE:
			return "RAM Used";
		case RAM_TEMP:
			return "RAM Temperature";
		case OS_LANGUAGE:
			return "Language";
		case OS_NAME:
			return "OS Name";
		case OS_ARCH:
			return "OS Architecture";
		case OS_FAMILY:
			return "OS Family";
		case OS_TIMEZONE:
			return "Timezone";
		case USER_NAME:
			return "Username";
		case USER_HOME:
			return "User Home";
		case USER_STATUS:
			return "User Status";
		case OS_VIRTUALIZATION:
			return "Virtualization";
		case WIN_IE_VERSION:
			return "Internet Explorer Version";
		case WIN_INSTALLDATE:
			return "Install Date";
		case WIN_POWERSHELL_VERSION:
			return "Powershell version";
		case WIN_SERIAL:
			return "Windows Serial Number";
		case LINUX_DISTRO:
			return "Linux Distribution";
		case LINUX_KERNEL:
			return "Kernel Version";
		case LINUX_PACKAGES:
			return "Packages";
		case LINUX_SHELL:
			return "Shell";
		case LINUX_WM:
			return "Window Manager/Desktop Environment";
		case LINUX_TERMINAL:
			return "Terminal";
		default:
			break;

		}
		return super.toString();
	}

	public boolean valid(OSFAMILY os, Instance instance) {
		switch (this) {
		case KEYLOGGER_STATE:
		case KEYLOGGER_TRIGGER:
		case KEYLOGGER_TRIGGER_VALUE:
			return instance == Instance.CLIENT;
		case LINUX_DISTRO:
		case LINUX_KERNEL:
		case LINUX_PACKAGES:
		case LINUX_SHELL:
		case LINUX_WM:
		case LINUX_TERMINAL:
			return os == OSFAMILY.LIN;
		case WIN_IE_VERSION:
		case WIN_INSTALLDATE:
		case WIN_POWERSHELL_VERSION:
		case WIN_SERIAL:
			return os == OSFAMILY.WIN;
		default:
			return true;
		}
	}

	public static final SimpleAttribute[] ordinal = SimpleAttribute.values();
}