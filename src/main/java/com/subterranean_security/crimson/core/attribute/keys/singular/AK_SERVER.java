package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Server attribute keys
 */
public enum AK_SERVER implements SingularKey {
	ACTIVE_LISTENERS, CONNECTED_CLIENTS, CONNECTED_VIEWERS, INACTIVE_LISTENERS, TOTAL_CLIENTS, TOTAL_VIEWERS;

	@Override
	public String toString() {
		switch (this) {
		}
		return super.toString();
	}

	@Override
	public boolean isHeaderable() {
		switch (this) {
		case CONNECTED_CLIENTS:
		case CONNECTED_VIEWERS:
		case TOTAL_CLIENTS:
		case TOTAL_VIEWERS:
		case ACTIVE_LISTENERS:
		case INACTIVE_LISTENERS:
			return false;
		default:
			return true;
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case CONNECTED_CLIENTS:
		case CONNECTED_VIEWERS:
		case TOTAL_CLIENTS:
		case TOTAL_VIEWERS:
		case ACTIVE_LISTENERS:
		case INACTIVE_LISTENERS:
			return instance == Instance.SERVER || instance == Instance.VIEWER;
		default:
			return SingularKey.super.isCompatible(os, instance);
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

	private static final int TYPE_ID = 6;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
