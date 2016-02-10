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

import com.subterranean_security.crimson.core.proto.net.MSG.Message;
import com.subterranean_security.crimson.core.proto.net.Stream.InfoData;
import com.subterranean_security.crimson.core.proto.net.Stream.InfoParam;
import com.subterranean_security.crimson.core.proto.net.Stream.Param;
import com.subterranean_security.crimson.core.proto.net.Stream.StreamStart_EV;
import com.subterranean_security.crimson.core.proto.net.Stream.StreamStop_EV;
import com.subterranean_security.crimson.core.stream.Stream;

public class InfoMaster extends Stream {

	@Override
	public void received(Message m) {
		InfoData infoData = m.getStreamDataEv().getInfoData();
		if (infoData.hasRamUsage()) {
			// update profile
		}

	}

	@Override
	public void send() {
		// do nothing

	}

	@Override
	public void start() {
		Message.newBuilder().setUrgent(true).setStreamStartEv(
				StreamStart_EV.newBuilder().setParam(Param.newBuilder().setInfoParam(InfoParam.newBuilder())));

	}

	@Override
	public void stop() {
		Message.newBuilder().setUrgent(true)
				.setStreamStopEv(StreamStop_EV.newBuilder().setStreamID(param.getStreamID()));

	}

}