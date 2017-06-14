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
package com.subterranean_security.crimson.core.struct.stat_stream;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.struct.stat_stream.StatStreamBuffer;
import com.subterranean_security.crimson.core.util.RandomUtil;

public class StatStreamBufferTest {

	private StatStreamBuffer<Long> buffer;
	private int capacity;

	@Before
	public void setup() {
		capacity = RandomUtil.rand(50, 10000);
		buffer = new StatStreamBuffer<>(capacity);
	}

	@Test
	public void testGet() {
		for (int i = 0; i < capacity; i++) {
			buffer.add((long) i);
		}

		for (int i = capacity - 1; i >= 0; i--) {
			assertEquals(buffer.get(capacity - i - 1), new Long(i));
		}

		for (int i = 0; i < 3 * capacity; i++) {
			buffer.add((long) 0);
		}

		for (int i = 0; i < capacity; i++) {
			assertEquals(buffer.get(i), new Long(0));
		}

		buffer.add((long) 42);
		buffer.add((long) 55);

		assertEquals(buffer.get(0), new Long(55));
		assertEquals(buffer.get(1), new Long(42));
	}

	@Test
	public void testSize() {
		assertEquals(0, buffer.size());
		buffer.add(1L);
		assertEquals(1, buffer.size());
		for (int i = 0; i < capacity; i++) {
			buffer.add((long) i);
		}
		assertEquals(capacity, buffer.size());
	}

	@Test
	public void testCapacity() {
		assertEquals(capacity, buffer.capacity());
	}

}
