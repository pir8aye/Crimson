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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.subterranean_security.crimson.client.Client;

public class CLIENT {
	private CLIENT() {
	}

	private static ArrayList<String> status = new ArrayList<String>();

	public static String getStatus() {
		if (status.size() > 0) {
			String stat = "";
			for (String s : status) {
				stat += ";" + s;
			}
			return stat.substring(1);
		} else {
			return "IDLE";
		}
	}

	public static void addStatus(String s) {
		status.add(s);
	}

	public static void cancelStatus(String s) {
		Iterator<String> it = status.iterator();
		while (it.hasNext()) {
			if (it.next().equals(s)) {
				it.remove();
			}
		}
	}

	public static String getBasePath() {
		try {
			return CLIENT.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			return "N/A";
		}
	}

	public static String getInstallDate() {
		try {
			return new Date(Client.clientDB.getLong("install.timestamp")).toString();
		} catch (Exception e) {
			return "N/A";
		}
	}

}
