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
package com.subterranean_security.crimson.sv.permissions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ViewerPermissionsTest {

	private ViewerPermissions permissions;

	@Before
	public void setup() {
		permissions = new ViewerPermissions();
	}

	@Test
	public void test() {
		int cid = 230;

		permissions.addFlag(Perm.server.generator.generate_exe);
		permissions.addFlag(Perm.server.fs.read);
		permissions.addFlag(Perm.server.fs.write);

		permissions.addFlag(cid, Perm.client.visibility);
		permissions.addFlag(cid, Perm.client.power.shutdown);

		assertTrue(permissions.getFlag(Perm.server.generator.generate_exe));
		assertTrue(permissions.getFlag(cid, Perm.client.visibility));
		assertTrue(permissions.getFlag(cid, Perm.client.power.shutdown));

		assertFalse(permissions.getFlag(cid, Perm.client.power.restart));
		assertFalse(permissions.getFlag(cid + 1, Perm.client.power.shutdown));

		permissions.addFlag(Perm.Super);

		assertTrue(permissions.getFlag(cid, Perm.client.power.restart));
	}

	@Test
	public void testTranslateFlag() {
		assertEquals(17179869484L, ViewerPermissions.translateFlag(4, Perm.client.visibility));
	}

}
