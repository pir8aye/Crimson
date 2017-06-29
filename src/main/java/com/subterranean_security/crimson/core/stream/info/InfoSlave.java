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

import java.util.LinkedList;
import java.util.List;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.stream.PeriodicStream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;

/**
 * The {@code InfoSlave} is reponsible for gathering a selection of information
 * and sending it to the {@code InfoMaster}.
 * 
 * @author cilki
 * @since 4.0.0
 */
public class InfoSlave extends PeriodicStream {

	/**
	 * The profile delta used to remember attributes' last value. This delta is not
	 * actually sent.
	 */
	private EV_ProfileDelta.Builder history;

	/**
	 * The profile delta which is actually sent to the endpoint.
	 */
	private EV_ProfileDelta.Builder pd;

	/**
	 * The {@code AttributeKey}s that this {@code InfoSlave} will be updating.
	 */
	private List<AttributeKey> aKeys;

	public InfoSlave(Param param, int endpoint) {
		super(param, endpoint);
		initialize();
	}

	public InfoSlave(Param param) {
		super(param);
		initialize();
	}

	private void initialize() {
		aKeys = new LinkedList<>();
		for (int wireID : param.getInfoParam().getKeyList()) {
			aKeys.add(AttributeKey.convert(wireID));
		}

		history = EV_ProfileDelta.newBuilder();
		pd = EV_ProfileDelta.newBuilder().setCvid(LcvidStore.cvid);

		start();
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.stream()).setVID(LcvidStore.cvid).build());
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	/**
	 * Gather the information 
	 * 
	 * @return A new {@code EV_ProfileDelta}
	 */
	protected EV_ProfileDelta gather() {
		pd.clearIntAttr();
		pd.clearBooleanAttr();
		pd.clearLongAttr();
		pd.clearStrAttr();

		// TODO default values
		for (AttributeKey key : aKeys) {
			Object value = key.query();
			int wireID = key.getWireID();

			if (value instanceof Integer) {
				int newValue = (int) value;
				if (history.getIntAttrOrDefault(wireID, 0) != newValue) {
					pd.putIntAttr(wireID, newValue);
				}
			} else if (value instanceof String) {
				String newValue = (String) value;
				if (history.getStrAttrOrDefault(wireID, "").equals(newValue)) {
					pd.putStrAttr(wireID, newValue);
				}
			} else if (value instanceof Long) {
				long newValue = (long) value;
				if (history.getLongAttrOrDefault(wireID, 0) == newValue) {
					pd.putLongAttr(wireID, newValue);
				}
			} else if (value instanceof Boolean) {
				boolean newValue = (boolean) value;
				if (history.getBooleanAttrOrDefault(wireID, false) == newValue) {
					pd.putBooleanAttr(wireID, newValue);
				}
			}
		}

		EV_ProfileDelta finalPd = pd.build();
		history.mergeFrom(finalPd);

		return finalPd;
	}

	private void update(SingularKey key, Object value) {
		if (value instanceof String) {

		} else if (value instanceof Integer) {

		} else if (value instanceof Long) {

		} else if (value instanceof Boolean) {

		}
	}

	@Override
	public void send() {
		EV_ProfileDelta pd = gather();

		if (pd.getIntAttrCount() > 0 || pd.getStrAttrCount() > 0 || pd.getBooleanAttrCount() > 0
				|| pd.getLongAttrCount() > 0) {
			write(Message.newBuilder().setRid(param().getCID()).setSid(param().getVID()).setEvProfileDelta(pd));
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
