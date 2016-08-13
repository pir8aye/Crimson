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

import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.server.net.Receptor;

public class SInfoSlave extends InfoSlave {

	public SInfoSlave(Param p) {
		super(p);
	}

	@Override
	public void send() {
		Receptor r = ServerStore.Connections.getConnection(param.getVID());
		if (r == null) {
			StreamStore.removeStream(getStreamID());
			return;
		}
		r.handle.write(Message.newBuilder().setUrgent(true).setRid(param.getCID()).setSid(param.getVID())
				.setEvServerProfileDelta(gatherServerInfo()).build());

	}

	private EV_ServerProfileDelta gatherServerInfo() {
		EV_ServerProfileDelta.Builder sid = EV_ServerProfileDelta.newBuilder();
		sid.setServerStatus(ServerStore.Listeners.isRunning());
		sid.setClientCount(ServerStore.Connections.countClients());
		sid.setUserCount(ServerStore.Connections.countUsers());
		if (param.getInfoParam().hasCpuTemp()) {
			for (double d : Platform.Advanced.getCPUTemps()) {
				sid.addCpuTemp(d);
			}
		}
		if (param.getInfoParam().hasCrimsonRamUsage()) {
			sid.setRamCrimsonUsage(Platform.Advanced.getCrimsonMemoryUsage());
		}
		if (param.getInfoParam().hasCrimsonCpuUsage()) {
			sid.setCpuCrimsonUsage(Platform.Advanced.getCrimsonCpuUsage());
		}
		return sid.build();
	}

}
