package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.MOBO;

/**
 * Motherboard attribute keys
 * 
 * @author
 * @since 4.0.0
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
	public Object query() {
		switch (this) {
		case MODEL:
			return MOBO.getModel();
		case VENDOR:
			return MOBO.getManufacturer();
		case TEMP:
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

	private static final int TYPE_ID = 60;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
