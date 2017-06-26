package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Java attribute keys
 */
public enum AK_JVM implements SingularKey {
	ARCH, PATH, START_TIME, VENDOR, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case VERSION:
			return "Java Version";
		case ARCH:
			return "Java Architecture";
		case PATH:
			return "Java Path";
		case START_TIME:
			return "Java Start Time";
		case VENDOR:
			return "Java Vendor";
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

	private static final int TYPE_ID = 63;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
