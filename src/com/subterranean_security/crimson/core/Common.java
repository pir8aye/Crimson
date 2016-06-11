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

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.util.CUtil;

public enum Common {
	;
	private static final Logger log = CUtil.Logging.getLogger(Common.class);

	/**
	 * When true, debug messages will be logged and additional functionality
	 * enabled
	 */
	private static boolean debug = false;

	public static void setDebug(boolean d) {
		debug = d;
		log.debug("Debug mode %s", (d ? "enabled" : "disabled"));

	}

	public static boolean isDebugMode() {
		return debug;
	}

	static {
		setDebug(new File("/debug.txt").exists());
	}

	public static int cvid = 0;

	/**
	 * Version Syntax: X.X.X.X with major versions being on the left and minor
	 * versions and fixes on the right
	 */
	public static String version;

	public static String build;

	/**
	 * Initialization Timestamp
	 */
	public static final Date start = new Date();

	public static final Instance instance = discoverInstance();

	public static final File base = discoverBaseDir();
	public static final File tmp = discoverTmpDir();
	public static final File var = discoverVarDir();

	static {

		try {
			version = CUtil.Misc.getManifestAttr("Crimson-Version");
			build = CUtil.Misc.getManifestAttr("Build-Number");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (instance != Instance.INSTALLER) {
			if ((!base.canRead() || !base.canWrite())) {
				log.error("Fatal Error: " + base.getAbsolutePath() + " is not readable and/or writable");

			}

			log.debug("Base directory: " + base.getAbsolutePath());
			log.debug("Temporary directory: " + tmp.getAbsolutePath());

		}

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
		log.error("Unknown Instance");
		return null;
	}

	private static File discoverBaseDir() {
		if (instance == Instance.INSTALLER) {
			return null;
		}

		try {
			// the base will always be two dirs above the core library
			String bpath = Common.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

			File f = new File(bpath.substring(0, bpath.length() - 16));
			if (!f.exists() || !f.isDirectory()) {
				log.error("Base directory does not exist: " + f.getAbsolutePath());
			}
			return f;
		} catch (URISyntaxException e) {
			log.error("Null Base Directory");
			return null;
		}
	}

	private static File discoverTmpDir() {
		if (instance == Instance.INSTALLER) {
			return null;
		}
		return new File(System.getProperty("java.io.tmpdir"));
	}

	private static File discoverVarDir() {
		if (instance == Instance.INSTALLER) {
			return null;
		}
		switch (Platform.osFamily) {
		case WIN:
			return new File(System.getProperty("user.home") + "/AppData/Local/Subterranean Security/Crimson/var");
		default:
			return new File(System.getProperty("user.home") +  "/.crimson/var");

		}

	}

}
