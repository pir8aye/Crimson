package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * User attribute keys
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
