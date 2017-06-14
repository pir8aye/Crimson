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
package com.subterranean_security.crimson.core.struct.collections.cached;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.RandomUtil;

public class CachedListTest {

	private static CachedList<String> list;
	private static List<String> saved;
	private static BasicDatabase database;
	private static File temp;

	static {
		Native.Loader.loadJDBCTemporarily(new File("lib"));

		temp = new File(System.getProperty("java.io.tmpdir") + "/" + RandomUtil.randString(5) + ".db");
		database = new BasicDatabase(temp);
		try {
			database.initialize();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Before
	public void setup() {
		saved = new ArrayList<>();
		list = new CachedList<>(database);
	}

	private void randomize() {
		for (int i = 0; i < RandomUtil.rand(50, 1000); i++) {
			String s = RandomUtil.randString(2);
			saved.add(s);
			list.add(s);
		}
	}

	@Test
	public void testAddGet() {
		randomize();

		assertEquals(saved.size(), list.size());
		for (int i = 0; i < saved.size(); i++) {
			assertEquals(saved.get(i), list.get(i));
		}

	}

	@Test
	public void testAddIntT() {
		for (int i = 0; i < RandomUtil.rand(50, 100); i++) {
			String s = RandomUtil.randString(10);
			saved.add(i / 2, s);
			list.add(i / 2, s);
		}

		assertEquals(saved.size(), list.size());
		for (int i = 0; i < saved.size(); i++) {
			assertEquals(saved.get(i), list.get(i));
		}
	}

	@Test
	public void testRemoveInt() {
		randomize();

		for (int i = 0; i < list.size() / 4; i++) {
			int pos = RandomUtil.rand(0, list.size() - 1);
			list.remove(pos);
			saved.remove(pos);
		}

		assertEquals(saved.size(), list.size());
		for (int i = 0; i < saved.size(); i++) {
			assertEquals(saved.get(i), list.get(i));
		}
	}

	@Test
	public void testClear() {
		randomize();
		list.clear();
		assertEquals(0, list.size());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(list.isEmpty());
		randomize();
		assertFalse(list.isEmpty());
	}

	@Test
	public void testAddAllCollectionOfQextendsT() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAllIntCollectionOfQextendsT() {
		fail("Not yet implemented");
	}

	@Test
	public void testContains() {
		fail("Not yet implemented");
	}

	@Test
	public void testContainsAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testIndexOf() {
		randomize();

		for (int i = 0; i < saved.size(); i++) {
			assertEquals(i, list.indexOf(saved.get(i)));
		}
	}

	@Test
	public void testIterator() {
		randomize();

		Iterator<String> it1 = saved.iterator();
		Iterator<String> it2 = list.iterator();

		int i = 0;
		while (it1.hasNext()) {
			assertEquals(it1.next(), it2.next());

			if (i++ % 4 == 0) {
				it1.remove();
				it2.remove();
			}

		}

		assertFalse(it2.hasNext());
	}

	@Test
	public void testLastIndexOf() {
		randomize();

	}

	@Test
	public void testListIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testListIteratorInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveObject() {
		randomize();

		for (int i = 0; i < saved.size(); i++) {
			if (i % 3 == 0) {
				list.remove(saved.get(i));
				saved.remove(saved.get(i));
			}
		}

		assertEquals(saved.size(), list.size());
		for (int i = 0; i < saved.size(); i++) {
			assertEquals(saved.get(i), list.get(i));
		}
	}

	@Test
	public void testRemoveAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetainAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		randomize();

		for (int i = 0; i < saved.size() / 2; i++) {
			int index = RandomUtil.rand(0, list.size() - 1);

			list.set(index, "duplicate");
			saved.set(index, "duplicate");

		}

		assertEquals(saved.size(), list.size());
		for (int i = 0; i < saved.size(); i++) {
			assertEquals(saved.get(i), list.get(i));
		}

	}

	@Test
	public void testSubList() {
		fail("Not yet implemented");
	}

	@Test
	public void testToArray() {
		randomize();

		assertArrayEquals(saved.toArray(), list.toArray());
	}

	@Test
	public void testToArrayTArray() {
		fail("Not yet implemented");
	}

}
