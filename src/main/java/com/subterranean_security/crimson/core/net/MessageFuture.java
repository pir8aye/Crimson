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
package com.subterranean_security.crimson.core.net;

import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * A MessageFuture is given to a thread waiting for a message (most likely a
 * response of some sort). If the desired message arrives before the thread
 * times out, the MessageFuture gives the waiting thread access to the message.
 */
public class MessageFuture {

	/**
	 * The target message, if it arrives
	 */
	private Message message;

	public void setMessage(Message m) {
		if (m == null)
			throw new IllegalArgumentException();

		this.message = m;
		synchronized (this) {
			notifyAll();
		}

	}

	/**
	 * Returns the target message if it arrives.
	 * 
	 * @param timeout
	 *            The amount of time to wait for the message in milliseconds
	 * @return
	 * @throws MessageTimeout
	 *             If the message does not arrive in time
	 * @throws InterruptedException
	 */
	public Message get(long timeout) throws MessageTimeout, InterruptedException {
		synchronized (this) {
			wait(timeout);
		}

		if (message == null)
			throw new MessageTimeout();
		return message;
	}

	/**
	 * Indicates that a thread has timed out while waiting for a message
	 */
	public static class MessageTimeout extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
