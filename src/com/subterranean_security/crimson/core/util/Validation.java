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
package com.subterranean_security.crimson.core.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JPasswordField;

public class Validation {

	private static final Pattern valid_dns = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

	// TODO define valid username characters
	private static final Pattern valid_user = Pattern.compile("");

	private static final Pattern valid_ipv4 = Pattern
			.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private static final Pattern valid_private_ipv4 = Pattern
			.compile("(^127\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

	private static final Pattern valid_email = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	public static boolean username(String user) {
		if (user.length() < 4 || user.length() > 60) {
			return false;
		}

		return !valid_user.matcher(user).matches();
	}

	public static boolean groupname(String group) {
		return true;
	}

	public static boolean password(JPasswordField field) {
		char[] password = field.getPassword();
		boolean outcome = true;
		if (password.length < 4 || password.length > 64) {
			outcome = false;
		}
		RandomUtil.clearChar(password);
		return outcome;
	}

	public static boolean dns(String dns) {
		if (dns == null) {
			return false;
		}
		return valid_dns.matcher(dns).find();
	}

	public static boolean ip(String ip) {
		if (ip == null) {
			return false;
		}
		return valid_ipv4.matcher(ip).matches();
	}

	public static boolean privateIP(String ip) {
		return valid_private_ipv4.matcher(ip).find();
	}

	public static boolean port(String port) {
		try {
			int p = Integer.parseInt(port);
			return (p > 0 && p < 65536);
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean path(String path) {
		try {
			new File(path).getCanonicalPath();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public static boolean serial(String key) {
		if (key.length() != 16) {
			return false;
		}

		if (!key.matches("^[A-Z0-9]*$")) {
			return false;
		}

		return true;
	}

	public static boolean email(String email) {
		return valid_email.matcher(email).matches();
	}

	public static boolean flushValue(String value) {
		try {
			return (Integer.parseInt(value) > 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}

}