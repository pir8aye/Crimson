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
package com.subterranean_security.crimson.server.net.listener;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.listener.ConnectionEventListener;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkAdded;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkRemoved;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.universal.Universal.Instance;

public class ServerConnectionEventListener extends ConnectionEventListener {

	private static final Logger log = LoggerFactory.getLogger(ServerConnectionEventListener.class);

	@Override
	public void update(Observable o, Object arg) {
		super.update(o, arg);

		Connector connector = (Connector) o;
		if (arg instanceof ConnectionState) {
			if (connector.getInstance() == Instance.CLIENT) {
				switch ((ConnectionState) arg) {
				case AUTHENTICATED:
					clientAuthenticated(connector);
					addLink(connector.getCvid(), 0);
					break;
				case CONNECTED:
					break;
				case NOT_CONNECTED:
					clientNotConnected(connector);
					removeLink(connector.getCvid(), 0);
					break;
				default:
					break;

				}

			} else if (connector.getInstance() == Instance.VIEWER) {

			}
		}

	}

	private void clientAuthenticated(Connector connector) {
		System.out.println("clientAuthenticated");
		AuthStore.refreshVisibilityPermissions(connector.getCvid());
		ServerProfileStore.getClient(connector.getCvid()).setOnline(true);
		NetworkStore.sendToViewersWithAuthorityOverClient(connector.getCvid(), Perm.client.visibility,
				Message.newBuilder()
						.setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(connector.getCvid())
								.addGroup(AttributeGroupContainer.newBuilder()
										.setGroupType(AttributeKey.Type.GENERAL.ordinal()).putAttribute(
												AKeySimple.CLIENT_ONLINE.ordinal(), "1"))));
	}

	private void clientNotConnected(Connector connector) {
		ServerProfileStore.getClient(connector.getCvid()).setOnline(false);
		NetworkStore.sendToViewersWithAuthorityOverClient(connector.getCvid(), Perm.client.visibility,
				Message.newBuilder()
						.setEvProfileDelta(EV_ProfileDelta.newBuilder().setCvid(connector.getCvid())
								.addGroup(AttributeGroupContainer.newBuilder().setGroupId("")
										.setGroupType(AttributeKey.Type.GENERAL.ordinal()).putAttribute(
												AKeySimple.CLIENT_ONLINE.ordinal(), "0")
										.build())));
	}

	public static void addLink(int cvid1, int cvid2) {
		log.debug("Linking: {} and {}", cvid1, cvid2);
		EV_NetworkDelta.Builder ev = EV_NetworkDelta.newBuilder()
				.setLinkAdded(LinkAdded.newBuilder().setCvid1(cvid1).setCvid2(cvid2));

		NetworkStore.broadcastTo(Instance.VIEWER, Message.newBuilder().setEvNetworkDelta(ev).build());
	}

	public static void removeLink(int cvid1, int cvid2) {
		log.debug("Delinking: {} and {}", cvid1, cvid2);
		EV_NetworkDelta.Builder ev = EV_NetworkDelta.newBuilder()
				.setLinkRemoved(LinkRemoved.newBuilder().setCvid1(cvid1).setCvid2(cvid2));

		NetworkStore.broadcastTo(Instance.VIEWER, Message.newBuilder().setEvNetworkDelta(ev).build());
	}

}