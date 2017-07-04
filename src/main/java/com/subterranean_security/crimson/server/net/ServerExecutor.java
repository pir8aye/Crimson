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

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_RemoveAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.Generator.RS_Generate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_Ping;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeServerState;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EV_EndpointClosed;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.exe.AuthExe;
import com.subterranean_security.crimson.server.exe.CvidExe;
import com.subterranean_security.crimson.server.exe.DeltaExe;
import com.subterranean_security.crimson.server.exe.FileManagerExe;
import com.subterranean_security.crimson.server.exe.ListenerExe;
import com.subterranean_security.crimson.server.exe.LoginExe;
import com.subterranean_security.crimson.server.exe.NetworkExe;
import com.subterranean_security.crimson.server.exe.ServerInfoExe;
import com.subterranean_security.crimson.server.exe.StreamExe;
import com.subterranean_security.crimson.server.exe.UserExe;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.util.ReferenceCountUtil;

public class ServerExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ServerExecutor.class);

	public ServerExecutor() {
		super();
	}

	@Override
	public void execute(Message m) {
		// perform redirections
		if (m.getRid() != 0) {
			// route
			try {
				ConnectionStore.get(m.getRid()).write(m);
			} catch (NullPointerException e) {
				log.debug("Could not forward message to CVID: {}", m.getRid());
				connector.write(Message.newBuilder()
						.setEvEndpointClosed(EV_EndpointClosed.newBuilder().setCVID(m.getRid())).build());
			}
		} else {
			super.execute(m);
		}

	}

}
