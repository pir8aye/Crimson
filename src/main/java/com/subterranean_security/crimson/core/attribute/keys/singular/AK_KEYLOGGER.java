package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Keylogger attribute keys
 */
public enum AK_KEYLOGGER implements SingularKey {
	STATE, TRIGGER, TRIGGER_VALUE;

	@Override
	public String toString() {
		switch (this) {
		case STATE:
			return "Keylogger State";
		case TRIGGER:
			return "Keylogger Trigger";
		case TRIGGER_VALUE:
			return "Keylogger Trigger Value";
		default:
			return super.toString();
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case STATE:
		case TRIGGER:
		case TRIGGER_VALUE:
			return instance == Instance.CLIENT;
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

	private static final int TYPE_ID = 7;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
