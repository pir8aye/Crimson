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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class ServerProfileStore extends ProfileStore {

	static {
		try {
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection("profiles.viewers");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store("profiles.viewers", new CachedMap<Integer, ViewerProfile>());
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection("profiles.viewers");
		}

		try {
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection("profiles.clients");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store("profiles.clients", new CachedMap<Integer, ClientProfile>());
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection("profiles.clients");
		}

		// server profile is recreated and not saved
		serverProfile = new ServerProfile();

	}

	public static List<ViewerProfile> getViewersWithAuthorityOverClient(int cid) {
		List<ViewerProfile> vps = new ArrayList<ViewerProfile>();
		try {
			for (int vid : viewerProfiles.keySet()) {
				// TODO filter
				vps.add(viewerProfiles.get(vid));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vps;
	}

	public static List<ClientProfile> getClientsUnderAuthorityOfViewer(int vid) {
		List<ClientProfile> cps = new ArrayList<ClientProfile>();
		try {
			for (int cid : clientProfiles.keySet()) {
				// TODO filter
				cps.add(clientProfiles.get(cid));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cps;
	}

	public static Set<Integer> getViewerKeyset() {
		return viewerProfiles.keySet();
	}

	public static Set<Integer> getClientKeyset() {
		return clientProfiles.keySet();
	}

}