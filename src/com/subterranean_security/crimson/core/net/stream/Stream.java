/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.core.net.stream;

import java.util.Observable;
import java.util.Observer;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.net.NetworkNode;
import com.subterranean_security.crimson.core.proto.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.store.ConnectionStore;

public abstract class Stream implements Observer {
	private Param param;
	private int endpointCvid;

	public Stream(Param param, int endpointCvid) {
		this(param);
		this.endpointCvid = endpointCvid;
	}

	public Stream(Param param) {
		this.param = param;
	}

	public Param param() {
		return param;
	}

	public int getStreamID() {
		return param.getStreamID();
	}

	protected boolean running;

	public boolean isRunning() {
		return running;
	}

	/**
	 * Called when data arrives
	 */
	public abstract void received(Message m);

	public abstract void start();

	public abstract void stop();

	/**
	 * Writes a message to the endpoint of this stream
	 * 
	 * @param msg
	 */
	protected void write(Message.Builder msg) {
		ConnectionStore.route(msg.setRid(endpointCvid).setSid(Common.cvid));
	}

	@Override
	public void update(Observable o, Object arg) {
		NetworkNode node = (NetworkNode) o;
		EV_NetworkDelta nd = (EV_NetworkDelta) arg;

		// the network has changed. Check if this stream is affected.
		if (nd.getNodeRemoved() != null
				&& (nd.getNodeRemoved().getCvid() == endpointCvid || nd.getNodeRemoved().getCvid() == 0)) {
			// stop this stream
			StreamStore.removeStreamBySID(getStreamID());
			stop();
		}

	}

}
