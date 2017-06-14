package com.subterranean_security.crimson.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationUtilTest {

	@Test
	public void testUsername() {
		assertFalse(ValidationUtil.username("test user"));
		assertFalse(ValidationUtil.username("_test*"));

		assertTrue(ValidationUtil.username("cilki"));
	}

	@Test
	public void testGroup() {
		assertFalse(ValidationUtil.group("test group**"));

		assertTrue(ValidationUtil.group("test group"));
	}

	@Test
	public void testDns() {
		assertFalse(ValidationUtil.dns("test..com"));
		assertFalse(ValidationUtil.dns("test.-.com"));
		assertFalse(ValidationUtil.dns(".test.com"));
		assertFalse(ValidationUtil.dns("test..com"));

		assertTrue(ValidationUtil.dns("test.com"));
		assertTrue(ValidationUtil.dns("test.com.org.co"));
	}

	@Test
	public void testIpv4() {
		assertFalse(ValidationUtil.ipv4(null));
		assertFalse(ValidationUtil.ipv4(""));
		assertFalse(ValidationUtil.ipv4("...."));
		assertFalse(ValidationUtil.ipv4("-1.-1.-1.-1"));
		assertFalse(ValidationUtil.ipv4("400.300.1000.1"));
		assertFalse(ValidationUtil.ipv4("192.168.1.600"));

		assertTrue(ValidationUtil.ipv4("192.168.1.1"));
		assertTrue(ValidationUtil.ipv4("192.168.5.254"));
		assertTrue(ValidationUtil.ipv4("74.165.90.80"));
		assertTrue(ValidationUtil.ipv4("0.0.0.0"));
		assertTrue(ValidationUtil.ipv4("255.255.255.255"));
	}

	@Test
	public void testPrivateIP() {
		assertFalse(ValidationUtil.privateIP("74.192.155.80"));

		assertTrue(ValidationUtil.privateIP("192.168.1.1"));
	}

	@Test
	public void testPortString() {
		assertFalse(ValidationUtil.port(null));
		assertFalse(ValidationUtil.port(""));
		assertFalse(ValidationUtil.port("123456789"));
		assertFalse(ValidationUtil.port("4000g"));
		assertFalse(ValidationUtil.port("test"));
		assertFalse(ValidationUtil.port("-5000"));

		assertTrue(ValidationUtil.port("80"));
		assertTrue(ValidationUtil.port("8080"));
		assertTrue(ValidationUtil.port("10101"));

	}

	@Test
	public void testPath() {
		assertFalse(ValidationUtil.path(null));

		assertTrue(ValidationUtil.path("test/.test.txt"));
	}

	@Test
	public void testEmail() {
		assertFalse(ValidationUtil.email("test @test.com"));
		assertFalse(ValidationUtil.email("test@test"));
		assertFalse(ValidationUtil.email("test.com"));
		assertFalse(ValidationUtil.email("test@test.com."));
		assertFalse(ValidationUtil.email(".test@test.com"));
		assertFalse(ValidationUtil.email("test@test@test.com"));
		assertFalse(ValidationUtil.email("test"));

		assertTrue(ValidationUtil.email("test@test.com"));
		assertTrue(ValidationUtil.email("test@test.test.com"));
		assertTrue(ValidationUtil.email("test@test.test.com.org"));
	}

}
