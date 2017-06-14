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
package com.subterranean_security.crimson.core.storage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.test.TestUtil;

public class BasicDatabaseTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static BasicDatabase database;

	private Map<String, String> saved1;
	private Map<Integer, String> saved2;
	private Map<String, Long> saved3;
	private Map<String, Object> saved4;

	@Before
	public void setUp() {
		// add some data
		saved1 = new HashMap<>();
		saved2 = new HashMap<>();
		saved3 = new HashMap<>();
		saved4 = new HashMap<>();

		for (int i = 0; i < RandomUtil.rand(50, 1000); i++) {

			if (i % 3 == 0) {
				String key = RandomUtil.randString(10);
				String value = RandomUtil.randString(10);

				saved1.put(key, value);
				database.store(key, value);
			} else if (i % 4 == 0) {
				String value = RandomUtil.randString(10);
				saved2.put(database.store(value), value);
			} else if (i % 5 == 0) {
				String key = RandomUtil.randString(10);
				Long value = RandomUtil.rand(Long.MIN_VALUE + 1, Long.MAX_VALUE);

				saved3.put(key, value);
				database.store(key, value);
			} else {
				String key = RandomUtil.randString(10);
				Object value = new Date();

				saved4.put(key, value);
				database.store(key, value);
			}

		}
	}

	@Test
	private void testDatabase() {
		testDatabase(false);
		testDatabase(true);
	}

	private void testDatabase(boolean save) {
		if (save) {
			database = TestUtil.saveDatabase(database);
		}

		for (String key : saved1.keySet()) {
			assertEquals(saved1.get(key), database.getString(key));
		}
		for (Integer key : saved2.keySet()) {
			assertEquals(saved2.get(key), database.getString(key));
		}
		for (String key : saved3.keySet()) {
			assertEquals(saved3.get(key), new Long(database.getLong(key)));
		}
		for (String key : saved4.keySet()) {
			assertEquals(saved4.get(key), database.getObject(key));
		}
	}

	@Test
	public void testDeleteString() {
		for (int i = 0; i < RandomUtil.rand(50, 1000); i++) {
			String key = RandomUtil.randString(10);
			String value = RandomUtil.randString(10);

			database.store(key, value);
			database.delete(key);

			thrown.expect(NoSuchElementException.class);
			database.getString(key);
		}
		testDatabase();
	}

	@Test
	public void testDeleteInt() {
		for (int i = 0; i < RandomUtil.rand(50, 1000); i++) {

			String value = RandomUtil.randString(10);
			int key = database.store(value);

			database.delete(key);

			thrown.expect(NoSuchElementException.class);
			database.getString(key);
		}
		testDatabase();
	}

}
