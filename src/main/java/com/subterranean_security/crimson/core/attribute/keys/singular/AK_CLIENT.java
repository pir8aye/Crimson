package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.CRIMSON;
import com.subterranean_security.crimson.core.platform.info.RAM;

/**
 * Client attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_CLIENT implements SingularKey {
	BASE_PATH, CPU_USAGE, INSTALL_DATE, RAM_USAGE, STATUS;

	@Override
	public String toString() {
		switch (this) {
		case BASE_PATH:
			return "Install Path";
		case CPU_USAGE:
			return "Client CPU Usage";
		case INSTALL_DATE:
			return "Install Date";
		case RAM_USAGE:
			return "Client RAM Usage";
		case STATUS:
			return "Client Status";
		default:
			return super.toString();
		}
	}

	@Override
	public Object query() {
		switch (this) {
		case BASE_PATH:
			return CRIMSON.getBasePath();
		case CPU_USAGE:
			return CPU.getClientUsage();
		case INSTALL_DATE:
			return CRIMSON.getInstallDate();
		case RAM_USAGE:
			return RAM.getClientUsage();
		case STATUS:
			return CRIMSON.getStatus();
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

	private static final int TYPE_ID = 4;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
