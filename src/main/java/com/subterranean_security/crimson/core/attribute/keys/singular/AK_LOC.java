/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
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
