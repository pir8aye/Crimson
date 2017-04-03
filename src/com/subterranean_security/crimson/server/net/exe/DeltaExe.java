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
package com.subterranean_security.crimson.server.net.exe;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.ProfileTimestamp;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.LocationUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.server.net.ServerConnectionStore;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

public final class DeltaExe {
	private static final Logger log = LoggerFactory.getLogger(DeltaExe.class);

	private DeltaExe() {
	}

	public static void ev_profileDelta(Connector r, EV_ProfileDelta pd) {
		if (pd.getFig()) {
			pd = EV_ProfileDelta.newBuilder(pd).addGroup(resolveLocation(r, pd)).build();
		}

		if (pd.getCvid() != r.getCvid()) {
			System.out.println(
					"Attention: PD CVID differs from receptor! PD: " + pd.getCvid() + " receptor: " + r.getCvid());
			pd = EV_ProfileDelta.newBuilder(pd).setCvid(r.getCvid()).build();
		}

		ProfileStore.getClient(r.getCvid()).amalgamate(pd);
		ServerConnectionStore.sendToViewersWithAuthorityOverClient(r.getCvid(), Perm.client.visibility,
				Message.newBuilder().setEvProfileDelta(pd));
	}

	private static AttributeGroupContainer resolveLocation(Connector receptor, EV_ProfileDelta pd) {
		AttributeGroupContainer general = ProtoUtil.getGeneralGroup(pd);
		AttributeGroupContainer.Builder update = AttributeGroupContainer.newBuilder(general).clearAttribute();

		String ip = null;
		if (!general.containsAttribute(AKeySimple.NET_EXTERNALIP.getFullID())) {
			// amend IP
			ip = receptor.getRemoteIP();
			update.putAttribute(AKeySimple.NET_EXTERNALIP.getFullID(), ip);
		} else {
			ip = general.getAttributeOrDefault(AKeySimple.NET_EXTERNALIP.getFullID(), "");
		}

		if (!ValidationUtil.privateIP(ip)) {
			try {
				HashMap<String, String> location = LocationUtil.resolve(ip);
				update.putAttribute(AKeySimple.IPLOC_COUNTRYCODE.getFullID(), location.get("countrycode"));
				update.putAttribute(AKeySimple.IPLOC_COUNTRY.getFullID(), location.get("countryname"));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return update.build();
	}

	public static void mi_trigger_profile_delta(Connector r, Message m) {

		for (ClientProfile cp : ProfileStore.getClientsUnderAuthority(r.getCvid())) {
			boolean flag = true;
			for (ProfileTimestamp pt : m.getMiTriggerProfileDelta().getProfileTimestampList()) {
				if (pt.getCvid() == cp.getCid()) {
					log.debug("Updating client in viewer");
					r.write(
							Message.newBuilder().setEvProfileDelta(cp.getUpdates(new Date(pt.getTimestamp()))).build());
					flag = false;
					continue;
				}
			}
			if (flag) {
				log.debug("Sending new client to viewer");
				r.write(Message.newBuilder().setEvProfileDelta(cp.getUpdates(new Date(0))).build());
			}

		}

	}

}
