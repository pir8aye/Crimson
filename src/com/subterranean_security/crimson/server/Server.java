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
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.EH;
import com.subterranean_security.crimson.core.util.FileLocking;
import com.subterranean_security.viridian.Database;
import com.subterranean_security.viridian.mail.Mail;

public final class Server {
	private static final Logger log = LoggerFactory.getLogger(Server.class);

	private static boolean running = false;

	public static void main(String[] argv) {

		CUtil.Logging.configure();

		// Establish the custom fallback exception handler
		log.debug("Initializing exception handler");
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		log.debug("Initializing shutdown hook");
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Try to get a lock or exit
		if (!FileLocking.lock(Instance.SERVER)) {
			System.exit(0);
		}

		// Read configuration
		readConfig();

		// Load native libraries
		Platform.Advanced.loadSigar();
		Platform.Advanced.loadLapis();

		// Clear /tmp/
		log.debug("Clearing temporary directory");
		CUtil.Files.Temp.clear();

		// initialize system database
		try {
			ServerStore.Databases.system = new ServerDB(
					new File(Common.Directories.var.getAbsolutePath() + "/system.db"));
			Common.cvid = ServerStore.Databases.system.getInteger("cvid");
		} catch (Exception e) {
			log.error("Failed to initialize system database");
			System.exit(0);
		}

		start();

		if (Common.isDebugMode() && !ServerState.isCloudMode() && !ServerState.isExampleMode()) {
			generateDebugInstaller();
		}

		parse();
	}

	public static void parse() {

		try (Scanner stdin = new Scanner(System.in)) {
			while (true) {
				String input = stdin.nextLine();
				String[] parts = input.split("\\s+");

				if (input.isEmpty()) {
					continue;
				} else if (parts[0].equals("quit") || parts[0].equals("exit")) {
					System.exit(0);
				}

			}
		} catch (NoSuchElementException e) {
			// ignore because server is probably shutting down
		}

	}

	public static boolean isRunning() {
		return running;
	}

	public static void stop() {
		if (running) {
			log.info("Stopping server");
			running = false;
			ServerStore.Listeners.unload();
		}

	}

	public static void start() {
		if (!running) {
			log.info("Starting server");
			running = true;
			ServerStore.Listeners.load();
		}
	}

	private static void generateDebugInstaller() {
		log.debug("Generating debug installer");

		ClientConfig cc = ClientConfig.newBuilder().setOutputType("Java (.jar)").setAuthType(AuthType.NO_AUTH)
				.addTarget(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build())
				.setPathWin("C:\\Users\\dev\\Documents\\Crimson").setPathBsd("/").setPathLin("/home/dev/cr")
				.setPathOsx("/").setPathSol("/").setReconnectPeriod(3000).setBuildNumber(Common.build)
				.setAutostart(true).build();
		try {
			Generator g = new Generator();
			g.generate(cc);
			byte[] res = g.getResult();
			CUtil.Files.writeFile(res, new File(System.getProperty("user.home") + "/Desktop/client.jar"));
			log.info("Installer size: " + res.length + " bytes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setState(StateType stateType) {
		switch (stateType) {
		case FUNCTIONING_OFF:
			stop();
			break;
		case FUNCTIONING_ON:
			start();
			break;
		case RESTART:
			break;
		case SHUTDOWN:
			break;
		case UNINSTALL:
			break;
		default:
			break;
		}
		// notify viewers
		ServerStore.Connections.sendToAll(Instance.VIEWER, Message.newBuilder()
				.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().setServerStatus(running)).build());

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
