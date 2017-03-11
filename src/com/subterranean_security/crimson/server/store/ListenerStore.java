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
package com.subterranean_security.crimson.server.store;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.sv.net.Listener;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public final class ListenerStore {
	private ListenerStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(ListenerStore.class);

	private static boolean running = false;

	public static ArrayList<Listener> listeners = new ArrayList<Listener>();

	public static void load() {
		unloadAll();
		try {
			for (ListenerConfig lc : ((ArrayList<ListenerConfig>) DatabaseStore.getDatabase().getObject("listeners"))) {
				listeners.add(new Listener(lc));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void unloadAll() {
		for (Listener l : listeners) {
			try {
				l.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		listeners.clear();
	}

	public static boolean isRunning() {
		return running;
	}

	public static void stop() {
		if (running) {
			log.info("Stopping network listeners");
			running = false;
			unloadAll();
		}

	}

	public static void start() {
		if (!running) {
			log.info("Starting network listeners");
			running = true;
			load();
		}
	}

}