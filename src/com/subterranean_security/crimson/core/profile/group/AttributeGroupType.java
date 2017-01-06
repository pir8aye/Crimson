/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.core.profile.group;

public enum AttributeGroupType {
	// CPU Attributes
	CPU_VENDOR, CPU_MODEL, CPU_CORES, CPU_CACHE, CPU_TEMP, CPU_TOTAL_USAGE, CPU_FREQUENCY, CPU_FREQUENCY_MAX,
	// GPU Attributes
	GPU_VENDOR, GPU_MODEL, GPU_RAM, GPU_TEMP,
	// Display Attributes
	DISP_ID, DISP_TYPE, DISP_WIDTH, DISP_HEIGHT, DISP_MEMORY, DISP_REFRESH_RATE, DISP_BIT_DEPTH,
	// Network Interface Attributes
	NIC_NAME, NIC_DESC, NIC_IP, NIC_MAC, NIC_MASK, NIC_RX_SPEED, NIC_TX_SPEED, NIC_RX_BYTES, NIC_TX_BYTES, NIC_RX_PACKETS, NIC_TX_PACKETS,
	// Filesystem Attributes
	FS;

	public static final AttributeGroupType[] ordinal = AttributeGroupType.values();
}
