package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * IP address location attribute keys
 */
public enum AK_LOC implements SingularKey {
	IPLOC_CITY, IPLOC_COUNTRY, IPLOC_COUNTRYCODE, IPLOC_LATITUDE, IPLOC_LONGITUDE, IPLOC_REGION;

	@Override
	public String toString() {
		switch (this) {
		case IPLOC_COUNTRY:
			return "IP Location";
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

	private static final int TYPE_ID = 61;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
