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
package com.subterranean_security.crimson.server.stream;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;

public class SInfoSlave extends InfoSlave {

	public SInfoSlave(Param p) {
		super(p);
	}

	@Override
	public void send() {
		Connector r = ConnectionStore.get(param.getVID());
		if (r == null) {
			StreamStore.removeStreamBySID(getStreamID());
			return;
		}
		r.write(Message.newBuilder().setRid(param.getCID()).setSid(param.getVID())
				.setEvServerProfileDelta(gatherServerInfo()).build());

	}

	private EV_ServerProfileDelta gatherServerInfo() {
		return EV_ServerProfileDelta.newBuilder().setPd(gather()).build();
	}

}
