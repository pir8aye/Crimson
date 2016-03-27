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
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerInfoDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.server.ServerStore;

public class SInfoSlave extends InfoSlave {

	public SInfoSlave(Param p) {
		super(p);
	}

	@Override
	public void send() {
		ServerStore.Connections.getConnection(param.getVID()).handle.write(Message.newBuilder().setUrgent(true)
				.setCid(param.getCID()).setVid(param.getVID()).setEvServerInfoDelta(gatherServerInfo()).build());

	}

	private EV_ServerInfoDelta gatherServerInfo() {
		EV_ServerInfoDelta.Builder sid = EV_ServerInfoDelta.newBuilder();
		sid.setServerStatus(Server.isRunning());
		sid.setClientCount(ServerStore.Connections.countClients());
		sid.setUserCount(ServerStore.Connections.countUsers());
		if (param.getInfoParam().hasCpuSpeed()) {
		}
		if (param.getInfoParam().hasCpuTemp()) {
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
