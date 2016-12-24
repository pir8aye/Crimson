package com.subterranean_security.crimson.core.profile;

public enum ComplexAttribute implements AbstractAttribute {
	// CPU
	CPU_MODEL, CPU_SPEED, CPU_TEMP, CPU_USAGE,
	// Network
	NET_INTERNALIP, NET_MESSAGEPING, NET_TRUEPING,
	// Displays
	DISP_NUMBER;

	@Override
	public String toString() {
		switch (this) {
		case CPU_MODEL:
			return "CPU Model";
		case CPU_SPEED:
			return "CPU Speed";
		case CPU_TEMP:
			return "CPU Temperature";
		case CPU_USAGE:
			return "CPU Usage";
		case NET_INTERNALIP:
			return "Internal IP";
		case NET_MESSAGEPING:
			return "Message Ping";
		case DISP_NUMBER:
			return "Number of Displays";
		default:
			break;
		}
		return super.toString();
	}
}
