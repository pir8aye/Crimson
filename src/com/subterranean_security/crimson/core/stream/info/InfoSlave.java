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
package com.subterranean_security.crimson.core.stream.info;

import java.util.ArrayList;
import java.util.List;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.attribute.keys.AKeyCPU;
import com.subterranean_security.crimson.core.attribute.keys.AKeyNIC;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.misc.StatStream;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.CRIMSON;
import com.subterranean_security.crimson.core.platform.info.NIC;
import com.subterranean_security.crimson.core.platform.info.RAM;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.server.store.ListenerStore;

public abstract class InfoSlave extends Stream {

	/**
	 * The last profile delta. Subsequent updates use this as a base.
	 */
	private EV_ProfileDelta.Builder pd;
	private AttributeGroupContainer.Builder lastGeneralContainer;

	private AttributeGroupContainer.Builder lastCpuContainer;
	private int whichCPU = 0;

	private StatStream rxSpeed;

	private StatStream txSpeed;
	private AttributeGroupContainer.Builder lastNicContainer;
	private int whichNIC = 0;

	private List<AttributeKey> keys;

	public InfoSlave(Param p) {
		param = p;
		initKeys(p.getInfoParam().getKeyList());

		pd = EV_ProfileDelta.newBuilder().setCvid(Common.cvid);

		initializeContainers();

		for (AttributeKey key : keys) {
			if (key instanceof AKeyCPU) {
				initializeCPU();
				break;
			}
		}

		for (AttributeKey key : keys) {
			if (key instanceof AKeyNIC) {
				initializeNIC();
				break;
			}
		}

		start();
	}

