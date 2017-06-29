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
package com.subterranean_security.crimson.core.platform.collect.plural;

import java.util.ArrayList;

import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_NIC;
import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.platform.collect.Collector;
import com.subterranean_security.crimson.core.struct.stat_stream.StatStream;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class NIC extends Collector {
	private static final Logger log = LoggerFactory.getLogger(NIC.class);

	private NetInterfaceConfig config;
	private NetInterfaceStat stat;

	// TODO
	private StatStream rxSpeed;
	private StatStream txSpeed;

	public NIC(NetInterfaceConfig config, NetInterfaceStat stat) {
		this.config = config;
		this.stat = stat;

		rxSpeed = new StatStream(1000, 60);
		txSpeed = new StatStream(1000, 60);
	}

	public void refresh() {
		try {
			stat.gather(SigarStore.getSigar(), config.getName());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getTxBytes() {
		refresh(i);
		return stat.getTxBytes();
	}

	public long getTxPackets() {
		refresh(i);
		return stat.getTxPackets();
	}

	public long getRxBytes() {
		refresh(i);
		return stat.getRxBytes();
	}

	public long getRxPackets() {
		refresh(i);
		return stat.getRxPackets();
	}

	public double getRxSpeed() {
		return rxSpeed.getInstantaneousSpeed();
	}

	public double getTxSpeed() {
		return txSpeed.getInstantaneousSpeed();
	}

	public String getIpv4() {
		return config.getAddress();
	}

	public String getNetmask() {
		return config.getNetmask();
	}

	public String getMAC() {
		return config.getHwaddr();
	}

	public String getDescription() {
		return config.getDescription();
	}

	public String getName() {
		return config.getName();
	}

}
