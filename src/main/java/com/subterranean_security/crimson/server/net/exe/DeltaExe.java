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
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.LocationUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.ProfileTimestamp;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.set.ProfileSetFactory;
import com.subterranean_security.crimson.universal.Universal.Instance;

public final class DeltaExe extends Exelet {

	private static final Logger log = LoggerFactory.getLogger(DeltaExe.class);

	public DeltaExe(Connector connector) {
		super(connector);
	}

	public static void ev_profileDelta(Connector r, EV_ProfileDelta pd) {
		// TODO resolve location somewhere else!
		// pd = EV_ProfileDelta.newBuilder(pd).mergeFrom(resolveLocation(r,
		// pd)).build();

		if (pd.getCvid() != r.getCvid()) {
			log.warn("CVID from ProfileDelta ({}) differs from connector CVID ({})", pd.getCvid(), r.getCvid());
			pd = EV_ProfileDelta.newBuilder(pd).setCvid(r.getCvid()).build();
		}

		ServerProfileStore.getClient(r.getCvid()).merge(pd);
		NetworkStore.broadcastTo(Message.newBuilder().setEvProfileDelta(pd).build(),
				new ProfileSetFactory().addFilter(Instance.VIEWER).addFilter(r.getCvid(), Perm.client.visibility));
	}

	private static EV_ProfileDelta resolveLocation(Connector receptor, EV_ProfileDelta pd) {
		EV_ProfileDelta.Builder update = EV_ProfileDelta.newBuilder();

		String ip = null;
		if (!pd.containsStrAttr(AK_NET.EXTERNAL_IPV4.getGTID())) {
			// amend IP
			ip = receptor.getRemoteIP();
			update.putStrAttr(AK_NET.EXTERNAL_IPV4.getGTID(), ip);
		} else {
			ip = pd.getStrAttrOrDefault(AK_NET.EXTERNAL_IPV4.getGTID(), "");
		}

		if (!ValidationUtil.privateIP(ip)) {
			try {
				Map<AK_LOC, String> location = LocationUtil.resolve(ip);
				for (AK_LOC key : location.keySet()) {
					update.putStrAttr(key.getGTID(), location.get(key));
				}
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

		for (ClientProfile cp : ServerProfileStore.getClientsUnderAuthorityOfViewer(r.getCvid())) {
			boolean flag = true;
			for (ProfileTimestamp pt : m.getMiTriggerProfileDelta().getProfileTimestampList()) {
				if (pt.getCvid() == cp.getCvid()) {
					log.debug("Updating client in viewer");
					r.write(Message.newBuilder().setEvProfileDelta(cp.getUpdates(pt.getTimestamp())).build());
					flag = false;
					continue;
				}
			}
			if (flag) {
				log.debug("Sending new client to viewer");
				r.write(Message.newBuilder().setEvProfileDelta(cp.getUpdates(0)).build());
			}

		}

	}

}