	private void initKeys(List<Integer> list) {
		keys = new ArrayList<AttributeKey>();
		for (int id : list) {
			keys.add(AttributeKey.getKey(id));
		}
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.stream()).setVID(Common.cvid).build());
	}

	private void initializeContainers() {
		lastCpuContainer = AttributeGroupContainer.newBuilder().setGroupType(AttributeKey.Type.CPU.ordinal());
		lastNicContainer = AttributeGroupContainer.newBuilder().setGroupType(AttributeKey.Type.NIC.ordinal());
		lastGeneralContainer = AttributeGroupContainer.newBuilder().setGroupType(AttributeKey.Type.GENERAL.ordinal())
				.setGroupId("");
	}

	private void initializeCPU() {
		String cpuID = null;
		if (param.getInfoParam().hasCpuId()) {
			cpuID = param.getInfoParam().getCpuId();
			for (int i = 0; i < CPU.getCount(); i++) {
				if (cpuID.equals(CPU.computeGID(i))) {
					whichCPU = i;
					break;
				}
			}
			// TODO check if for loop actually found the correct row
			// If not, the supplied cpuID is no longer attached
		} else {
			// calculate the primary cpu id
			cpuID = CPU.computeGID(0);
			whichCPU = 0;
		}

		lastCpuContainer.setGroupId(cpuID);

	}

	private void initializeNIC() {
		String nicID = null;
		if (param.getInfoParam().hasNicId()) {
			nicID = param.getInfoParam().getNicId();
			for (int i = 0; i < NIC.getCount(); i++) {
				if (nicID.equals(NIC.computeGID(i))) {
					whichNIC = i;
					break;
				}
			}
			// TODO check if for loop actually found the correct row
			// If not, the supplied nicID is no longer attached
		} else {
			// calculate the primary nic id
			nicID = NIC.computeGID(0);
			whichNIC = 0;
		}

		lastNicContainer.setGroupId(nicID);

		if (keys.contains(AKeyNIC.NIC_RX_BYTES) || keys.contains(AKeyNIC.NIC_RX_SPEED)) {
			rxSpeed = new StatStream(1000, 60);
		}

		if (keys.contains(AKeyNIC.NIC_TX_BYTES) || keys.contains(AKeyNIC.NIC_TX_SPEED)) {
			txSpeed = new StatStream(1000, 60);
		}
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	protected EV_ProfileDelta gather() {

		// purge last attribute groups
		pd.clearGroup();

		if (keys.contains(AKeyNIC.NIC_RX_BYTES)) {
			long l = NIC.getRxBytes(whichNIC);
			rxSpeed.addPoint(l);

			poll(lastNicContainer, AKeyNIC.NIC_RX_BYTES, UnitTranslator.translateNicOutput(l));
		}

		if (keys.contains(AKeyNIC.NIC_TX_BYTES)) {
			long l = NIC.getTxBytes(whichNIC);
			txSpeed.addPoint(l);

			poll(lastNicContainer, AKeyNIC.NIC_TX_BYTES, UnitTranslator.translateNicOutput(l));
		}

		if (keys.contains(AKeyNIC.NIC_RX_SPEED))
			poll(lastNicContainer, AKeyNIC.NIC_RX_SPEED, UnitTranslator.nicSpeed(keys.contains(AKeyNIC.NIC_RX_BYTES)
					? rxSpeed.getInstantaneousSpeed() : rxSpeed.addPoint(NIC.getRxBytes(whichNIC))));
		if (keys.contains(AKeyNIC.NIC_TX_SPEED))
			poll(lastNicContainer, AKeyNIC.NIC_TX_SPEED, UnitTranslator.nicSpeed(keys.contains(AKeyNIC.NIC_TX_BYTES)
					? txSpeed.getInstantaneousSpeed() : txSpeed.addPoint(NIC.getTxBytes(whichNIC))));
		if (keys.contains(AKeyNIC.NIC_RX_PACKETS))
			poll(lastNicContainer, AKeyNIC.NIC_RX_PACKETS, "" + NIC.getRxPackets(whichNIC));
		if (keys.contains(AKeyNIC.NIC_TX_PACKETS))
			poll(lastNicContainer, AKeyNIC.NIC_TX_PACKETS, "" + NIC.getTxPackets(whichNIC));
		if (keys.contains(AKeyCPU.CPU_TOTAL_USAGE))
			poll(lastCpuContainer, AKeyCPU.CPU_TOTAL_USAGE, CPU.getTotalUsage(whichCPU));
		if (keys.contains(AKeyCPU.CPU_TEMP))
			poll(lastCpuContainer, AKeyCPU.CPU_TEMP, CPU.getTemp());
		if (keys.contains(AKeySimple.OS_ACTIVE_WINDOW))
			poll(lastGeneralContainer, AKeySimple.OS_ACTIVE_WINDOW, Native.getActiveWindow());
		if (keys.contains(AKeySimple.CLIENT_STATUS))
			poll(lastGeneralContainer, AKeySimple.CLIENT_STATUS, CRIMSON.getStatus());
		if (keys.contains(AKeySimple.RAM_USAGE))
			poll(lastGeneralContainer, AKeySimple.RAM_USAGE, RAM.getUsage());
		if (keys.contains(AKeySimple.CLIENT_RAM_USAGE))
			poll(lastGeneralContainer, AKeySimple.CLIENT_RAM_USAGE, RAM.getClientUsage());
		if (keys.contains(AKeySimple.CLIENT_CPU_USAGE))
			poll(lastGeneralContainer, AKeySimple.CLIENT_CPU_USAGE, CPU.getClientUsage());
		if (keys.contains(AKeySimple.SERVER_STATUS))
			poll(lastGeneralContainer, AKeySimple.SERVER_STATUS, ListenerStore.isRunning() ? "1" : "0");
		if (keys.contains(AKeySimple.SERVER_CONNECTED_CLIENTS))
			poll(lastGeneralContainer, AKeySimple.SERVER_CONNECTED_CLIENTS, "" + ConnectionStore.countClients());
		if (keys.contains(AKeySimple.SERVER_CONNECTED_VIEWERS))
			poll(lastGeneralContainer, AKeySimple.SERVER_CONNECTED_VIEWERS, "" + ConnectionStore.countUsers());

		if (lastGeneralContainer.getAttributeCount() > 0)
			pd.addGroup(lastGeneralContainer);
		if (lastCpuContainer.getAttributeCount() > 0)
			pd.addGroup(lastCpuContainer);
		if (lastNicContainer.getAttributeCount() > 0)
			pd.addGroup(lastNicContainer);

		System.out.println("Gathered info for " + pd.getGroupCount() + " groups");

		return pd.build();
	}

	private void poll(AttributeGroupContainer.Builder container, AttributeKey key, String value) {
		if (container.getAttributeOrDefault(key.getFullID(), "").equals(value)) {
			container.removeAttribute(key.getFullID());
		} else {
			container.putAttribute(key.getFullID(), value);
		}
	}

	@Override
	public void start() {
		timer.schedule(sendTask, 0, param.hasPeriod() ? param.getPeriod() : 1000);
	}

	@Override
	public void stop() {
		timer.cancel();
	}

}
