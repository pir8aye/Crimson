package com.subterranean_security.crimson.core.util;

public class Native {
	public static native String getActiveWindow();

	public static native long getSystemUptime();

	public static native long getCpuTemp();

	public static native void poweroff();

	public static native void restart();

	public static native void standby();

	public static native void hibernate();

	public static native String execute(String cmd);

}
