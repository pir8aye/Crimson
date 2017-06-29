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
package com.subterranean_security.crimson.core.net.listener;

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.debug.CharcoalAppender;
import com.subterranean_security.crimson.proto.core.net.sequences.Debug.EV_DebugLogEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal.Instance;

public abstract class ConnectionEventListener implements Observer {
	private Thread charcoalUpdater;

	@Override
	public void update(Observable o, Object arg) {
		Connector connector = (Connector) o;
		if (arg instanceof ConnectionState) {
			if (connector.getInstance() == Instance.VIRIDIAN) {
				switch ((ConnectionState) arg) {
				case CONNECTED:
					// trigger report buffer flush
					new Thread(() -> Reporter.flushBuffer()).start();
					break;
				default:
					break;
				}
			} else if (DEV_MODE && connector.getInstance() == Instance.CHARCOAL) {
				switch ((ConnectionState) arg) {
				case AUTHENTICATED:
					startUpdater();
					break;
				case NOT_CONNECTED:
					stopUpdater();
					break;
				default:
					break;

				}
			}
		}
	}

	private void startUpdater() {
		if (charcoalUpdater != null)
			return;

		charcoalUpdater = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(CharcoalAppender.in))) {
				while (!Thread.interrupted()) {
					NetworkStore.route(Message.newBuilder().setSid(LcvidStore.cvid).setRid(Reserved.CHARCOAL)
							.setEvDebugLogEvent(EV_DebugLogEvent.newBuilder().setLine(br.readLine())));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		charcoalUpdater.start();
	}

	private void stopUpdater() {
		if (charcoalUpdater != null) {
			charcoalUpdater.interrupt();
			charcoalUpdater = null;
		}
	}
}