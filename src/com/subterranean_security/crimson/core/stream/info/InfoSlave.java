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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.platform.info.CRIMSON;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.NIC;
import com.subterranean_security.crimson.core.platform.info.RAM;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.profile.group.GroupAttributeType;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.StatStream;

public abstract class InfoSlave extends Stream {

	/**
	 * The last profile delta. Subsequent updates use this as a base.
	 */
	private EV_ProfileDelta.Builder pd;
	private AttributeGroupContainer.Builder lastCpuUsage;
	private AttributeGroupContainer.Builder lastCpuTemp;
	private int whichCPU = 0;

	private StatStream rxSpeed;
	private AttributeGroupContainer.Builder lastRxSpeed;
	private AttributeGroupContainer.Builder lastRxBytes;
	private AttributeGroupContainer.Builder lastRxPackets;

	private StatStream txSpeed;
	private AttributeGroupContainer.Builder lastTxSpeed;
	private AttributeGroupContainer.Builder lastTxBytes;
	private AttributeGroupContainer.Builder lastTxPackets;
	private int whichNIC = 0;

	public InfoSlave(Param p) {
		param = p;
		pd = EV_ProfileDelta.newBuilder().setCvid(Common.cvid);

		if (param.getInfoParam().hasCpuUsage() || param.getInfoParam().hasCpuTemp()) {
			initializeCPU();
		}

		if (param.getInfoParam().hasNicRxBytes() || param.getInfoParam().hasNicRxPackets()
				|| param.getInfoParam().hasNicRxSpeed() || param.getInfoParam().hasNicTxBytes()
				|| param.getInfoParam().hasNicTxPackets() || param.getInfoParam().hasNicTxSpeed()) {
			initializeNIC();
		}

		start();
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.getStreamid()).setVID(Common.cvid).build());
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

		if (param.getInfoParam().hasCpuUsage()) {
			lastCpuUsage = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.CPU.ordinal())
					.setGroupId(cpuID).setAttributeType(AttributeGroupType.CPU_TOTAL_USAGE.ordinal());
		}

		if (param.getInfoParam().hasCpuTemp()) {
			lastCpuTemp = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.CPU.ordinal())
					.setGroupId(cpuID).setAttributeType(AttributeGroupType.CPU_TEMP.ordinal());
		}
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

		if (param.getInfoParam().hasNicRxBytes()) {
			lastRxBytes = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_RX_BYTES.ordinal());
		}

		if (param.getInfoParam().hasNicRxPackets()) {
			lastRxPackets = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_RX_PACKETS.ordinal());
		}

		if (param.getInfoParam().hasNicRxSpeed()) {
			lastRxSpeed = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_RX_SPEED.ordinal());
		}

		if (param.getInfoParam().hasNicTxBytes()) {
			lastTxBytes = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_TX_BYTES.ordinal());
		}

		if (param.getInfoParam().hasNicTxPackets()) {
			lastTxPackets = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_TX_PACKETS.ordinal());
		}

		if (param.getInfoParam().hasNicTxSpeed()) {
			lastTxSpeed = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.NIC.ordinal())
					.setGroupId(nicID).setAttributeType(AttributeGroupType.NIC_TX_SPEED.ordinal());
		}

		if (param.getInfoParam().hasNicRxBytes() || param.getInfoParam().hasNicRxSpeed()) {
			rxSpeed = new StatStream(1000, 60);
		}

		if (param.getInfoParam().hasNicTxBytes() || param.getInfoParam().hasNicTxSpeed()) {
			txSpeed = new StatStream(1000, 60);
		}
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	protected EV_ProfileDelta gather() {

		// purge last attribute groups
		pd.clearGroupAttr();

		// cpu usage
		if (param.getInfoParam().hasCpuUsage()) {
			String coreUsage = CPU.getTotalUsage(whichCPU);
			if (!lastCpuUsage.getValue().equals(coreUsage)) {
				pd.addGroupAttr(lastCpuUsage.setValue(coreUsage));
			}
		}

		// cpu temps
		if (param.getInfoParam().hasCpuTemp()) {
			String cpuTemp = CPU.getTemp();
			if (!lastCpuTemp.getValue().equals(cpuTemp)) {
				pd.addGroupAttr(lastCpuTemp.setValue(cpuTemp));
			}
		}

		// nic rx bytes
		if (param.getInfoParam().hasNicRxBytes()) {
			long l = NIC.getRxBytes(whichNIC);
			rxSpeed.addPoint(l);
			String nicRxBytes = CUtil.UnitTranslator.translateNicOutput(l);
			if (!lastRxBytes.getValue().equals(nicRxBytes)) {
				pd.addGroupAttr(lastRxBytes.setValue(nicRxBytes));
			}
		}

		// nic rx speed
		if (param.getInfoParam().hasNicRxSpeed()) {
			String nicRxSpeed = CUtil.UnitTranslator.translateNicSpeed(param.getInfoParam().hasNicRxBytes()
					? rxSpeed.getInstantaneousSpeed() : rxSpeed.addPoint(NIC.getRxBytes(whichNIC)));
			if (!lastRxSpeed.getValue().equals(nicRxSpeed)) {
				pd.addGroupAttr(lastRxSpeed.setValue(nicRxSpeed));
			}
		}

		// nic rx packets
		if (param.getInfoParam().hasNicRxPackets()) {
			String nicRxPackets = "" + NIC.getRxPackets(whichNIC);
			if (!lastRxPackets.getValue().equals(nicRxPackets)) {
				pd.addGroupAttr(lastRxPackets.setValue(nicRxPackets));
			}
		}

		// nic tx bytes
		if (param.getInfoParam().hasNicTxBytes()) {
			long l = NIC.getTxBytes(whichNIC);
			txSpeed.addPoint(l);
			String nicTxBytes = CUtil.UnitTranslator.translateNicOutput(l);
			if (!lastTxBytes.getValue().equals(nicTxBytes)) {
				pd.addGroupAttr(lastTxBytes.setValue(nicTxBytes));
			}
		}

		// nic tx speed
		if (param.getInfoParam().hasNicTxSpeed()) {
			String nicTxSpeed = CUtil.UnitTranslator.translateNicSpeed(param.getInfoParam().hasNicTxBytes()
					? txSpeed.getInstantaneousSpeed() : txSpeed.addPoint(NIC.getTxBytes(whichNIC)));
			if (!lastTxSpeed.getValue().equals(nicTxSpeed)) {
				pd.addGroupAttr(lastTxSpeed.setValue(nicTxSpeed));
			}
		}

		// nic tx packets
		if (param.getInfoParam().hasNicTxPackets()) {
			String nicTxPackets = "" + NIC.getTxPackets(whichNIC);
			if (!lastTxPackets.getValue().equals(nicTxPackets)) {
				pd.addGroupAttr(lastTxPackets.setValue(nicTxPackets));
			}
		}

		// active window
		if (param.getInfoParam().hasActiveWindow()) {
			String activeWindow = Native.getActiveWindow();
			if (!pd.getStrAttrOrDefault(SimpleAttribute.OS_ACTIVE_WINDOW.ordinal(), "").equals(activeWindow)) {
				pd.putStrAttr(SimpleAttribute.OS_ACTIVE_WINDOW.ordinal(), activeWindow);
			} else {
				pd.removeStrAttr(SimpleAttribute.OS_ACTIVE_WINDOW.ordinal());
			}
		}

		// client status
		if (param.getInfoParam().hasClientStatus()) {
			String clientStatus = CRIMSON.getStatus();
			if (!pd.getStrAttrOrDefault(SimpleAttribute.CLIENT_STATUS.ordinal(), "").equals(clientStatus)) {
				pd.putStrAttr(SimpleAttribute.CLIENT_STATUS.ordinal(), clientStatus);
			} else {
				pd.removeStrAttr(SimpleAttribute.CLIENT_STATUS.ordinal());
			}
		}

		// ram usage
		if (param.getInfoParam().hasRamUsage()) {
			String ramUsage = RAM.getUsage();
			if (!pd.getStrAttrOrDefault(SimpleAttribute.RAM_USAGE.ordinal(), "").equals(ramUsage)) {
				pd.putStrAttr(SimpleAttribute.RAM_USAGE.ordinal(), ramUsage);
			} else {
				pd.removeStrAttr(SimpleAttribute.RAM_USAGE.ordinal());
			}

		}

		// crimson ram usage
		if (param.getInfoParam().hasCrimsonRamUsage()) {
			String crimsonRamUsage = RAM.getClientUsage();
			if (!pd.getStrAttrOrDefault(SimpleAttribute.CLIENT_RAM_USAGE.ordinal(), "").equals(crimsonRamUsage)) {
				pd.putStrAttr(SimpleAttribute.CLIENT_RAM_USAGE.ordinal(), crimsonRamUsage);
			} else {
				pd.removeStrAttr(SimpleAttribute.CLIENT_RAM_USAGE.ordinal());
			}

		}

		// crimson cpu usage
		if (param.getInfoParam().hasCrimsonCpuUsage()) {
			String crimsonCpuUsage = CPU.getClientUsage();
			if (!pd.getStrAttrOrDefault(SimpleAttribute.CLIENT_CPU_USAGE.ordinal(), "").equals(crimsonCpuUsage)) {
				pd.putStrAttr(SimpleAttribute.CLIENT_CPU_USAGE.ordinal(), crimsonCpuUsage);
			} else {
				pd.removeStrAttr(SimpleAttribute.CLIENT_CPU_USAGE.ordinal());
			}

		}
		return pd.build();
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
