package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Motherboard attribute keys
 */
public enum AK_MOBO implements SingularKey {
	VENDOR, MODEL, TEMP;

	@Override
	public String toString() {
		switch (this) {
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

	private static final int TYPE_ID = 60;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
