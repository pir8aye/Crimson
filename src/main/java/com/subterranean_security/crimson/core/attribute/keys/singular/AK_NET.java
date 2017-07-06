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

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.collect.singular.NET;

/**
 * Network attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_NET implements SingularKey {
	DEFAULT_GATEWAY, DNS1, DNS2, EXTERNAL_IPV4, FQDN, HOSTNAME;

	@Override
	public String toString() {
		switch (this) {
		case EXTERNAL_IPV4:
			return "External IP";
		case HOSTNAME:
			return "Hostname";
		case DEFAULT_GATEWAY:
			return "Default Gateway";
		case DNS1:
			return "DNS 1";
		case DNS2:
			return "DNS 2";
		case FQDN:
			return "Domain Name";
		default:
			return super.toString();
		}
	}

	@Override
	public Object query() {
		switch (this) {
		case DEFAULT_GATEWAY:
			return NET.getDefaultGateway();
		case DNS1:
			return NET.getDNS1();
		case DNS2:
			return NET.getDNS2();
		case EXTERNAL_IPV4:
			return NET.getExternalIP();
		case FQDN:
			return NET.getFQDN();
		case HOSTNAME:
			return NET.getHostname();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);

		}
	}

	@Override
	public Attribute fabricate() {
		switch (this) {
		case EXTERNAL_IPV4:
			return new TrackedAttribute();
		default:
			return SingularKey.super.fabricate();

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
		return TypeIndex.NET.ordinal();
	}

}
