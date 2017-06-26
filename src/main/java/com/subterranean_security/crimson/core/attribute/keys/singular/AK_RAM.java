package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * RAM attribute keys
 */
public enum AK_RAM implements SingularKey {
	FREQUENCY, SIZE, TEMP, USAGE;

	@Override
	public String toString() {
		switch (this) {
		case FREQUENCY:
			return "RAM Frequency";
		case SIZE:
			return "RAM Size";
		case USAGE:
			return "RAM Used";
		case TEMP:
			return "RAM Temperature";
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

	private static final int TYPE_ID = 0;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
