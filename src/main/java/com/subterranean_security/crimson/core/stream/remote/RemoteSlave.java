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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.util.Date;
import java.util.LinkedList;

import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.RemoteStore;
import com.subterranean_security.crimson.core.stream.PeriodicStream;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EV_StreamData;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EventData;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;

public class RemoteSlave extends PeriodicStream {

	private Thread poller = new Thread(new Runnable() {

		public void run() {

			Date iterationStart;
			while (!Thread.interrupted()) {
				iterationStart = new Date();
				ScreenInterface.captureDelta(param().getMasterID(), getStreamID());

				// dynamic wait
				try {
					int iterationDuration = (int) (new Date().getTime() - iterationStart.getTime());
					Thread.sleep(iterationDuration > param().getPeriod() ? 0 : param().getPeriod() - iterationDuration);
				} catch (InterruptedException e) {
					return;
				}
			}

		}
	});

	public RemoteSlave(Param p) {
		// TODO MasterID or SlaveID depends on instance
		super(p, p.getMasterID());

		RemoteStore.setSlave(this);

		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			if (gd.getIDstring().equals(param().getRemoteParam().getMonitor())) {
				ScreenInterface.setDevice(gd);
				break;
			}
		}

		ScreenInterface.setColorQuality(param().getRemoteParam().getColorType());
		ScreenInterface.setCompQuality(param().getRemoteParam().getCompType());

		start();
	}

	@Override
	public void received(Message m) {
		EventData ev = m.getEvStreamData().getEventData();
		if (ev.hasMouseMovedX()) {
			ScreenInterface.getRobot().mouseMove(ev.getMouseMovedX(), ev.getMouseMovedY());
		} else if (ev.hasKeyPressed()) {
			ScreenInterface.getRobot().keyPress(m.getEvStreamData().getEventData().getKeyPressed());
		} else if (ev.hasKeyReleased()) {
			ScreenInterface.getRobot().keyRelease(m.getEvStreamData().getEventData().getKeyReleased());
		} else if (ev.hasMouseReleased()) {
			ScreenInterface.getRobot().mouseRelease(InputEvent.getMaskForButton(ev.getMouseReleased()));
		} else if (ev.hasMousePressed()) {
			ScreenInterface.getRobot().mouseMove(ev.getMouseMovedX(), ev.getMouseMovedY());
			ScreenInterface.getRobot().mousePress(InputEvent.getMaskForButton(ev.getMousePressed()));
		} else if (ev.hasScaleUpdate()) {
			ScreenInterface.setScale(ev.getScaleUpdate());
		}
	}

	@Override
	public void start() {
		switch (param().getRemoteParam().getRmethod()) {
		case NATIVE:
			Native.startRD();
			break;
		case POLL:
			poller.start();
			break;
		default:
			break;

		}

		timer.schedule(sendTask, 0, 100);

	}

	@Override
	public void stop() {
		timer.cancel();

		switch (param().getRemoteParam().getRmethod()) {
		case NATIVE:
			// TODO
			break;
		case POLL:
			poller.interrupt();
			break;
		default:
			break;

		}

	}

	// TODO investigate
	private LinkedList<EV_StreamData> uQueue = new LinkedList<EV_StreamData>();

	@Override
	public void send() {

		if (uQueue.size() != 0) {
			write(Message.newBuilder().setFrom(LcvidStore.cvid).setTo(param().getMasterID())
					.setEvStreamData(uQueue.poll()));
		}

	}

	public void addFrame(EV_StreamData.Builder sd) {
		uQueue.add(sd.setStreamID(getStreamID()).build());
	}

}
