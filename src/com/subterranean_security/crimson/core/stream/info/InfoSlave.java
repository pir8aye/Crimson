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

import com.subterranean_security.crimson.client.Native;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.Stream;

public abstract class InfoSlave extends Stream {

	public InfoSlave(Param p) {
		param = p;
		start();
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(new Random().nextInt()).setVID(Common.cvid).build());
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	protected EV_ProfileDelta gatherDefaultInfo() {
		EV_ProfileDelta.Builder pd = EV_ProfileDelta.newBuilder().setCvid(Common.cvid);
		if (param.getInfoParam().hasActiveWindow()) {
			pd.setActiveWindow(Native.getActiveWindow());
		}
		if (param.getInfoParam().hasCpuSpeed()) {
		}
		if (param.getInfoParam().hasCpuTemp()) {
		}
		if (param.getInfoParam().hasCrimsonRamUsage()) {
			pd.setCrimsonRamUsage(Platform.Advanced.getCrimsonMemoryUsage());
		}
		if (param.getInfoParam().hasCrimsonCpuUsage()) {
			pd.setCrimsonCpuUsage(Platform.Advanced.getCrimsonCpuUsage());
		}
		return pd.build();
	}

	@Override
	public void start() {

		timer.schedule(sendTask, 0, param.hasPeriod() ? param.getPeriod() : 1000);

	}

	@Override
	public void stop() {
		timer.cancel();

	}

}
