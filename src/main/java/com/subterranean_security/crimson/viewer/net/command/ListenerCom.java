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
package com.subterranean_security.crimson.viewer.net.command;

import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.RQ_AddListener;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.RQ_RemoveListener;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

public final class ListenerCom {
	private ListenerCom() {
	}

	/**
	 * Start a new listener on the server
	 * 
	 * @param lf
	 * @return
	 */
	public static Outcome addListener(ListenerConfig lf) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			Message m = ConnectionStore
					.routeAndWait(Message.newBuilder().setRqAddListener(RQ_AddListener.newBuilder().setConfig(lf)), 3);
			if (m.getRsOutcome() != null) {
				outcome.setResult(m.getRsOutcome().getResult());
				if (!m.getRsOutcome().getComment().isEmpty())
					outcome.setComment(m.getRsOutcome().getComment());

			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (Timeout e) {
			outcome.setResult(false).setComment("Request Timeout");
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	/**
	 * Stop and remove a listener on the server
	 * 
	 * @param id
	 * @return
	 */
	public static Outcome removeListener(int id) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ConnectionStore.routeAndWait(
					Message.newBuilder().setRqRemoveListener(RQ_RemoveListener.newBuilder().setId(id)), 3);
			if (m.getRsOutcome() != null) {
				outcome.setResult(m.getRsOutcome().getResult());
				if (!m.getRsOutcome().getComment().isEmpty())
					outcome.setComment(m.getRsOutcome().getComment());

			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (Timeout e) {
			outcome.setResult(false).setComment("Request Timeout");
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}
}
