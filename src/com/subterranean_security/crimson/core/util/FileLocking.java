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
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;

public enum FileLocking {
	;
	private static final Logger log = LoggerFactory.getLogger(FileLocking.class);

	private static FileChannel channel;
	private static File file;
	private static FileLock lock;

	@SuppressWarnings("resource")
	public static void lock(Instance i) {

		if (lockExists(i)) {
			// already locked
			return;
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
		base += CUtil.Misc.nameGen(15);
		// hash the base
		MessageDigest digest;
		String second = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(base.getBytes());
			second = B64.encodeString(new String(digest.digest())).replaceAll("[/\\+=]", "");

		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			file = CUtil.Files.Temp.getGFile(base + second);

			channel = new RandomAccessFile(file, "rw").getChannel();
			lock = channel.lock();
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public static boolean lockExists(Instance i) {
		// search through the temp dir looking for a lock file

		for (File f : Common.gtmp.listFiles()) {
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
			if (f.getName().length() < 20) {
				// not this one
				continue;
			}
			String base = f.getName().substring(0, 16);

			// hash the base
			MessageDigest digest;
			String second = null;
			try {
				digest = MessageDigest.getInstance("MD5");
				digest.update(base.getBytes());
				second = B64.encodeString(new String(digest.digest())).replaceAll("[/\\+=]", "");

			} catch (NoSuchAlgorithmException e1) {
				log.error("MD5 hashing algorithm missing!");
				System.exit(0);
			}

			if (f.getName().equals(base + second)) {
				// we found a lockfile created by Crimson
				if (f.delete()) {
					// the jvm that created this lockfile has exited
					continue;
				} else {
					// could not delete it either locking is still active or no
					// permissions to delete

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
			log.error("Failed to release file lock");
		}

		try {
			// close the channel
			channel.close();
		} catch (Throwable e) {

		}

		try {
			// delete the file
			file.delete();
		} catch (Throwable e) {
			log.error("Failed to delete file lock");
		}

	}

}
