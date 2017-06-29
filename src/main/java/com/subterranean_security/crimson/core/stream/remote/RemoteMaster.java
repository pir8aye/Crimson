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

import java.util.concurrent.LinkedBlockingQueue;

import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.stream.PeriodicStream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.cv.ui.remote.RDArea;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EV_StreamData;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EventData;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.RemoteParam;

public class RemoteMaster extends PeriodicStream {

	private RDArea rda;
	private int cid;

	private Thread eventThread = new Thread(new Runnable() {
		public void run() {

			while (!Thread.interrupted()) {
				try {
					NetworkStore.route(Message.newBuilder().setRid(cid)
							.setEvStreamData(
									EV_StreamData.newBuilder().setStreamID(getStreamID()).setEventData(queue.take()))
							.build());
				} catch (InterruptedException e) {
					return;
				}

			}
		}
	});

	public RemoteMaster(RemoteParam rp, int cid, RDArea rda) {
		super(Param.newBuilder().setPeriod(100).setRemoteParam(rp).setStreamID(IDGen.stream()).setCID(cid)
				.setVID(LcvidStore.cvid).build());
		this.rda = rda;
		this.cid = cid;

		eventThread.start();
	}

	public RemoteMaster(RemoteParam rp, RDArea rda) {
		this(rp, 0, rda);
	}

	@Override
	public void received(Message m) {
		// update rda
		if (m.getEvStreamData().hasDirtyRect()) {
			rda.updateScreen(m.getEvStreamData().getDirtyRect());
		} else if (m.getEvStreamData().hasDirtyBlock()) {
			rda.updateScreen(m.getEvStreamData().getDirtyBlock());
		}

	}

	@Override
	public void send() {
		// send events

	}

	private LinkedBlockingQueue<EventData> queue = new LinkedBlockingQueue<EventData>();

	public void sendEvent(EventData event) {
		queue.offer(event);
	}

}
