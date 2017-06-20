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
package com.subterranean_security.crimson.core.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.test.TestUtil;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class ProfileStoreTest {

	static {
		TestUtil.setupDatabaseStore();
		DatabaseStore.getDatabase().store("clients", new CachedMap<Integer, ClientProfile>());
		DatabaseStore.getDatabase().store("viewers", new CachedMap<Integer, ViewerProfile>());
		ProfileStore.initialize(
				(CachedMap<Integer, ClientProfile>) DatabaseStore.getDatabase().getCachedCollection("clients"),
				(CachedMap<Integer, ViewerProfile>) DatabaseStore.getDatabase().getCachedCollection("viewers"),
				new ServerProfile());
	}

	private ViewerProfile getRandomViewer() {
		ViewerProfile vp = new ViewerProfile(IDGen.cvid());

		return vp;
	}

	@Test
	public void testAddClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddGetViewer() {
		ViewerProfile vp = getRandomViewer();
		ProfileStore.addViewer(vp);
		for (int i = 0; i < 1000; i++) {
			ProfileStore.addViewer(getRandomViewer());
		}
		assertEquals(vp, ProfileStore.getViewer(vp.getCvid()));
		int size = ProfileStore.countTotalProfiles();

		// add duplicate
		ProfileStore.addViewer(vp);
		assertEquals(size, ProfileStore.countTotalProfiles());
	}

	@Test
	public void testGetProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetViewerInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetViewerString() {
		ViewerProfile vp = getRandomViewer();
		vp.setAttr(AKeySimple.VIEWER_USER.ordinal(), "admin");
		ProfileStore.addViewer(vp);
		assertEquals(vp, ProfileStore.getViewer("admin"));
	}

	@Test
	public void testRemoveProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveViewer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetViewers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClients() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProfiles() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateEV_ServerProfileDelta() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateEV_ViewerProfileDelta() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateEV_ProfileDelta() {
		fail("Not yet implemented");
	}

}
