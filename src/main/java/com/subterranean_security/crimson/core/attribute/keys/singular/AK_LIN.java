package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.LIN;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Linux attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_LIN implements SingularKey {
	DISTRO, KERNEL_VERSION, PACKAGES, SHELL, TERMINAL, WM;

	@Override
	public String toString() {
		switch (this) {
		case DISTRO:
			return "Linux Distribution";
		case KERNEL_VERSION:
			return "Kernel Version";
		case PACKAGES:
			return "Packages";
		case SHELL:
			return "Shell";
		case WM:
			return "Window Manager/Desktop Environment";
		case TERMINAL:
			return "Terminal";
		}
		return super.toString();
	}

	@Override
	public Object query() {
		switch (this) {
		case DISTRO:
			return LIN.getDistro();
		case KERNEL_VERSION:
			return LIN.getKernel();
		case PACKAGES:
			return LIN.getPackages();
		case SHELL:
			return LIN.getShell();
		case TERMINAL:
			return LIN.getTerminal();
		case WM:
			return LIN.getWM();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case DISTRO:
		case KERNEL_VERSION:
		case PACKAGES:
		case SHELL:
		case WM:
		case TERMINAL:
			return os == OSFAMILY.LIN;
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

	private static final int TYPE_ID = 62;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
