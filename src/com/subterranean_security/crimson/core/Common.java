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
package com.subterranean_security.crimson.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import com.subterranean_security.crimson.core.utility.CUtil;

public enum Common {

	;

	/**
	 * When true, debug messages will be logged and additional functionality
	 * enabled
	 */
	public static final boolean debug = false;

	/**
	 * Version Syntax: X.X.X.X with major versions being on the left and minor
	 * versions and fixes on the right
	 */
	public static final String version = "1.0.0.0";

	/**
	 * Initialization Timestamp
	 */
	public static final Date start = new Date();

	public static final Instance instance = discoverInstance();

	public static final File base = discoverBaseDir();
	public static final File log = new File(base.getAbsolutePath() + "/log/crimson.log");
	public static final File tmp = new File(base.getAbsolutePath() + "/tmp");
	public static final File var = new File(base.getAbsolutePath() + "/var");
	public static final File gtmp = new File(System.getProperty("java.io.tmpdir"));

	static {

		if ((!base.canRead() || !base.canWrite()) && instance != Instance.INSTALLER) {
			Logger.ferror("Fatal Error: " + base.getAbsolutePath() + " is not readable and/or writable");

		}

		if (new File(base.getAbsolutePath() + "/log").mkdirs()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO
			}
		}

		Logger.debug("Base directory: " + base.getAbsolutePath());
		Logger.debug("Log file: " + log.getAbsolutePath());
		Logger.debug("Temporary directory: " + tmp.getAbsolutePath());
	}

	public enum Instance {
		SERVER, CLIENT, VIEWER, INSTALLER, VIRIDIAN;
	}

	private static Instance discoverInstance() {

		if (CUtil.Misc.findClass("com.subterranean_security.crimson.server.Server")) {
			return Instance.SERVER;
		}
		if (CUtil.Misc.findClass("com.subterranean_security.crimson.viewer.Viewer")) {
			return Instance.VIEWER;
		}
		if (CUtil.Misc.findClass("com.subterranean_security.crimson.client.Client")) {
			return Instance.CLIENT;
		}
		if (CUtil.Misc.findClass("com.subterranean_security.cinstaller.Main")) {
			return Instance.INSTALLER;
		}
		if (CUtil.Misc.findClass("com.subterranean_security.viridian.Main")) {
			return Instance.VIRIDIAN;
		}
		Logger.info("Unknown Instance");
		return null;
	}

	private static File discoverBaseDir() {

		try {
			// the base will always be two dirs above the core library
			String bpath = Common.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

			return new File(bpath.substring(0, bpath.length() - 16));
		} catch (URISyntaxException e) {
			Logger.ferror("Null Base Directory");
			return null;
		}
	}

}
