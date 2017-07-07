/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.PluralKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.platform.collect.plural.NIC;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.core.store.CollectorStore;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * NIC attribute keys
 */
public enum AK_NIC implements PluralKey {
	DESC, IPV4, MAC, NAME, NETMASK, RX_BYTES, RX_PACKETS, RX_SPEED, TX_BYTES, TX_PACKETS, TX_SPEED;

	@Override
	public Attribute fabricate() {
		return new UntrackedAttribute();
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	@Override
	public boolean isHeaderable() {
		return true;
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
		return TypeIndex.NIC.ordinal();
	}

	@Override
	public int getGroupID() {
		return groupID;
	}

	@Override
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	private int groupID;

	@Override
	public String toString() {
		switch (this) {
		case DESC:
			return "Description";
		case IPV4:
			return "IPV4 Address";
		case MAC:
			return "MAC Address";
		case NAME:
			return "Interface Name";
		case NETMASK:
			return "Subnet Mask";
		case RX_BYTES:
			return "Bytes Received";
		case RX_PACKETS:
			return "Packets Received";
		case RX_SPEED:
			return "Receive Speed";
		case TX_BYTES:
			return "Bytes Transmitted";
		case TX_PACKETS:
			return "Packets Transmitted";
		case TX_SPEED:
			return "Transmit Speed";
		default:
			return super.toString();
		}
	}

	@Override
	public Object query() {
		if (groupID == 0)
			throw new IllegalStateException(
					"Failed to query attribute because groupID cannot be 0 for plural attributes");

		NIC collector = (NIC) CollectorStore.getCollector(getGTID());

		if (collector == null)
			throw new IllegalStateException(
					"Failed to query attribute because GTID (" + getGTID() + ") does not exist");

		switch (this) {
		case DESC:
			return collector.getDescription();
		case IPV4:
			return collector.getIpv4();
		case MAC:
			return collector.getMAC();
		case NAME:
			return collector.getName();
		case NETMASK:
			return collector.getNetmask();
		case RX_BYTES:
			return collector.getRxBytes();
		case RX_PACKETS:
			return collector.getRxPackets();
		case RX_SPEED:
			return collector.getRxSpeed();
		case TX_BYTES:
			return collector.getTxBytes();
		case TX_PACKETS:
			return collector.getTxPackets();
		case TX_SPEED:
			return collector.getTxSpeed();
		default:
			return null;

		}
	}
}
