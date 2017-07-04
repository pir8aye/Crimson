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
package com.subterranean_security.crimson.server.exe;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_DirectConnection;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_MakeDirectConnection;

/**
 * @author cilki
 * @since 4.0.0
 */
public class NetworkExe extends Exelet implements ExeI {

	public NetworkExe(Connector connector) {
		super(connector);
	}

	@Override
	public void rq_direct_connection(Message msg) {
		RQ_DirectConnection rq = msg.getRqDirectConnection();
		if (rq.getListenerPort() == 0) {

		} else {
			NetworkStore.route(Message.newBuilder().setRqMakeDirectConnection(RQ_MakeDirectConnection.newBuilder()
					.setHost(connector.getRemoteIP()).setPort(rq.getListenerPort())));
		}
	}
}
