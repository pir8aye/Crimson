package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.OS;

/**
 * Operating System attribute keys
 * 
 * @author cilki
 * @since 4.0.0
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
	public Object query() {
		switch (this) {
		case ACTIVE_WINDOW:
			return OS.getActiveWindow();
		case ARCH:
			return OS.getArch();
		case ENDIAN:
			return OS.getEndian();
		case FAMILY:
			return Platform.osFamily.toString();
		case LANGUAGE:
			return OS.getLanguage();
		case NAME:
			return OS.getName();
		case START_TIME:
			return OS.getStartTime();
		case TIMEZONE:
			return OS.getTimezone();
		case VIRTUALIZATION:
			return OS.getVirtualization();
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

	private static final int TYPE_ID = 5;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
