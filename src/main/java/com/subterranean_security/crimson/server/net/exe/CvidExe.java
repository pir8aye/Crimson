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

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.net.sequences.CVID.RS_Cvid;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

public final class CvidExe {
	private CvidExe() {
	}

	/**
	 * Respond to a request for a cvid. If the server is not familiar with one
	 * of the supplied long-cvids, respond with a new lcvid-cvid pair.
	 * 
	 * @param c
	 * @param m
	 */
	public static void rq_cvid(Connector c, Message m) {
		for (String lcvid : m.getRqCvid().getLcvidList()) {
			if (LcvidStore.contains(lcvid)) {
				int cvid = LcvidStore.get(lcvid);

				c.setCvid(cvid);
				ConnectionStore.add(c);
				c.write(Message.newBuilder().setId(m.getId())
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

		c.setCvid(cvid);
		ConnectionStore.add(c);
		c.write(Message.newBuilder().setId(m.getId()).setRsCvid(RS_Cvid.newBuilder().setCvid(cvid).setLcvid(lcvid))
				.build());
	}
}
