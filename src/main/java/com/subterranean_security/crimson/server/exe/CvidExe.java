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

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.net.sequences.CVID.RS_Cvid;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class CvidExe extends Exelet implements ExeI {

	public CvidExe(Connector connector) {
		super(connector);
	}

	@Override
	public void rq_cvid(Message m) {
		for (String lcvid : m.getRqCvid().getLcvidList()) {
			if (LcvidStore.contains(lcvid)) {
				int cvid = LcvidStore.get(lcvid);

				connector.setCvid(cvid);
				ConnectionStore.add(connector);
				connector.write(Message.newBuilder().setId(m.getId())
						.setRsCvid(RS_Cvid.newBuilder().setCvid(cvid).setLcvid(lcvid)).build());
				return;
			}
		}

		// create a new cvid and lcvid
		int cvid = IDGen.cvid();
		String lcvid = IDGen.lcvid();
		LcvidStore.addLcvid(lcvid, cvid);

		// create a new profile for this cvid
		if (m.getRqCvid().getViewer()) {
			// ViewerProfile vp = new ViewerProfile(cvid);
			// ProfileStore.addViewer(vp);
		} else {
			// ClientProfile cp = new ClientProfile(cvid);
			// ServerProfileStore.addClient(cp);
		}

		connector.setCvid(cvid);
		ConnectionStore.add(connector);
		connector.write(Message.newBuilder().setId(m.getId())
				.setRsCvid(RS_Cvid.newBuilder().setCvid(cvid).setLcvid(lcvid)).build());
	}
}
