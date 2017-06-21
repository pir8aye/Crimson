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
package com.subterranean_security.crimson.client.net;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.ShutdownHook;
import com.subterranean_security.crimson.client.net.command.AuthCom;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.net.thread.ConnectionPeriod;
import com.subterranean_security.crimson.core.net.thread.ConnectionThread;
import com.subterranean_security.crimson.core.net.thread.routines.RoundRobin;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.cv.net.command.CvidCom;
import com.subterranean_security.crimson.proto.core.Generator.NetworkTarget;

public final class ClientConnectionStore extends ConnectionStore {

	private ClientConnectionStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(ClientConnectionStore.class);

	private static List<NetworkTarget> targets;

	private static boolean connecting;
	public static int connectionIterations;

	public static void setTargets(List<NetworkTarget> t) {
		targets = t;
	}

	public static void connectionRoutine() {
		if (connecting || ShutdownHook.shuttingdown) {
			return;
		}

		ConnectionThread ct = new ConnectionThread(new RoundRobin(new ExecutorFactory(ClientExecutor.class), targets,
				new ConnectionPeriod(ConfigStore.getConfig().getReconnectPeriod()), 0,
				ConfigStore.getConfig().getForceCertificates()));
		try {
			ct.connect();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		connecting = true;
		connectionIterations = 0;
		try {
			while (!Thread.interrupted()) {
				for (NetworkTarget n : targets) {
					connectionIterations++;

					Connector connector = ConnectionThread.makeConnection(new ExecutorFactory(ClientExecutor.class),
							n.getServer(), n.getPort(), ConfigStore.getConfig().getForceCertificates());

					if (connector != null) {
						try {
							CvidCom.getCvid(connector);
						} catch (MessageTimeout e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							return;
						}

						AuthCom.auth(connector);
						return;
					}

					try {
						Thread.sleep(ConfigStore.getConfig().getReconnectPeriod());
					} catch (InterruptedException e) {
						return;
					}

				}
			}
		} finally {
			connecting = false;
		}

	}

}