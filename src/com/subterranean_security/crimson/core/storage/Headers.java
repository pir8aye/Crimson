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
package com.subterranean_security.crimson.core.storage;

public enum Headers {
	COUNTRY, LANGUAGE, CRIMSON_VERSION, JAVA_VERSION, TIMEZONE, HOSTNAME, USERNAME, INTERNAL_IP, EXTERNAL_IP, OS_FAMILY, OS_ARCH, RAM_CAPACITY, USER_STATUS, MESSAGE_PING, RAM_USAGE, CPU_TEMP, CPU_MODEL, CPU_USAGE, ACTIVE_WINDOW, SCREEN_PREVIEW, VIRTUALIZATION, MONITOR_COUNT;

	@Override
	public String toString() {
		switch (this) {
		case ACTIVE_WINDOW:
			return "Active Window";
		case COUNTRY:
			return "Country";
		case CPU_MODEL:
			return "CPU Model";
		case CPU_TEMP:
			return "CPU Temperature";
		case CPU_USAGE:
			return "CPU Usage";
		case CRIMSON_VERSION:
			return "Crimson Version";
		case EXTERNAL_IP:
			return "External IP";
		case HOSTNAME:
			return "Hostname";
		case INTERNAL_IP:
			return "Internal IP";
		case JAVA_VERSION:
			return "Java Version";
		case LANGUAGE:
			return "Language";
		case MESSAGE_PING:
			return "Message Ping";
		case MONITOR_COUNT:
			return "Monitor Count";
		case OS_ARCH:
			return "OS Architecture";
		case OS_FAMILY:
			return "OS Family";
		case RAM_CAPACITY:
			return "RAM Capacity";
		case RAM_USAGE:
			return "RAM Usage";
		case SCREEN_PREVIEW:
			return "Screen Preview";
		case TIMEZONE:
			return "Timezone";
		case USERNAME:
			return "Username";
		case USER_STATUS:
			return "User Status";
		case VIRTUALIZATION:
			return "Virtualization";

		}
		return super.toString();
	}
}
