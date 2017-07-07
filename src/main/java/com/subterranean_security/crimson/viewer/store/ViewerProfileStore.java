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

import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.net.listener.ViewerProfileListener;

public final class ViewerProfileStore extends ProfileStore {
	private ViewerProfileStore() {
	}

	static {
		setProfileListener(new ViewerProfileListener());
	}

	public static void initialize(String lcvid) throws NoSuchElementException, SQLException {
		if (lcvid == null)
			throw new IllegalArgumentException();

		try {
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".viewers");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".viewers", new CachedMap<Integer, ViewerProfile>());
			viewerProfiles = (CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".viewers");
		}

		try {
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".clients");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".clients", new CachedMap<Integer, ClientProfile>());
			clientProfiles = (CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".clients");
		}

		try {
			serverProfiles = (CachedMap<Integer, ServerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".servers");
		} catch (NoSuchElementException e) {
			DatabaseStore.getDatabase().store(lcvid + ".servers", new CachedMap<Integer, ServerProfile>());
			serverProfiles = (CachedMap<Integer, ServerProfile>) DatabaseStore.getDatabase()
					.getCachedCollection(lcvid + ".servers");
		}

	}

}