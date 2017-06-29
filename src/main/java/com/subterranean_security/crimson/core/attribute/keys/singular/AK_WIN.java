package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.collect.singular.WIN;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Windows attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_WIN implements SingularKey {
	IE_VERSION, INSTALL_DATE, POWERSHELL_VERSION, SERIAL;

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return os == OSFAMILY.WIN;
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
	public Object query() {
		switch (this) {
		case IE_VERSION:
			return WIN.getIEVersion();
		case INSTALL_DATE:
			return WIN.getInstallTime();
		case POWERSHELL_VERSION:
			return WIN.getPowerShellVersion();
		case SERIAL:
			return WIN.getSerial();
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

	@Override
	public int getTypeID() {
		return TypeIndex.WIN.ordinal();
	}

}
