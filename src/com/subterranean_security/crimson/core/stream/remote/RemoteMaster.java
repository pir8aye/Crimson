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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;
import com.subterranean_security.crimson.core.proto.Stream.EventData;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.ui.remote.RDArea;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.viewer.net.ViewerRouter;

public class RemoteMaster extends Stream {

	private RDArea rda;
	private int cid;

	private Thread eventThread = new Thread(new Runnable() {
		public void run() {

			EV_StreamData.Builder ev = EV_StreamData.newBuilder().setStreamID(getStreamID());
			Message.Builder msg = Message.newBuilder().setRid(cid).setEvStreamData(ev);

			while (!Thread.interrupted()) {
				try {
					ev.setEventData(queue.take());
					ViewerRouter.route(msg.build());
				} catch (InterruptedException e) {
					return;
				}

			}
		}
	});

	public RemoteMaster(RemoteParam rp, int cid, RDArea rda) {
		this.rda = rda;
		this.cid = cid;
		param = Param.newBuilder().setPeriod(100).setRemoteParam(rp).setStreamID(IDGen.getStreamid()).setCID(cid)
				.setVID(Common.cvid).build();
		eventThread.start();
	}

	public RemoteMaster(RemoteParam rp, RDArea rda) {
		this(rp, 0, rda);
	}

	@Override
	public void received(Message m) {
		// update rda
		rda.updateScreen(m.getEvStreamData().getScreenData());

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
