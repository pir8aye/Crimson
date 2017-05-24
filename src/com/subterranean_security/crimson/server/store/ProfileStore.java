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
import java.util.Set;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.misc.MemMap;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class ProfileStore {
	private static MemMap<Integer, ClientProfile> clientProfiles;
	private static MemMap<Integer, ViewerProfile> viewerProfiles;
	private static ServerProfile serverProfile;

	static {
		try {
			clientProfiles = (MemMap<Integer, ClientProfile>) DatabaseStore.getDatabase().getObject("profiles.clients");
			clientProfiles.setDatabase(DatabaseStore.getDatabase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			viewerProfiles = (MemMap<Integer, ViewerProfile>) DatabaseStore.getDatabase().getObject("profiles.viewers");
			viewerProfiles.setDatabase(DatabaseStore.getDatabase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// server profile is recreated and not saved
		serverProfile = new ServerProfile();

	}

	public static ServerProfile getServer() {
		return serverProfile;
	}

	public static ClientProfile getClient(int cid) {
		try {
			return clientProfiles.get(cid).initialize();
		} catch (Exception e) {
			return null;
		}
	}

	public static void addClient(ClientProfile p) {
		clientProfiles.put(p.getCvid(), p);
	}

	public static ViewerProfile getViewer(int vid) {
		try {
			return viewerProfiles.get(vid);
		} catch (Exception e) {
			return null;
		}
	}

	public static ViewerProfile getViewer(String user) {
		try {
			for (Integer i : viewerProfiles.keySet()) {
				ViewerProfile vp = viewerProfiles.get(i);
				if (vp.get(AKeySimple.VIEWER_USER).equals(user)) {
					return vp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	public static void addViewer(ViewerProfile p) {
		viewerProfiles.put(p.getCvid(), p);
	}

}