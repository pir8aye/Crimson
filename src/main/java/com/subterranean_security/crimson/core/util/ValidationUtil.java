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
import java.util.regex.Pattern;

import javax.swing.JPasswordField;

public final class ValidationUtil {
	private ValidationUtil() {
	}

	private static final Pattern PATTERN_VALID_USER = Pattern.compile("^[a-zA-Z0-9]*$");

	/**
	 * Validate a user name
	 * 
	 * @param user
	 * @return True if user is a valid username
	 */
	public static boolean username(String user) {
		if (user.length() < 4 || user.length() > 60) {
			return false;
		}

		return PATTERN_VALID_USER.matcher(user).matches();
	}

	private static final Pattern PATTERN_VALID_GROUP = Pattern.compile("^[a-zA-Z0-9 ]*$");

	/**
	 * Validate a group name
	 * 
	 * @param group
	 * @return True if group is a valid group name
	 */
	public static boolean group(String group) {
		if (group.length() < 4 || group.length() > 60) {
			return false;
		}

		return PATTERN_VALID_GROUP.matcher(group).matches();
	}

	/**
	 * Validate a password
	 * 
	 * @param field
	 * @return True if the given JPasswordField contains a valid password.
	 */
	public static boolean password(JPasswordField field) {
		char[] password = field.getPassword();
		boolean outcome = true;
		if (password.length < 4 || password.length > 64) {
			outcome = false;
		}
		RandomUtil.clearChar(password);
		return outcome;
	}

	private static final Pattern PATTERN_VALID_DNS = Pattern
			.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

	/**
	 * Validate a DNS name
	 * 
	 * @param dns
	 * @return True if dns is a valid DNS name
	 */
	public static boolean dns(String dns) {
		if (dns == null) {
			return false;
		}
		return PATTERN_VALID_DNS.matcher(dns).find();
	}

	private static final Pattern PATTERN_VALID_IPV4 = Pattern
			.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	/**
	 * Validate an IP
	 * 
	 * @param ip
	 * @return True if ip is a valid IP address
	 */
	public static boolean ipv4(String ip) {
		if (ip == null) {
			return false;
		}
		return PATTERN_VALID_IPV4.matcher(ip).matches();
	}

	private static final Pattern PATTERN_VALID_PRIVATE_IPV4 = Pattern
			.compile("(^127\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

	/**
	 * Validate a private IP
	 * 
	 * @param ip
	 * @return True if ip is a valid private IP address
	 */
	public static boolean privateIP(String ip) {
		return ipv4(ip) && PATTERN_VALID_PRIVATE_IPV4.matcher(ip).find();
	}

	/**
	 * Validate a port number
	 * 
	 * @param port
	 * @return True if port is an integer and a valid port number
	 */
	public static boolean port(String port) {
		try {
			return port(Integer.parseInt(port));
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * Validate a port number
	 * 
	 * @param port
	 * @return True if port is within range
	 */
	public static boolean port(int port) {
		return (port > 0 && port < 65536);
	}

	/**
	 * Validate a filesystem path
	 * 
	 * @param path
	 * @return True if path is a valid path
	 */
	public static boolean path(String path) {
		try {
			new File(path).getCanonicalPath();
		} catch (Throwable e) {
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

	private static final Pattern PATTERN_VALID_EMAIL = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	/**
	 * Validate an email address
	 * 
	 * @param email
	 * @return True if email is a valid email
	 */
	public static boolean email(String email) {
		return PATTERN_VALID_EMAIL.matcher(email).matches();
	}

	/**
	 * Validate a keylogger flush value. This is either a number of events or a
	 * period.
	 * 
	 * @param value
	 * @return True if value is a valid keylogger flush value
	 */
	public static boolean keyloggerFlushNumber(String value) {
		try {
			return (Integer.parseInt(value) > 0);
		} catch (Throwable e) {
			return false;
		}
	}

}