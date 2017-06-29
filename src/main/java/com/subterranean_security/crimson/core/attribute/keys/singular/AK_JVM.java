package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.collect.singular.JVM;

/**
 * Java attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_JVM implements SingularKey {
	ARCH, PATH, START_TIME, VENDOR, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case VERSION:
			return "Java Version";
		case ARCH:
			return "Java Architecture";
		case PATH:
			return "Java Path";
		case START_TIME:
			return "Java Start Time";
		case VENDOR:
			return "Java Vendor";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case ARCH:
			return JVM.getArch();
		case PATH:
			return JVM.getHome();
		case START_TIME:
			return JVM.getStartTime();
		case VENDOR:
			return JVM.getVendor();
		case VERSION:
			return JVM.getVersion();
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
		return TypeIndex.JVM.ordinal();
	}

}
