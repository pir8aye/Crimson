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
package com.subterranean_security.crimson.core.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.util.RandomUtil;

public class UntrackedAttributeTest {

	private UntrackedAttribute<String> attribute;

	@Before
	public void setUp() {
		attribute = new UntrackedAttribute<>();
		for (int i = 0; i < RandomUtil.rand(10, 100); i++) {
			attribute.set(RandomUtil.randString(10));
		}
	}

	@Test
	public void testGetTimestamp() {
		Date d1 = new Date();
		attribute.set("");
		Date d2 = new Date();

		long range = d2.getTime() - d1.getTime();

		assertTrue(d2.getTime() - attribute.getTimestamp() <= range);
	}

	@Test
	public void testSet() {
		String expected = RandomUtil.randString(10);
		attribute.set(expected);

		assertEquals(expected, attribute.get());

	}

}
