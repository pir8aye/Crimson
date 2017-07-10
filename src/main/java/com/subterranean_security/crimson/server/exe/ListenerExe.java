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
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.store.ProfileStore;

/**
 * @author cilki
 * @since 5.0.0
 */
public class ListenerExe extends Exelet implements ExeI {

	public ListenerExe(Connector connector) {
		super(connector);
	}

	public void rq_add_listener(Message m) {
		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.network.create_listener)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions"))
					.build());
			return;
		}

		connector.write(
				Message.newBuilder().setId(m.getId()).setRsOutcome(Outcome.newBuilder().setResult(true)).build());
		ListenerStore.add(m.getRqAddListener().getConfig());
		// TODO UPDATE
		// Message update = Message.newBuilder().setEvServerProfileDelta(
		// EV_ServerProfileDelta.newBuilder().addListener(m.getRqAddListener().getConfig())).build();
		// NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

	}

	public void rq_remove_listener(Message m) {

	}
}