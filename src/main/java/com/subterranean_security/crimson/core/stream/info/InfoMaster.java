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

import java.util.Random;

import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.stream.PeriodicStream;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;

public class InfoMaster extends PeriodicStream {

	public InfoMaster(InfoParam ip, int CID, int period) {
		super(Param.newBuilder().setPeriod(period).setInfoParam(ip).setStreamID(new Random().nextInt()).setCID(CID)
				.setVID(LcvidStore.cvid).build());
		start();
	}

	public InfoMaster(InfoParam ip, int period) {
		this(ip, 0, period);
	}

	@Override
	public void received(Message m) {
		// receiving is handled by executor

	}

	@Override
	public void send() {
		// do nothing

	}

}
