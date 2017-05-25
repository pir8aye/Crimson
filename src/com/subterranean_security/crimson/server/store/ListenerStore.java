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
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.sv.net.Listener;

/**
 * Manage listeners
 */
public final class ListenerStore {
	private ListenerStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(ListenerStore.class);

	private static List<Listener> loaded;
	private static List<ListenerConfig> saved;

	static {
		// initialize stored list
	}

	/**
	 * Load listeners from persistent storage. Does not start any listeners.
	 * 
	 * @pre: Unloaded
	 */
	public static void load() {
		if (loaded != null)
			throw new IllegalStateException("The ListenerStore is already loaded");
		if (saved == null)
			throw new IllegalStateException("Failed to load listener configs");

		loaded = new ArrayList<Listener>();
		for (ListenerConfig config : saved)
			loaded.add(new Listener(config));
	}

	/**
	 * Stop and release all listeners.
	 * 
	 * @pre: Loaded
	 */
	public static void unload() {
		if (loaded == null)
			throw new IllegalStateException("The ListenerStore is already unloaded");

		stopAll();
		loaded.clear();
		loaded = null;
	}

	/**
	 * Stop all listeners.
	 * 
	 * @pre: Loaded
	 */
	public static void stopAll() {
		if (loaded == null)
			throw new IllegalStateException("The ListenerStore is unloaded");
		for (Listener toStop : loaded) {
			toStop.stop();
		}
	}

	/**
	 * Starts all listeners.
	 * 
	 * @pre: Loaded
	 */
	public static void startAll() {
		if (loaded == null)
			throw new IllegalStateException("The ListenerStore is unloaded");
		for (Listener toStart : loaded) {
			toStart.start();
		}
	}

	/**
	 * Stop a Listener
	 * 
	 * @param name
	 *            The name of the Listener to stop
	 */
	public static void stop(String name) {
		for (Listener toStop : loaded) {
			if (toStop.getConfig().getName().equals(name)) {
				toStop.stop();
				break;
			}
		}
	}

	/**
	 * Start a Listener
	 * 
	 * @param name
	 *            The name of the Listener to start
	 */
	public static void start(String name) {
		for (Listener toStart : loaded) {
			if (toStart.getConfig().getName().equals(name)) {
				toStart.start();
				break;
			}
		}
	}

	/**
	 * Stop, unload, and delete a Listener from persistent storage.
	 * 
	 * @param name
	 *            The name of the listener to be deleted
	 */
	public static void delete(String name) {
		stop(name);

		// remove from loaded listeners
		for (Iterator<Listener> iterator = loaded.iterator(); iterator.hasNext();) {
			Listener toDelete = iterator.next();
			if (toDelete.getConfig().getName().equals(name)) {
				iterator.remove();
				break;
			}
		}

		// removed from saved listeners
		for (Iterator<ListenerConfig> iterator = saved.iterator(); iterator.hasNext();) {
			ListenerConfig toDelete = iterator.next();
			if (toDelete.getName().equals(name)) {
				iterator.remove();
				break;
			}
		}
	}

	/**
	 * Add a Listener to persistent storage.
	 * 
	 * @param config
	 */
	public static void add(ListenerConfig config) {
		if (saved.contains(config))
			throw new IllegalArgumentException("This Listener is already stored");
		if (loaded == null)
			throw new IllegalStateException("The ListenerStore has not been loaded");

		loaded.add(new Listener(config));
		saved.add(config);
	}

	/**
	 * @return The number of active listeners
	 */
	public static int getActive() {
		int total = 0;
		for (Listener listener : loaded)
			if (listener.isActive())
				total++;

		return total;
	}

	/**
	 * @return The number of inactive listeners
	 */
	public static int getInactive() {
		int total = 0;
		for (Listener listener : loaded)
			if (!listener.isActive())
				total++;

		return total;
	}

	/**
	 * @return A list of the loaded listeners
	 */
	public static List<Listener> getLoaded() {
		return loaded;
	}

}