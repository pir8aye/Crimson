package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.USER;

/**
 * User attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_USER implements SingularKey {
	HOME, NAME, STATUS;

	@Override
	public String toString() {
		switch (this) {
		case NAME:
			return "Username";
		case HOME:
			return "User Home";
		case STATUS:
			return "User Status";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case HOME:
			return USER.getHome();
		case NAME:
			return USER.getName();
		case STATUS:
			return USER.getStatus();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

	@Override
	public String toSuperString() {
		return super.toString();
	}

	@Override
	public int getConstID() {
		return this.ordinal();
	}

	private static final int TYPE_ID = 9;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
