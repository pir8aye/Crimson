package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Viewer attribute keys
 */
public enum AK_Viewer implements SingularKey {
	LOGIN_IP, LOGIN_TIME, USER;

	@Override
	public Attribute fabricate() {
		switch (this) {
		case LOGIN_IP:
		case LOGIN_TIME:
			return new TrackedAttribute();
		default:
			return SingularKey.super.fabricate();

		}
	}

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

	private static final int TYPE_ID = 57;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
