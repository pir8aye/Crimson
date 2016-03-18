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
package com.subterranean_security.crimson.sv.permissions;

import java.io.Serializable;
import java.util.HashMap;

public class Permissions implements Serializable {

	private static final long serialVersionUID = 1L;
	public HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
	public HashMap<Integer, ClientPermissions> clients = new HashMap<Integer, ClientPermissions>();

	public Permissions() {
		flags.put("super", false);
		flags.put("gen.payload", false);
		flags.put("net.listener.creation", false);
		flags.put("srv.power", false);
		flags.put("srv.settings", false);
		flags.put("srv.fs.read", false);
		flags.put("srv.fs.write", false);

	}

	public boolean verify(String permission) {
		return flags.get("super") || flags.get(permission);
	}

	public boolean verify(int clientID, String permission) {
		return flags.get("super") || (clients.containsKey(clientID)
				&& (clients.get(clientID).flags.get("super") || clients.get(clientID).flags.get(permission)));
	}

}

class ClientPermissions implements Serializable {

	private static final long serialVersionUID = 1L;
	public HashMap<String, Boolean> flags = new HashMap<String, Boolean>();

	public ClientPermissions() {
		flags.put("super", false);
	}

}