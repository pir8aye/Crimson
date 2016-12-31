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
import com.subterranean_security.crimson.core.platform.info.CLIENT;
import com.subterranean_security.crimson.core.platform.info.CPU;
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
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.Native;

public abstract class InfoSlave extends Stream {

	/**
	 * The last profile delta. Subsequent updates use this as a base.
	 */
	private EV_ProfileDelta.Builder pd;
	private AttributeGroupContainer.Builder lastCpuUsage;
	private AttributeGroupContainer.Builder lastCpuTemp;
	private int whichCPU = 0;

	public InfoSlave(Param p) {
		param = p;
		pd = EV_ProfileDelta.newBuilder().setCvid(Common.cvid);

		String cpuID = null;
		if (param.getInfoParam().hasCpuUsage() || param.getInfoParam().hasCpuTemp()) {
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
		}

		if (param.getInfoParam().hasCpuUsage()) {

			lastCpuUsage = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.CPU.ordinal())
					.setGroupId(cpuID).setAttributeType(AttributeGroupType.CPU_TOTAL_USAGE.ordinal());
		}

		if (param.getInfoParam().hasCpuTemp()) {
			lastCpuTemp = AttributeGroupContainer.newBuilder().setGroupType(GroupAttributeType.CPU.ordinal())
					.setGroupId(cpuID).setAttributeType(AttributeGroupType.CPU_TEMP.ordinal());
		}
		start();
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.getStreamid()).setVID(Common.cvid).build());
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
			String clientStatus = CLIENT.getStatus();
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
