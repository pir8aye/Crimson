package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Network attribute keys
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
		}
		return super.toString();
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

	private static final int TYPE_ID = 59;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
