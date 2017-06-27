package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.RAM;

/**
 * RAM attribute keys
 * 
 * @author cilki
 * @since 4.0.0
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
	public Object query() {
		switch (this) {
		case FREQUENCY:
			return RAM.getFrequency();
		case SIZE:
			return RAM.getSize();
		case TEMP:
			return RAM.getTemperature();
		case USAGE:
			return RAM.getUsage();
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

	private static final int TYPE_ID = 0;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
