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
package com.subterranean_security.crimson.server.net.stream;

import com.subterranean_security.crimson.core.net.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;

public class SInfoSlave extends InfoSlave {

	public SInfoSlave(Param p) {
		super(p, p.getVID());
	}

	@Override
	public void send() {
		write(Message.newBuilder().setRid(param().getCID()).setSid(param().getVID())
				.setEvServerProfileDelta(gatherServerInfo()));
	}

	private EV_ServerProfileDelta gatherServerInfo() {
		return EV_ServerProfileDelta.newBuilder().setPd(gather()).build();
	}

}
