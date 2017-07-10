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
package com.subterranean_security.crimson.core.platform.collect.singular;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.util.FileUtil;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class LIN {
	private LIN() {
	}

	private static final Logger log = LoggerFactory.getLogger(LIN.class);

	public static String getWM() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getTerminal() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getShell() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getKernel() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getDistro() {
		File os_release = new File("/etc/os-release");

		if (os_release.exists() && os_release.canRead()) {
			try {
				for (String s : FileUtil.readLines(os_release)) {
					s = s.trim();
					if (s.toUpperCase().startsWith("PRETTY_NAME")) {
						return s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
					}
				}
			} catch (IOException e) {
				log.warn("Failed to read os-release");
			}
		}

		File issue = new File("/etc/issue");

		if (issue.exists() && issue.canRead()) {
			try {
				for (String s : FileUtil.readLines(issue)) {
					return s.trim();
				}
			} catch (IOException e) {
				log.warn("Failed to read issue");
			}
		}

		return "Unknown Linux";
	}
}
