package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Windows attribute keys
 */
public enum AK_WIN implements SingularKey {
	IE_VERSION, INSTALL_DATE, POWERSHELL_VERSION, SERIAL;

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case IE_VERSION:
		case INSTALL_DATE:
		case POWERSHELL_VERSION:
		case SERIAL:
			return os == OSFAMILY.WIN;
		default:
			return SingularKey.super.isHeaderable();
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case IE_VERSION:
			return "Internet Explorer Version";
		case INSTALL_DATE:
			return "Install Date";
		case POWERSHELL_VERSION:
			return "Powershell version";
		case SERIAL:
			return "Windows Serial Number";
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

	private static final int TYPE_ID = 56;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}
}
