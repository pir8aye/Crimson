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
package com.subterranean_security.crimson.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.exe.StreamExe;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EV_EndpointClosed;
import com.subterranean_security.crimson.server.exe.S_AuthExe;
import com.subterranean_security.crimson.server.exe.DeltaExe;
import com.subterranean_security.crimson.server.exe.FileManagerExe;
import com.subterranean_security.crimson.server.exe.GenerateExe;
import com.subterranean_security.crimson.server.exe.ListenerExe;
import com.subterranean_security.crimson.server.exe.LoginExe;
import com.subterranean_security.crimson.server.exe.MiscExe;
import com.subterranean_security.crimson.server.exe.NetworkExe;
import com.subterranean_security.crimson.server.exe.ServerInfoExe;
import com.subterranean_security.crimson.server.exe.UserExe;

public class ServerExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ServerExecutor.class);

	public ServerExecutor() {
		super();
		initUnauth();
	}

	@Override
	public void execute(Message m) {
		// perform redirections
		if (m.getTo() != 0) {
			// route
			try {
				ConnectionStore.get(m.getTo()).write(m);
			} catch (NullPointerException e) {
				log.debug("Could not forward message to CVID: {}", m.getTo());
				connector.write(Message.newBuilder()
						.setEvEndpointClosed(EV_EndpointClosed.newBuilder().setCVID(m.getTo())).build());
			}
		} else {
			super.execute(m);
		}

	}

	@Override
	public void initUnauth() {
		setExecutors(new S_AuthExe(connector, this), new LoginExe(connector, this), new ServerInfoExe(connector));
	}

	@Override
	public void initAuth() {
		setExecutors(new DeltaExe(connector), new FileManagerExe(connector), new GenerateExe(connector),
				new ListenerExe(connector), new MiscExe(connector), new NetworkExe(connector), new StreamExe(connector),
				new UserExe(connector));
	}

}
