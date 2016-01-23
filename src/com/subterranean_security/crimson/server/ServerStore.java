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
package com.subterranean_security.crimson.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.proto.msg.Gen.Group;
import com.subterranean_security.crimson.core.storage.ServerDB;
import com.subterranean_security.crimson.core.storage.ViewerDB;
import com.subterranean_security.crimson.server.network.Listener;
import com.subterranean_security.crimson.server.network.Receptor;

public enum ServerStore {
	;
	public static class Listeners {
		private static ArrayList<Listener> listeners = new ArrayList<Listener>();

	}

	public static class Databases {
		public static ServerDB system;
		public static HashMap<String, ViewerDB> loaded_viewers = new HashMap<String, ViewerDB>();// UID

	}

	public static class Connections {
		public static ArrayList<Receptor> connections = new ArrayList<Receptor>();

		public static void add(Receptor connection) {
			synchronized (connections) {
				connections.add(connection);
			}
		}

		public static void remove(Receptor connection) {
			synchronized (connections) {
				connections.remove(connection);
			}
		}

	}

	public static class Streams {

	}

	public static class Files {
		// TODO somehow get the correct filesystem
		ArrayList<LocalFilesystem> fs = new ArrayList<LocalFilesystem>();
	}

	public static class Groups {
		public static ArrayList<Group> groups = null;

		static {
			try {
				groups = (ArrayList<Group>) Databases.system.getObject("groups");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static Group getGroup(String groupname) {
			for (Group g : groups) {
				if (g.getName().equals(groupname)) {
					return g;
				}
			}
			return null;
		}
	}

}
