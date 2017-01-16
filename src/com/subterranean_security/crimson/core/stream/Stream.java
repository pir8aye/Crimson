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
package com.subterranean_security.crimson.core.stream;

import java.util.Timer;
import java.util.TimerTask;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.MI_StreamStart;
import com.subterranean_security.crimson.core.proto.Stream.MI_StreamStop;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.viewer.net.ViewerRouter;

public abstract class Stream {

	public Param param;

	protected Timer timer = new Timer();
	protected TimerTask sendTask = new TimerTask() {
		@Override
		public void run() {
			send();
		}

	};

	public int getStreamID() {
		return param.getStreamID();
	}

	/**
	 * Called when data arrives
	 */
	public abstract void received(Message m);

	/**
	 * Called periodically to pump the stream
	 */
	public abstract void send();

	protected boolean running;

	public boolean isRunning() {
		return running;
	}

	public void start() {
		running = true;
		if (Common.instance == Universal.Instance.VIEWER) {
			ViewerRouter.route(Message.newBuilder().setSid(param.getVID()).setRid(param.getCID())
					.setMiStreamStart(MI_StreamStart.newBuilder().setParam(param)));
		}

	}

	public void stop() {
		running = false;
		timer.cancel();
		if (Common.instance == Universal.Instance.VIEWER) {
			ViewerRouter.route(Message.newBuilder().setSid(param.getVID()).setRid(param.getCID())
					.setMiStreamStop(MI_StreamStop.newBuilder().setStreamID(param.getStreamID())));
		}
	}

}
