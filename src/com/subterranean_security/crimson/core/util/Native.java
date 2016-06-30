package com.subterranean_security.crimson.core.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;

public final class Native {

	private static final Logger log = LoggerFactory.getLogger(Native.class);

	private Native() {
	}

	public static native String getActiveWindow();

	public static native long getSystemUptime();

	public static native long getCpuTemp();

	public static native void poweroff();

	public static native void restart();

	public static native void standby();

	public static native void hibernate();

	public static native String execute(String cmd);

	public static class Loader {
		public static void load() {
			loadJDBC();
			loadLapis();
			Platform.Advanced.loadSigar();
		}

		public static boolean loadLapis() {
			File lib = new File(Common.Directories.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString()
					+ "/" + Platform.osFamily.getLapisName(Platform.javaArch));
			log.debug("Loading LAPIS native library: {}", lib.getName());

			try {
				System.load(lib.getAbsolutePath());
			} catch (Throwable e) {
				log.error("Failed to load lapis!");
				e.printStackTrace();
				return false;
			}
			return true;
		}

		public static boolean loadJDBC() {
			File lib = new File(Common.Directories.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString()
					+ "/" + Platform.osFamily.getJDBCName(Platform.javaArch));
			log.debug("Loading JDBC native library: {}", lib.getName());

			try {
				System.load(lib.getAbsolutePath());

			} catch (Throwable e) {
				log.error("Failed to load jdbc!");
				e.printStackTrace();
				return false;
			}
			return true;
		}

		public static boolean loadJDBCTemporarily(File temp) {

			File lib = new File(temp.getAbsolutePath() + "/jni/" + Platform.osFamily.toString() + "/"
					+ Platform.osFamily.getJDBCName(Platform.javaArch));
			log.debug("Temporarily loading native library: ", lib.getName());

			try {
				System.load(lib.getAbsolutePath());

			} catch (Throwable e) {
				log.error("Failed to load!");
				e.printStackTrace();
				return false;
			}
			return true;

		}
	}

}
