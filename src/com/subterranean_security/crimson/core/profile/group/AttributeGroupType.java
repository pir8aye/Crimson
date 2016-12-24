package com.subterranean_security.crimson.core.profile.group;

public enum AttributeGroupType {
	// CPU Attributes
	CPU_VENDOR, CPU_MODEL, CPU_CORES, CPU_CACHE, CPU_TEMP, CPU_TOTAL_USAGE, CPU_FREQUENCY, CPU_FREQUENCY_MAX,
	// GPU Attributes
	GPU_VENDOR, GPU_MODEL, GPU_RAM, GPU_TEMP,
	// Display Attributes
	DISP_ID, DISP_X, DISP_Y,
	// Network Interface Attributes
	NIC_NAME, NIC_IP, NIC_MAC, NIC_MASK, NIC_SPEED,
	// Filesystem Attributes
	FS;

	public static final AttributeGroupType[] ordinal = AttributeGroupType.values();
}
