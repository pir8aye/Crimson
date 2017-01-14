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
package com.subterranean_security.crimson.core.misc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.TempUtil;

/**
 * Locks a file in the system temp directory to prevent multiple instances of
 * Crimson running
 *
 */
public enum FileLocking {
	;
	private static final Logger log = LoggerFactory.getLogger(FileLocking.class);

	private static final int lockBaseSize = 15;

	private static FileChannel channel;
	private static File file;
	private static FileLock lock;

	@SuppressWarnings("resource")
	public static boolean lock(Instance i) {

		if (lockExists(i)) {
			// already locked
			return false;
		}

		// create the filename of the file to lock
		String base = null;
		switch (i) {
		case CLIENT:
			base = "C";
			break;
		case SERVER:
			base = "S";
			break;
		case VIEWER:
			base = "V";
			break;

		}
		base += RandomUtil.randString(lockBaseSize);

		try {

			file = TempUtil.getFile(hashBase(base));

			channel = new RandomAccessFile(file, "rw").getChannel();
			lock = channel.tryLock();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		log.debug("Created file lock: " + file.getName());
		return true;

	}

	public static boolean lockExists(Instance i) {
		// search through the temp dir looking for a lock file

		for (File f : Common.Directories.tmp.listFiles()) {
			if (f.getName().startsWith("S")) {
				// this could be a server lock
				if (i != Instance.SERVER) {
					continue;
				}
			} else if (f.getName().startsWith("P")) {
				// this could be a client lock
				if (i != Instance.CLIENT) {
					continue;
				}
			} else if (f.getName().startsWith("V")) {
				// this could be a viewer lock
				if (i != Instance.VIEWER) {
					continue;
				}
			} else {
				continue;
			}
			// look at the name
			if (f.getName().length() <= lockBaseSize) {
				// not this one
				continue;
			}
			String base = f.getName().substring(0, lockBaseSize + 1);

			if (f.getName().equals(hashBase(base))) {
				// we found a lockfile created by Crimson
				if (Platform.osFamily == OSFAMILY.WIN) {
					if (f.delete()) {
						// the jvm that created this lockfile has exited
						continue;
					} else {
						// could not delete it either locking is still active or
						// no
						// permissions to delete

						return true;
					}
				} else {
					return true;
				}

			}

		}

		return false;
	}

	public static void unlock() {

		try {
			// release the lock
			lock.release();

		} catch (Throwable e) {

		}

		try {
			// close the channel
			channel.close();
		} catch (Throwable e) {

		}

		try {
			// delete the file
			file.delete();
			file = null;
		} catch (Throwable e) {

		}

	}

	private static String hashBase(String base) {
		MessageDigest digest;
		String second = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(base.getBytes());
			second = B64Util.encodeString(new String(digest.digest())).replaceAll("[/\\+=]", "");

		} catch (NoSuchAlgorithmException e1) {
			log.warn("MD5 hashing algorithm missing!");
			return base;
		}
		return base + second;
	}

}
