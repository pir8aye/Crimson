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

import com.subterranean_security.crimson.core.proto.net.MSG.Message;
import com.subterranean_security.crimson.core.proto.net.Stream.Param;

public abstract class Stream {

	public Param param;

	protected Timer timer = new Timer();
	protected TimerTask sendTask = new TimerTask() {
		@Override
		public void run() {
			send();
		}

	};

	/**
	 * Called when data arrives
	 */
	public abstract void received(Message m);

	/**
	 * Called to pump the stream
	 */
	public abstract void send();

	public abstract void start();

	public abstract void stop();

}
