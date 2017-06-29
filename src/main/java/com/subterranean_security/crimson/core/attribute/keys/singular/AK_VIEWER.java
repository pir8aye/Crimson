package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Viewer attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_VIEWER implements SingularKey {
	LOGIN_IP, LOGIN_TIME, USER, PERMISSIONS;

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

	@Override
	public int getTypeID() {
		return TypeIndex.VIEWER.ordinal();
	}

	@Override
	public Object query() {
		switch (this) {
		case LOGIN_IP:
			break;
		case LOGIN_TIME:
			break;
		case USER:
			break;
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
		return null;
	}
}
