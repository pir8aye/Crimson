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
package com.subterranean_security.crimson.sv.profile;

import java.util.ArrayList;
import java.util.Date;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.sv.net.Listener;
import com.subterranean_security.crimson.viewer.ui.UIStore;

public class ServerProfile extends Profile implements SVProfile {

	private static final long serialVersionUID = 1L;

	public ArrayList<ListenerConfig> listeners = new ArrayList<ListenerConfig>();
	public ArrayList<AuthMethod> authMethods = new ArrayList<AuthMethod>();

	// General attributes
	private Attribute messageLatency;

	public ServerProfile() {
		super();
		messageLatency = new UntrackedAttribute();
	}

	public String getMessageLatency() {
		return messageLatency.get();
	}

	public void setMessageLatency(String messageLatency) {
		this.messageLatency.set(messageLatency);
	}

	public void amalgamate(EV_ServerProfileDelta c) {
		super.amalgamate(c.getPd());

		for (ListenerConfig lc : c.getListenerList()) {
			for (ListenerConfig l : listeners) {
				if (lc.getId() == l.getId()) {
					listeners.remove(l);
					break;
				}
			}

			listeners.add(lc);
		}
		if (UIStore.netMan != null && c.getListenerCount() > 0) {
			UIStore.netMan.lp.lt.fireTableDataChanged();
		}

		for (AuthMethod am : c.getAuthMethodList()) {
			boolean modified = false;
			for (AuthMethod a : authMethods) {
				if (a.getId() == am.getId()) {
					a = AuthMethod.newBuilder().mergeFrom(a).mergeFrom(am).build();
					modified = true;
				}
			}
			if (!modified) {
				authMethods.add(am);
			}
		}

		if (UIStore.userMan != null && c.getAuthMethodCount() > 0) {
			UIStore.userMan.up.ut.fireTableDataChanged();
		}

	}

	public EV_ServerProfileDelta getUpdates(Date lastUpdate, ViewerProfile vp) {
		EV_ServerProfileDelta.Builder spd = EV_ServerProfileDelta.newBuilder();

		// add general attributes
		spd.setPd(EV_ProfileDelta.newBuilder(super.getUpdates(lastUpdate)).addGroup(AttributeGroupContainer.newBuilder()
				.putAttribute(AKeySimple.SERVER_ACTIVE_LISTENERS.getFullID(), "" + ListenerStore.getActive())
				.putAttribute(AKeySimple.SERVER_INACTIVE_LISTENERS.getFullID(), "" + ListenerStore.getInactive())));

		// TODO only changed listeners
		// add listeners
		for (Listener l : ListenerStore.getLoaded()) {
			// add everything from config except the private key
			spd.addListener(ListenerConfig.newBuilder(l.getConfig()).clearKey().build());
		}

		return spd.build();
	}

}
