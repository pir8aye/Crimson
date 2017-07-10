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
package com.subterranean_security.crimson.client.command;

import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH.AuthType;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.M1_AuthAttempt;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class AuthCom {
	private AuthCom() {
	}

	public static void auth(Connector c) {
		ClientConfig cc = ConfigStore.getConfig();

		AuthType authType = AuthType.valueOf(cc.getGroupType());

		M1_AuthAttempt.Builder auth = M1_AuthAttempt.newBuilder().setAuthType(authType.toString());

		switch (authType) {
		case KEY:
			auth.setGroupName(cc.getGroupName());
			break;
		default:
			break;
		}

		c.write(Message.newBuilder().setId(IDGen.msg()).setM1AuthAttempt(auth));
	}

}
