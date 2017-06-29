package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.collect.singular.IPLOC;

/**
 * IP address location attribute keys
 * 
 * @author cilki
 * @since 4.0.0
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
	public Object query() {
		switch (this) {
		case IPLOC_CITY:
			return IPLOC.getCity();
		case IPLOC_COUNTRY:
			return IPLOC.getCountry();
		case IPLOC_COUNTRYCODE:
			return IPLOC.getCountryCode();
		case IPLOC_LATITUDE:
			return IPLOC.getLatitude();
		case IPLOC_LONGITUDE:
			return IPLOC.getLongitude();
		case IPLOC_REGION:
			return IPLOC.getRegion();
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

	@Override
	public int getTypeID() {
		return TypeIndex.LOC.ordinal();
	}

}
