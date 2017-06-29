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
package com.subterranean_security.crimson.client.net.command;

import javax.security.auth.DestroyFailedException;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

public final class AuthCom {
	private AuthCom() {
	}

	public static void auth(Connector c) {
		AuthType authType = ConfigStore.getConfig().getAuthType();

		MI_AuthRequest.Builder auth = MI_AuthRequest.newBuilder().setCvid(LcvidStore.cvid).setType(authType);

		switch (authType) {
		case GROUP:
			AuthenticationGroup group = Client.getGroup();
			auth.setGroupName(group.getName());
			try {
				group.destroy();
			} catch (DestroyFailedException e) {
			}
			c.write(Message.newBuilder().setId(IDGen.msg()).setMiAuthRequest(auth).build());
			break;
		case NO_AUTH:
			c.write(Message.newBuilder().setId(IDGen.msg()).setMiAuthRequest(auth.setPd(Platform.fig())).build());

			break;
		case PASSWORD:
			auth.setPassword(ConfigStore.getConfig().getPassword());

			c.write(Message.newBuilder().setId(IDGen.msg()).setMiAuthRequest(auth).build());
			break;
		default:
			break;

		}
	}

}
