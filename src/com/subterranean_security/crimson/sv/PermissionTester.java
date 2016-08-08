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
package com.subterranean_security.crimson.sv;

import com.subterranean_security.crimson.core.proto.Users.ClientPermissions;
import com.subterranean_security.crimson.core.proto.Users.ViewerPermissions;
import com.subterranean_security.crimson.server.ServerStore;

public enum PermissionTester {
	;

	public static boolean verifyServerPermission(ViewerPermissions p, String permission) {
		if (p.getSuper()) {
			return true;
		}
		switch (permission.toLowerCase()) {
		case "generate": {
			return p.getGenerate();
		}
		case "create_listener": {
			return p.getCreateListener();
		}
		case "server_power": {
			return p.getServerPower();
		}
		case "server_settings": {
			return p.getServerSettings();
		}
		case "server_fs_read": {
			return p.getServerFsRead();
		}
		case "server_fs_write": {
			return p.getServerFsWrite();
		}
		default: {
			return false;
		}
		}

	}

	public static boolean verifyClientPermission(int vid, int cid, String permission) {
		return verifyClientPermission(ServerStore.Profiles.getViewer(vid).getPermissions(), cid, permission);
	}

	public static boolean verifyClientPermission(ViewerPermissions p, int cid, String permission) {
		if (p.getSuper()) {
			return true;
		}
		for (ClientPermissions cp : p.getClientPermissionsList()) {
			if (cp.getCvid() == cid) {
				if (cp.getSuper()) {
					return true;
				}
				switch (permission.toLowerCase()) {
				case "client_visibility": {
					// TODO
					return true;
				}
				case "client_power": {
					return cp.getClientPower();
				}
				case "client_settings": {
					return cp.getClientSettings();
				}
				case "client_fs_read": {
					return cp.getClientFsRead();
				}
				case "client_fs_write": {
					return cp.getClientFsWrite();
				}
				default: {
					return false;
				}
				}
			}
		}
		return false;

	}

}