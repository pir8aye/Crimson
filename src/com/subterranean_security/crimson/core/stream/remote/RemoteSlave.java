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
package com.subterranean_security.crimson.core.stream.remote;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.LinkedList;

import com.subterranean_security.crimson.client.ClientStore;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.CoreStore;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.ScreenData;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.Native;

public class RemoteSlave extends Stream {

	private Robot robot;

	public RemoteSlave(Param p) {
		param = p;

		CoreStore.Remote.setSlave(this);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void received(Message m) {
		if (m.getEvStreamData().getEventData().hasKeyPressed()) {
			robot.keyPress(m.getEvStreamData().getEventData().getKeyPressed());
		} else if (m.getEvStreamData().getEventData().hasKeyReleased()) {
			robot.keyRelease(m.getEvStreamData().getEventData().getKeyReleased());
		}
	}

	@Override
	public void start() {
		Native.startRD();
		timer.schedule(sendTask, 0, param.hasPeriod() ? param.getPeriod() : 50);

	}

	@Override
	public void stop() {
		timer.cancel();

	}

	private LinkedList<ScreenData> queue = new LinkedList<ScreenData>();

	@Override
	public void send() {
		if (queue.size() != 0) {
			ClientStore.Connections.route(
					Message.newBuilder().setUrgent(true).setSid(Common.cvid).setRid(param.getVID()).setEvStreamData(
							EV_StreamData.newBuilder().setStreamID(getStreamID()).setScreenData(queue.poll())));
		}

	}

	public void addFrame(ScreenData sd) {
		queue.add(sd);
	}

}
