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
package com.subterranean_security.crimson.core.net.stream.subscriber;

import java.util.Random;

import com.subterranean_security.crimson.core.net.stream.PeriodicStream;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.MI_StreamStart;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.SubscriberParam;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;

public class SubscriberMaster extends PeriodicStream {

	public SubscriberMaster(SubscriberParam sp, int CID) {
		super(Param.newBuilder().setSubscriberParam(sp).setStreamID(new Random().nextInt()).setCID(CID)
				.setVID(LcvidStore.cvid).build());
		start();
	}

	@Override
	public void received(Message m) {
		// receiving is handled by executor

	}

	@Override
	public void send() {
		// do nothing

	}

	@Override
	public void start() {
		ConnectionStore.route(Message.newBuilder().setSid(param().getVID()).setRid(Reserved.SERVER)
				.setMiStreamStart(MI_StreamStart.newBuilder().setParam(param())));

	}

}
