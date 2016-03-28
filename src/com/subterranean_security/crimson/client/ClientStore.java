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

import java.util.ArrayList;

import org.slf4j.Logger;

import com.subterranean_security.crimson.client.net.ClientConnector;
import com.subterranean_security.crimson.core.util.CUtil;

public enum ClientStore {
	;

	private static final Logger log = CUtil.Logging.getLogger(ClientStore.class);

	public static class Connections {
		private static ArrayList<ClientConnector> connections = new ArrayList<ClientConnector>();

		public static void add(ClientConnector c) {
			log.debug("Adding new connection");
			connections.add(c);
		}
	}

}
