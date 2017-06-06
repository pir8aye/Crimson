/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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

import java.io.File;

import com.subterranean_security.cinstaller.Main;
import com.subterranean_security.crimson.universal.Universal;

public final class Environment {
	private Environment() {
	}

	/**
	 * Directory containing the base installation (binaries, special
	 * dependancies, etc)
	 */
	public static final File base = discoverBase();

	/**
	 * Directory for variable user data
	 */
	public static final File var = discoverVar();

	/**
	 * Directory for temporary files
	 */
	public static final File tmp = discoverTmp();

	/**
	 * Directory for Crimson logs
	 */
	public static final File log = discoverLog();

	static {
		// test environment
		switch (Universal.instance) {
		case INSTALLER:
			// ignore
			break;
		default:
			// check base
			if (base == null || !base.exists() || !base.isDirectory()) {
				System.out.println("Fatal: base directory error");
				System.exit(1);
			}

			if (!var.exists())
				var.mkdirs();
			if (!tmp.exists())
				tmp.mkdirs();
			if (!log.mkdirs())
				log.mkdirs();
		}
	}

	/**
	 * Locate the base directory
	 * 
	 * @return A file representing the base directory
	 */
	private static File discoverBase() {
		switch (Universal.instance) {
		default:
			return Universal.jar.getParentFile();
		}
	}

	/**
	 * Locate the temporary directory
	 * 
	 * @return A file representing the temporary directory
	 */
	private static File discoverTmp() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Locate the log directory
	 * 
	 * @return A file representing the log directory
	 */
	private static File discoverLog() {
		switch (Universal.instance) {
		case INSTALLER:
			return new File(Main.temp.getAbsolutePath() + "/log");
		case VIRIDIAN:
			return new File("/var/log/viridian");
		default:
			return new File(var.getAbsolutePath() + "/log");
		}
	}

	/**
	 * Locate the var directory
	 * 
	 * @return A file representing the var directory
	 */
	private static File discoverVar() {
		switch (Universal.instance) {
		case CLIENT:
			return new File(base.getAbsolutePath() + "/var");
		default:
			switch (Platform.osFamily) {
			case WIN:
				return new File(System.getProperty("user.home") + "/AppData/Local/Subterranean Security/Crimson/var");
			default:
				return new File(base.getAbsolutePath() + "/var");
			}
		}
	}
}
