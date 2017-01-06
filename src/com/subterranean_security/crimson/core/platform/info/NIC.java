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
package com.subterranean_security.crimson.core.platform.info;

import java.util.ArrayList;

import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.profile.group.GroupAttributeType;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;

public class NIC {
	private static final Logger log = LoggerFactory.getLogger(NIC.class);

	private NIC() {
	}

	/*
	 * SIGAR objects
	 */

	private static ArrayList<NetInterfaceConfig> config;
	private static ArrayList<NetInterfaceStat> stat;

	public static void initialize() {
		String[] everyNic = null;
		try {
			everyNic = SigarStore.getSigar().getNetInterfaceList();
		} catch (SigarException e) {
			log.error("Failed to obtain collection object for network interfaces");
			return;
		}

		for (String s : everyNic) {
			NetInterfaceConfig nic = new NetInterfaceConfig();
			NetInterfaceStat nis = new NetInterfaceStat();

			try {
				nic.gather(SigarStore.getSigar(), s);
				nis.gather(SigarStore.getSigar(), s);
			} catch (SigarException e) {
				log.error("Failed to obtain collection object for network interface: {}", s);
				return;
			}

			if (!nic.getAddress().equals("0.0.0.0") || !containsMAC(nic.getHwaddr())) {
				config.add(nic);
				stat.add(nis);
			}

		}

	}

	public static boolean containsMAC(String mac) {
		for (NetInterfaceConfig nic : config) {
			if (nic.getHwaddr().equals(mac)) {
				return true;
			}
		}
		return false;
	}

	public static int getNicCount() {
		return config.size();
	}

	public static String computeGID(int i) {
		return GroupAttributeType.NIC.ordinal() + getMAC(i);
	}

	public static void refresh(int i) {
		try {
			stat.get(i).gather(SigarStore.getSigar(), config.get(i).getName());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Information retrieval
	 */

	public static long getTxBytes(int i) {
		refresh(i);
		return stat.get(i).getTxBytes();
	}

	public static long getTxPackets(int i) {
		refresh(i);
		return stat.get(i).getTxPackets();
	}

	public static long getRxBytes(int i) {
		refresh(i);
		return stat.get(i).getRxBytes();
	}

	public static long getRxPackets(int i) {
		refresh(i);
		return stat.get(i).getRxPackets();
	}

	public static String getIP(int i) {
		return config.get(i).getAddress();
	}

	public static String getNetmask(int i) {
		return config.get(i).getNetmask();
	}

	public static String getMAC(int i) {
		return config.get(i).getHwaddr();
	}

	public static String getDescription(int i) {
		return config.get(i).getDescription();
	}

	public static ArrayList<AttributeGroupContainer> getAttributes() {
		ArrayList<AttributeGroupContainer> a = new ArrayList<AttributeGroupContainer>();
		for (int i = 0; i < getNicCount(); i++) {
			AttributeGroupContainer.Builder template = AttributeGroupContainer.newBuilder()
					.setGroupType(GroupAttributeType.NIC.ordinal()).setGroupId(computeGID(i));

			a.add(template.setAttributeType(AttributeGroupType.NIC_IP.ordinal()).setValue(getIP(i)).build());
			a.add(template.setAttributeType(AttributeGroupType.NIC_MAC.ordinal()).setValue(getMAC(i)).build());
			a.add(template.setAttributeType(AttributeGroupType.NIC_MASK.ordinal()).setValue(getNetmask(i)).build());
			a.add(template.setAttributeType(AttributeGroupType.NIC_DESC.ordinal()).setValue(getDescription(i)).build());
		}
		return a;
	}

}
