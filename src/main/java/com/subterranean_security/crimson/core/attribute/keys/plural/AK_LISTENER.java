package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.keys.PluralKey;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Server listener attributes
 * 
 * @author cilki
 * @since 5.0.0
 */
public enum AK_LISTENER implements PluralKey {
	ID, // Listener ID
	PORT, // Listening Port
	NAME, OWNER, UPNP, CLIENT_ACCEPTOR, VIEWER_ACCEPTOR, CERTIFICATE, PRIVATE_KEY;

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return instance == Instance.SERVER;
	}

	@Override
	public Object query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSuperString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGroupID(int groupID) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGroupID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTypeID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getConstID() {
		// TODO Auto-generated method stub
		return 0;
	}
}
