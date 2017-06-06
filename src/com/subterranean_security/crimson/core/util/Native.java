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
package com.subterranean_security.crimson.core.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.proto.Stream.DirtyRect;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;
import com.subterranean_security.crimson.core.store.RemoteStore;

public final class Native {

	private static final Logger log = LoggerFactory.getLogger(Native.class);

	private Native() {
	}

	public static native String getActiveWindow();

	public static native long getCpuTemp();

	public static native void poweroff();

	public static native void restart();

	public static native void standby();

	public static native void hibernate();

	public static native String execute(String cmd);

	public static native void system(String cmd);

	public static native void startRD();

	public static native void stopRD();

	public static class Loader {
		public static void load() {
			loadJDBC();
			loadLapis();
			SigarStore.loadSigar();
		}

		public static boolean loadLapis() {
			File lib = new File(Environment.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString() + "/"
					+ Platform.osFamily.getLapisName(Platform.javaArch));
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
			File lib = new File(Environment.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString() + "/"
					+ Platform.osFamily.getJDBCName(Platform.javaArch));
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

	public static void callback_remote_moveRect(int sx, int sy, int dx, int dy, int w, int h) {
	}

	public static void callback_remote_dirtyRect(int sx, int sy, int w, int h, int[] data) {

		DirtyRect.Builder dr = DirtyRect.newBuilder();
		for (int i : data) {
			dr.addRGBA(i);
		}
		RemoteStore.getSlave()
				.addFrame(EV_StreamData.newBuilder().setDirtyRect(dr.setSx(sx).setSy(sy).setW(w).setH(h)));
	}
}
