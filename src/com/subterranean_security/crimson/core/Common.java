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
import org.slf4j.LoggerFactory;

import com.subterranean_security.cinstaller.Main;
import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.storage.Database;
import com.subterranean_security.crimson.core.util.JarUtil;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.viewer.ViewerStore;

public final class Common {

	private Common() {
	}

	public enum Instance {
		SERVER, CLIENT, VIEWER, INSTALLER, VIRIDIAN;

		public String getLabel() {
			switch (this) {
			case CLIENT:
				return "C";
			case INSTALLER:
				return "I";
			case SERVER:
				return "S";
			case VIEWER:
				return "V";
			case VIRIDIAN:
				return "Q";
			default:
				return null;

			}
		}
	}

	/**
	 * Identifies this instance based on available packages
	 */
	public static final Instance instance = discoverInstance();

	private static Instance discoverInstance() {

		if (JarUtil.classExists("com.subterranean_security.crimson.server.Server")) {
			return Instance.SERVER;
		}
		if (JarUtil.classExists("com.subterranean_security.crimson.viewer.Viewer")) {
			return Instance.VIEWER;
		}
		if (JarUtil.classExists("com.subterranean_security.crimson.client.Client")) {
			return Instance.CLIENT;
		}
		if (JarUtil.classExists("com.subterranean_security.cinstaller.Main")) {
			return Instance.INSTALLER;
		}
		if (JarUtil.classExists("com.subterranean_security.viridian.Main")) {
			return Instance.VIRIDIAN;
		}
		System.exit(0);
		return null;
	}

	/**
	 * Initialization Timestamp
	 */
	public static final Date start = new Date();

	/**
	 * When true, debug messages will be logged and additional functionality
	 * enabled
	 */
	private static boolean debug = false;

	public static void setDebug(boolean d) {
		debug = d;

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

	public static int build;

	static {

		try {
			version = JarUtil.getManifestValue("Crimson-Version");
			build = Integer.parseInt(JarUtil.getManifestValue("Build-Number"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static Database getInstanceDatabase() {
		switch (Common.instance) {
		case CLIENT:
			return Client.clientDB;
		case SERVER:
			return ServerStore.Databases.system;
		case VIEWER:
			return ViewerStore.Databases.local;

		default:
			return null;

		}
	}

	public enum Directories {
		;

		private static final Logger log = LoggerFactory.getLogger(Directories.class);

		/**
		 * Base contains binaries and configuration files
		 */
		public static final File base = discoverBaseDir();

		/**
		 * Temporary files
		 */
		public static final File tmp = discoverTmpDir();

		/**
		 * Var contains user and system databases
		 */
		public static final File var = discoverVarDir();

		/**
		 * Log files
		 */
		public static final File varLog = discoverLogDir();

		static {
			if (!varLog.exists()) {
				varLog.mkdirs();
			}
			if (Common.instance != Instance.INSTALLER) {
				if ((!base.canRead() || !base.canWrite())) {
					log.error("Fatal Error: " + base.getAbsolutePath() + " is not readable and/or writable");

				}

			}

		}

		private static File discoverBaseDir() {
			if (Common.instance == Instance.INSTALLER) {
				return null;
			}

			try {
				String bpath = Common.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
				File f = new File(bpath).getParentFile();
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
			if (Common.instance == Instance.INSTALLER) {
				return null;
			}
			return new File(System.getProperty("java.io.tmpdir"));
		}

		private static File discoverVarDir() {
			if (Common.instance == Instance.INSTALLER) {
				return null;
			}
			switch (Platform.osFamily) {
			case WIN:
				return new File(System.getProperty("user.home") + "/AppData/Local/Subterranean Security/Crimson/var");
			default:
				return new File(System.getProperty("user.home") + "/.crimson/var");

			}

		}

		private static File discoverLogDir() {
			if (Common.instance == Instance.INSTALLER) {
				return new File(Main.temp.getAbsolutePath() + "/log");
			}
			if (Common.instance == Instance.VIRIDIAN) {
				return new File("/var/log/viridian");
			}
			return new File(var.getAbsolutePath() + "/log");
		}

	}

}
