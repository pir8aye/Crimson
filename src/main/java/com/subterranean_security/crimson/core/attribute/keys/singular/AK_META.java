package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Meta attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_META implements SingularKey {
	FIRST_CONTACT;

	@Override
	public String toSuperString() {
		return super.toString();
	}

	private static final int TYPE_ID = 55;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

	@Override
	public int getConstID() {
		return this.ordinal();
	}

	@Override
	public Object query() {
		return null;
	}

}
