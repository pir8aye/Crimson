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

import com.subterranean_security.crimson.client.net.Router;
import com.subterranean_security.crimson.core.proto.net.MSG.Message;
import com.subterranean_security.crimson.core.proto.net.Stream.InfoData;
import com.subterranean_security.crimson.core.proto.net.Stream.Param;
import com.subterranean_security.crimson.core.proto.net.Stream.StreamData_EV;
import com.subterranean_security.crimson.core.stream.Stream;

public class InfoSlave extends Stream {

	public InfoSlave(Param p) {
		param = p;
		start();
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	@Override
	public void send() {
		System.out.println("Pumping stream");
		InfoData.Builder id = InfoData.newBuilder();
		if (param.getInfoParam().hasRamUsage()) {
			// add ram usage
		}

		Router.route(Message.newBuilder().setUrgent(true)
				.setStreamDataEv(StreamData_EV.newBuilder().setStreamID(param.getStreamID()).setInfoData(id)));

	}

	@Override
	public void start() {
		timer.schedule(sendTask, 0, 1000);

	}

	@Override
	public void stop() {
		timer.cancel();

	}

}
