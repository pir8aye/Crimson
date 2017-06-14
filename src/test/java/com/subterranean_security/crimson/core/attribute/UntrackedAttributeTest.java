package com.subterranean_security.crimson.core.attribute;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.util.RandomUtil;

public class UntrackedAttributeTest {

	private UntrackedAttribute attribute;

	@Before
	public void setUp() {
		attribute = new UntrackedAttribute();
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

		assertTrue(d2.getTime() - attribute.getTimestamp().getTime() <= range);
	}

	@Test
	public void testSet() {
		String expected = RandomUtil.randString(10);
		attribute.set(expected);

		assertEquals(expected, attribute.get());

	}

}
