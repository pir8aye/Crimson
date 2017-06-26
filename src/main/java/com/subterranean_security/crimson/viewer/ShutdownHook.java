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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.store.LocalServerStore;

/**
 * Terminate the viewer instance.
 * 
 * @author cilki
 * @since 1.0.0
 */
public final class ShutdownHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

	public static boolean shuttingdown = false;

	@Override
	public void run() {
		shuttingdown = true;
		log.info("Received shutdown signal");

		log.debug("Terminating network connections");
		ConnectionStore.closeAll();

		LocalServerStore.killLocalServer();

		try {
			log.debug("Closing database");
			DatabaseStore.close();
		} catch (Exception e) {
			log.error("Failed to close database: {}", e.getMessage());
		}

		try {
			log.debug("Closing preferences");
			PrefStore.close();
		} catch (Exception e) {
			log.error("Failed to close preferences: {}", e.getMessage());
		}

	}
}
