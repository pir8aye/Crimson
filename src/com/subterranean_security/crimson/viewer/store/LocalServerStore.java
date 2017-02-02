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
package com.subterranean_security.crimson.viewer.store;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;

public final class LocalServerStore {
	private LocalServerStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(LocalServerStore.class);

	/**
	 * The server executable
	 */
	public static final File bundledServer = new File(
			Common.Directories.base.getAbsolutePath() + "/Crimson-Server.jar");

	public static Process process;
	private static OutputStream os;

	public static boolean startLocalServer() {
		String command = "java -jar \"" + bundledServer.getAbsolutePath() + "\"";
		log.debug("Starting local server ({})", command);
		try {
			process = Runtime.getRuntime().exec(command);
			os = process.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void killLocalServer() {
		if (os != null) {
			// kill server
			try {
				os.write("quit\n".getBytes());
				os.flush();
				process.waitFor(3, TimeUnit.SECONDS);
				os.close();
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		}
	}
}