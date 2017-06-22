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
package com.subterranean_security.crimson.core.net.thread;

import java.net.ConnectException;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.CertificateState;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.net.thread.routines.ConnectionRoutine;
import com.subterranean_security.crimson.core.store.ConnectionStore;

/**
 * A Thread which makes one or more connection attempts.
 */
public class ConnectionThread extends Thread {

	private ConnectionRoutine routine;

	public ConnectionThread(ConnectionRoutine routine) {
		super(routine, "[ConnectionThread] " + routine.getName());
		this.routine = routine;
	}

	/**
	 * Checks if the ConnectionThread is attempting a connection
	 * 
	 * @return True if this ConnectionThread is attempting a connection
	 */
	public boolean isConnecting() {
		return isAlive();
	}

	public Connector connect() throws InterruptedException {
		start();
		join();
		return routine.getResult();
	}

	public static Connector makeConnection(ExecutorFactory exe, String server, int port, boolean forceCertificates) {
		ConnectionStore.log.debug("Attempting connection to: {}:{}", server, port);

		Connector connector = new Connector(exe.build());
		try {
			connector.connect(Config.ConnectionType.SOCKET, server, port, true);
		} catch (ConnectException | InterruptedException e) {
			ConnectionStore.log.debug("Connection failed: {}", e.getMessage());
			return null;
		}

		if (connector.getCertState() == CertificateState.REFUSED) {
			if (!forceCertificates) {
				// try insecure
				connector = new Connector(exe.build(), false);
				try {
					connector.connect(Config.ConnectionType.SOCKET, server, port, false);
				} catch (ConnectException | InterruptedException e) {
					ConnectionStore.log.debug("Connection failed: {}", e.getMessage());
					return null;
				}

				if (connector.getCertState() == CertificateState.REFUSED) {
					ConnectionStore.log.debug("Connection failed: Certificate error");
					return null;
				}

			} else {
				ConnectionStore.log.debug("Dropping connection with {} because certificate verification failed",
						server);
			}
		}

		return connector;
	}

}
