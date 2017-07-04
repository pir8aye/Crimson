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
package com.subterranean_security.crimson.viewer.command;

import java.net.ConnectException;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_DirectConnection;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_MakeDirectConnection;
import com.subterranean_security.crimson.viewer.net.ViewerExecutor;

public final class NetworkCom {
	private NetworkCom() {
	}

	/**
	 * Establish a direct UDP connection with the specified client.
	 * 
	 * @param cid
	 * @return
	 */
	public static Outcome establishDirectConnection(int cid) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			Message rs = ConnectionStore.get(0).writeAndGetResponse(Message.newBuilder()
					.setRqDirectConnection(RQ_DirectConnection.newBuilder().setCid(cid).setListenerPort(10102)).build())
					.get(7000);
			if (rs.getRsDirectConnection() != null && rs.getRsDirectConnection().hasRequest()) {
				// request has been granted
				makeDirectConnection(cid, rs.getRsDirectConnection().getRequest());
			}
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static boolean makeDirectConnection(int cid, RQ_MakeDirectConnection rq) {
		Connector connector = new Connector(new ViewerExecutor());

		try {
			connector.connect(Config.ConnectionType.DATAGRAM, rq.getHost(), rq.getPort());
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
