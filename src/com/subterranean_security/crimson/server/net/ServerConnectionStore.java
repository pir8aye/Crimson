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
package com.subterranean_security.crimson.server.net;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.ConnectionStore.ConnectionEventListener;
import com.subterranean_security.crimson.server.store.Authentication;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;

public final class ServerConnectionStore {
	private static final Logger log = LoggerFactory.getLogger(ServerConnectionStore.class);

	private ServerConnectionStore() {
	}

	public static void initialize() {
		ConnectionStore.initialize(new ServerConnectionEventListener());
	}

	public static void sendToAll(Universal.Instance i, Message m) {
		for (Connector c : ConnectionStore.getValues()) {
			if (c.getInstance() == i) {
				c.write(m);
			}
		}
	}

	public static void sendToViewersWithAuthorityOverClient(int cid, int perm, Message.Builder m) {
		for (Connector c : ConnectionStore.getValues()) {
			if (c.getInstance() == Universal.Instance.VIEWER
					&& ProfileStore.getViewer(c.getCvid()).getPermissions().getFlag(cid, perm)) {
				c.write(m.build());
			}
		}
	}

	private static class ServerConnectionEventListener implements ConnectionEventListener {

		@Override
		public void update(Observable o, Object arg) {
			Connector connector = (Connector) o;
			ConnectionState state = (ConnectionState) arg;

			if (connector.getInstance() == Instance.CLIENT) {
				switch (state) {
				case AUTHENTICATED:
					Authentication.refreshVisibilityPermissions(connector.getCvid());
					ProfileStore.getClient(connector.getCvid()).setOnline(true);
					// sendToViewersWithAuthorityOverClient(r.getCvid(),
					// Perm.client.visibility,
					// Message.newBuilder().setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(r.getCvid())
					// .putStrAttr(SimpleAttribute.CLIENT_ONLINE.ordinal(),
					// "1")));
					break;
				case NOT_CONNECTED:
					ProfileStore.getClient(connector.getCvid()).setOnline(false);
					sendToViewersWithAuthorityOverClient(connector.getCvid(), Perm.client.visibility,
							Message.newBuilder()
									.setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(connector.getCvid())
											.addGroup(AttributeGroupContainer.newBuilder().setGroupId("")
													.setGroupType(AttributeKey.Type.GENERAL.ordinal()).putAttribute(
															AKeySimple.CLIENT_ONLINE.ordinal(), "0")
													.build())));
					break;
				default:
					break;

				}

			} else {

			}

		}

	}

}