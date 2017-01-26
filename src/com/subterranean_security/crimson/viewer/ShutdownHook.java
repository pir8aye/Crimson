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
package com.subterranean_security.crimson.viewer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.FileLocking;
import com.subterranean_security.crimson.universal.stores.Database;

public class ShutdownHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

	public static boolean shuttingdown = false;

	@Override
	public void run() {
		shuttingdown = true;
		log.info("Received shutdown signal");

		log.debug("Terminating network connections");
		ViewerStore.Connections.closeAll();

		try {
			log.debug("Closing database");
			Database.closeFacility();
		} catch (IOException e) {
			log.error("Failed to close database: {}", e.getMessage());
		}

		ViewerStore.LocalServer.killLocalServer();

		FileLocking.unlock();
	}
}
