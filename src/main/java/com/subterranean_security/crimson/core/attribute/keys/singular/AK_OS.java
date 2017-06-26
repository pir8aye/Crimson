package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Operating System attribute keys
 */
public enum AK_OS implements SingularKey {
	ACTIVE_WINDOW, ARCH, ENDIAN, FAMILY, LANGUAGE, NAME, START_TIME, TIMEZONE, VIRTUALIZATION;

	@Override
	public String toString() {
		switch (this) {
		case LANGUAGE:
			return "Language";
		case NAME:
			return "OS Name";
		case ARCH:
			return "OS Architecture";
		case FAMILY:
			return "OS Family";
		case TIMEZONE:
			return "Timezone";
		case ACTIVE_WINDOW:
			return "Active Window";
		case VIRTUALIZATION:
			return "Virtualization";
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

	private static final int TYPE_ID = 5;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
