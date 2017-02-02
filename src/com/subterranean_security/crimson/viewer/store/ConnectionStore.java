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
package com.subterranean_security.crimson.viewer.store;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.viewer.net.ViewerConnector;

public final class ConnectionStore {
	private ConnectionStore() {
	}

	private static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

	private static HashMap<Integer, BasicConnector> connections = new HashMap<Integer, BasicConnector>();

	public static void put(int cvid, BasicConnector vc) {
		log.debug("Added new connection (CVID: {})", cvid);
		connections.put(cvid, vc);
	}

	public static BasicConnector get(int cvid) {
		return connections.get(cvid);
	}

	public static ViewerConnector getVC(int cvid) {
		return (ViewerConnector) connections.get(cvid);
	}

	public static void closeAll() {
		for (Integer i : connections.keySet()) {
			try {
				connections.remove(i).close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static int getSize() {
		return connections.size();
	}
}