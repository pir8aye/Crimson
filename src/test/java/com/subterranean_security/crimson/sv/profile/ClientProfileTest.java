package com.subterranean_security.crimson.sv.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.subterranean_security.crimson.core.attribute.keys.plural.AKeyCPU;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;

public class ClientProfileTest {

	private int cvid;
	private ClientProfile profile;
	private HashMap<AKeySimple, String> simple;

	@Before
	public void setUp() throws Exception {
		// generate a random profile
		cvid = RandomUtil.nextInt();
		profile = new ClientProfile(cvid);
		simple = new HashMap<AKeySimple, String>();

	}

	private void addRandom() {
		// add some simple attributes
		for (int i = 0; i < RandomUtil.rand(10, 100); i++) {
			AKeySimple key = AKeySimple.values()[RandomUtil.rand(0, AKeySimple.values().length)];
			switch (key) {
			case VIEWER_LOGIN_IP:
			case VIEWER_LOGIN_TIME:
			case VIEWER_USER:
				break;
			default:
				String value = RandomUtil.randString(10);
				simple.put(key, value);
				profile.set(key, value);
			}
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
			profile.set(AKeySimple.CLIENT_STATUS, RandomUtil.randString(10));
		}
		String expected = RandomUtil.randString(10);
		profile.set(AKeySimple.CLIENT_STATUS, expected);
		assertEquals(expected, profile.get(AKeySimple.CLIENT_STATUS));
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
	public void testAmalgamate() {

		EV_ProfileDelta pd = EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getFullID(), "25")
						.putAttribute(AKeySimple.CLIENT_STATUS.getFullID(), "idle"))
				.addGroup(AttributeGroupContainer.newBuilder().setGroupId("cpu1").setGroupType(1)
						.putAttribute(AKeyCPU.CPU_MODEL.getFullID(), "test cpu 1.0"))
				.build();
		profile.amalgamate(pd);

		assertEquals(profile.get(AKeySimple.CLIENT_CID), "25");
		assertEquals(profile.get(AKeySimple.CLIENT_STATUS), "idle");
		assertEquals(profile.getGroup(AKeyCPU.CPU_MODEL, "cpu1").get(AKeyCPU.CPU_MODEL), "test cpu 1.0");

		pd = EV_ProfileDelta.newBuilder().addGroup(
				AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_STATUS.getFullID(), "active"))
				.build();

		profile.amalgamate(pd);

		assertEquals(profile.get(AKeySimple.CLIENT_STATUS), "active");
	}

	@Test
	public void testGetUpdates() {
		profile.amalgamate(EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getFullID(), "35")
						.putAttribute(AKeySimple.CLIENT_STATUS.getFullID(), "idle"))
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

		profile.amalgamate(EV_ProfileDelta.newBuilder()
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.CLIENT_CID.getFullID(), "45"))
				.addGroup(AttributeGroupContainer.newBuilder().putAttribute(AKeySimple.OS_ACTIVE_WINDOW.getFullID(),
						"test"))
				.build());

		EV_ProfileDelta pd = profile.getUpdates(date);
		assertEquals(pd.getGroup(0).getAttributeCount(), 2);
		assertEquals(pd.getGroup(0).getAttributeOrDefault(AKeySimple.CLIENT_CID.getFullID(), ""), "45");
		assertEquals(pd.getGroup(0).getAttributeOrDefault(AKeySimple.OS_ACTIVE_WINDOW.getFullID(), ""), "test");
	}

	@Test
	public void testGetLastUpdate() {
		fail("Not yet implemented");
	}

}
