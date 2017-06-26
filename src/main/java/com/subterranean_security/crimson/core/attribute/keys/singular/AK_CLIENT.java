package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * Client attribute keys
 */
public enum AK_CLIENT implements SingularKey {
	BASE_PATH, CID, CPU_USAGE, INSTALL_DATE, ONLINE, RAM_USAGE, STATUS, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case CID:
			return "Client ID";
		case VERSION:
			return "Crimson Version";
		case BASE_PATH:
			return "Install Path";
		case CPU_USAGE:
			return "Client CPU Usage";
		case INSTALL_DATE:
			return "Install Date";
		case ONLINE:
			return "Online";
		case RAM_USAGE:
			return "Client RAM Usage";
		case STATUS:
			return "Client Status";
		default:
			return super.toString();
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

	private static final int TYPE_ID = 4;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
