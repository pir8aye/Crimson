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
import java.util.Map;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.PluralKey;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_CPU;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_NIC;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.NIC;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.stream.PeriodicStream;
import com.subterranean_security.crimson.core.struct.stat_stream.StatStream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;

public abstract class InfoSlave extends PeriodicStream {

	/**
	 * The profile delta used to remember attributes' last value. This delta is
	 * not actually sent.
	 */
	private EV_ProfileDelta.Builder pd;

	/**
	 * The profile delta which is actually sent to the endpoint.
	 */
	private EV_ProfileDelta.Builder newPD;

	private StatStream rxSpeed;
	private StatStream txSpeed;

	/**
	 * The {@code SingularKey}s that this {@code InfoSlave} will be updating.
	 */
	private List<SingularKey> singularKeys;

	/**
	 * The {@code PluralKey}s that this {@code InfoSlave} will be updating.
	 */
	private Map<PluralKey, List<Integer>> pluralKeys;

	public InfoSlave(Param param, int endpoint) {
		super(param, endpoint);
		initialize();
	}

	public InfoSlave(Param param) {
		super(param);
		initialize();
	}

	private void initialize() {
		initKeys(param().getInfoParam().getKeyList());

		pd = EV_ProfileDelta.newBuilder().setCvid(LcvidStore.cvid);

		for (AttributeKey key : keys) {
			if (key instanceof AK_CPU) {
				initializeCPU();
				break;
			}
		}

		for (AttributeKey key : keys) {
			if (key instanceof AK_NIC) {
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
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.stream()).setVID(LcvidStore.cvid).build());
	}

	private void initializeCPU() {
		String cpuID = null;
		if (param().getInfoParam().hasCpuId()) {
			cpuID = param().getInfoParam().getCpuId();
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
		if (param().getInfoParam().hasNicId()) {
			nicID = param().getInfoParam().getNicId();
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

		if (keys.contains(AK_NIC.RX_BYTES) || keys.contains(AK_NIC.RX_SPEED)) {
			rxSpeed = new StatStream(1000, 60);
		}

		if (keys.contains(AK_NIC.TX_BYTES) || keys.contains(AK_NIC.TX_SPEED)) {
			txSpeed = new StatStream(1000, 60);
		}
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	protected EV_ProfileDelta gather() {

		// move
		for (AttributeKey key : keys) {
			if (key instanceof SingularKey) {

			} else if (key instanceof PluralKey) {

			} else {
				throw new UnsupportedOperationException("Unsupported AttributeKey: " + key.getClass().getName());
			}
		}

		for (PluralKey key : pluralKeys.keySet()) {
			for (Integer groupID : pluralKeys.get(key)) {
				Object value = key.query(groupID);
			}
		}

		for (SingularKey key : singularKeys) {
			Object value = key.query();
		}

		// purge last attribute groups
		pd.clearGroup();

		if (keys.contains(AK_NIC.RX_BYTES)) {
			long l = NIC.getRxBytes(whichNIC);
			rxSpeed.addPoint(l);

			poll(lastNicContainer, AK_NIC.RX_BYTES, UnitTranslator.translateNicOutput(l));
		}

		if (keys.contains(AK_NIC.TX_BYTES)) {
			long l = NIC.getTxBytes(whichNIC);
			txSpeed.addPoint(l);

			poll(lastNicContainer, AK_NIC.TX_BYTES, UnitTranslator.translateNicOutput(l));
		}

		if (keys.contains(AK_NIC.RX_SPEED))
			poll(lastNicContainer, AK_NIC.RX_SPEED, UnitTranslator.nicSpeed(keys.contains(AK_NIC.RX_BYTES)
					? rxSpeed.getInstantaneousSpeed() : rxSpeed.addPoint(NIC.getRxBytes(whichNIC))));
		if (keys.contains(AK_NIC.TX_SPEED))
			poll(lastNicContainer, AK_NIC.TX_SPEED, UnitTranslator.nicSpeed(keys.contains(AK_NIC.TX_BYTES)
					? txSpeed.getInstantaneousSpeed() : txSpeed.addPoint(NIC.getTxBytes(whichNIC))));
		if (keys.contains(AK_NIC.RX_PACKETS))
			poll(lastNicContainer, AK_NIC.RX_PACKETS, "" + NIC.getRxPackets(whichNIC));
		if (keys.contains(AK_NIC.TX_PACKETS))
			poll(lastNicContainer, AK_NIC.TX_PACKETS, "" + NIC.getTxPackets(whichNIC));
		if (keys.contains(AK_CPU.TOTAL_USAGE))
			poll(lastCpuContainer, AK_CPU.TOTAL_USAGE, CPU.getTotalUsage(whichCPU));
		if (keys.contains(AK_CPU.TEMP))
			poll(lastCpuContainer, AK_CPU.TEMP, CPU.getTemp());

		return pd.build();
	}

	private void update(SingularKey key, Object value) {
		if (value instanceof String) {

		} else if (value instanceof Integer) {

		} else if (value instanceof Long) {

		} else if (value instanceof Boolean) {

		}
	}

	private void poll(AttributeGroupContainer.Builder container, AttributeKey key, String value) {
		if (value.equals(container.getAttributeOrDefault(key.getFullID(), null))) {
			container.removeAttribute(key.getFullID());
		} else {
			container.putAttribute(key.getFullID(), value);
		}
	}

	@Override
	public void start() {
		timer.schedule(sendTask, 0, param().hasPeriod() ? param().getPeriod() : 1000);
	}

	@Override
	public void stop() {
		timer.cancel();
	}

}
