package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.keys.PluralKey;

public enum AK_AUTH implements PluralKey {
	ID, NAME, TYPE, CREATION_DATE,

	// Key authentication
	KEY_SEED,

	// Password authentication
	PASSWORD;

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
