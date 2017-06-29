package com.subterranean_security.crimson.sv.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_CPU;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_WIN;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;

public class ClientProfileTest {

	private int cvid;
	private ClientProfile profile;
	private HashMap<SingularKey, String> simple;

	@Before
	public void setUp() throws Exception {
		// generate a random profile
		cvid = RandomUtil.nextInt();
		profile = new ClientProfile(cvid);
		simple = new HashMap<SingularKey, String>();

	}

	private void addRandom() {
		// add some simple attributes
		for (int i = 0; i < RandomUtil.rand(10, 50); i++) {
			SingularKey key = AK_OS.values()[RandomUtil.rand(0, AK_OS.values().length)];
			String value = RandomUtil.randString(10);
			simple.put(key, value);
			profile.set(key, value);
		}
		for (int i = 0; i < RandomUtil.rand(10, 50); i++) {
			SingularKey key = AK_WIN.values()[RandomUtil.rand(0, AK_WIN.values().length)];
			String value = RandomUtil.randString(10);
			simple.put(key, value);
			profile.set(key, value);
		}

	}

	@Test
	public void testGetGroupsAttributeKey() {
	}

	@Test
	public void testGetGroupsInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupListType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupListInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupAttributeKeyString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupIntString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		addRandom();
		for (int i = 0; i < RandomUtil.rand(10, 100); i++) {
			profile.set(AK_CLIENT.STATUS, RandomUtil.randString(10));
		}
		String expected = RandomUtil.randString(10);
		profile.set(AK_CLIENT.STATUS, expected);
		assertEquals(expected, profile.get(AK_CLIENT.STATUS));
	}

	@Test
	public void testGetAttributeAKeySimple() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAttributeAttributeKeyString() {
		fail("Not yet implemented");
	}

	@Test
	public void testMerge() {

		EV_ProfileDelta pd = EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getWireID(), "25")
						.putAttribute(AKeySimple.CLIENT_STATUS.getWireID(), "idle"))
				.addGroup(AttributeGroupContainer.newBuilder().setGroupId("cpu1").setGroupType(1)
						.putAttribute(AK_CPU.MODEL.getWireID(), "test cpu 1.0"))
				.build();
		profile.merge(pd);

		assertEquals(profile.get(AK_META.CVID), 25);
		assertEquals(profile.get(AK_CLIENT.STATUS), "idle");
		assertEquals(profile.getGroup(AK_CPU.MODEL, "cpu1").getStr(AK_CPU.MODEL), "test cpu 1.0");

		pd = EV_ProfileDelta.newBuilder().addGroup(
				AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_STATUS.getWireID(), "active"))
				.build();

		profile.merge(pd);

		assertEquals(profile.get(AK_CLIENT.STATUS), "active");
	}

	@Test
	public void testGetUpdates() {
		profile.merge(EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getWireID(), "35")
						.putAttribute(AKeySimple.CLIENT_STATUS.getWireID(), "idle"))
				.build());

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date date = new Date();

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		profile.merge(EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getWireID(), "45"))
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.OS_ACTIVE_WINDOW.getWireID(),
						"test"))
				.build());

		EV_ProfileDelta pd = profile.getUpdates(date);
		assertEquals(pd.getGroup(0).getAttributeCount(), 2);
		assertEquals(pd.getGroup(0).getAttributeOrDefault(AKeySimple.CLIENT_CID.getWireID(), ""), "45");
		assertEquals(pd.getGroup(0).getAttributeOrDefault(AKeySimple.OS_ACTIVE_WINDOW.getWireID(), ""), "test");
	}

	@Test
	public void testGetLastUpdate() {
		fail("Not yet implemented");
	}

}
