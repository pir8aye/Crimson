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

import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.LocationUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.set.ProfileSetFactory;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class DeltaExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(DeltaExe.class);

	public DeltaExe(Connector connector) {
		super(connector);
	}

	public void ev_profileDelta(EV_ProfileDelta pd) {
		// TODO resolve location somewhere else!
		// pd = EV_ProfileDelta.newBuilder(pd).mergeFrom(resolveLocation(r,
		// pd)).build();

		if (pd.getCvid() != connector.getCvid()) {
			log.warn("CVID from ProfileDelta ({}) differs from connector CVID ({})", pd.getCvid(), connector.getCvid());
			pd = EV_ProfileDelta.newBuilder(pd).setCvid(connector.getCvid()).build();
		}

		ServerProfileStore.getClient(connector.getCvid()).merge(pd);
		NetworkStore.broadcastTo(Message.newBuilder().setEvProfileDelta(pd).build(), new ProfileSetFactory()
				.addFilter(Instance.VIEWER).addFilter(connector.getCvid(), Perm.client.visibility));
	}

	private EV_ProfileDelta resolveLocation(EV_ProfileDelta pd) {
		EV_ProfileDelta.Builder update = EV_ProfileDelta.newBuilder();

		String ip = null;
		if (!pd.containsStrAttr(AK_NET.EXTERNAL_IPV4.getGTID())) {
			// amend IP
			ip = connector.getRemoteIP();
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

	public void m1_trigger_profile_delta(Message m) {

		if (m.getMiTriggerProfileDelta() == null)
			return;// TODO throw something

		Map<Integer, Long> timestamps = m.getMiTriggerProfileDelta().getUpdateTimestampMap();
		for (Profile cp : new ProfileSetFactory().addFilter(Instance.CLIENT)
				.addFilter(connector.getCvid(), Perm.client.visibility).build()) {

			if (timestamps.containsKey(cp.getCvid())) {
				log.debug("Updating client in viewer");
				connector.write(Message.newBuilder().setEvProfileDelta(cp.getUpdates(timestamps.get(cp.getCvid()))));
			} else {
				log.debug("Sending new client to viewer");
				connector.write(Message.newBuilder().setEvProfileDelta(cp.getUpdates(0)));
			}

		}

	}

}
