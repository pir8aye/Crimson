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
package com.subterranean_security.crimson.cv.net.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.net.exception.MessageFlowException;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.net.sequences.CVID.RQ_Cvid;
import com.subterranean_security.crimson.proto.core.net.sequences.CVID.RS_Cvid;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Commands related to obtaining a CVID (Client/Viewer ID).
 */
public final class CvidCom {
	private static final Logger log = LoggerFactory.getLogger(CvidCom.class);

	private CvidCom() {
	}

	/**
	 * Request a cvid from the server. The list of lcvids stored by this
	 * instance is sent to the server. If the server recognizes one of these
	 * lcvids, the corresponding cvid is sent back. Otherwise, the server
	 * generates a new cvid and lcvid for this instance.
	 * 
	 * @param c
	 * @throws Timeout
	 * @throws InterruptedException
	 */
	public static void getCvid(Connector c) throws Timeout, InterruptedException {
		RQ_Cvid.Builder rq = RQ_Cvid.newBuilder().addAllLcvid(LcvidStore.getLcvidSet())
				.setViewer(Universal.instance == Instance.VIEWER);

		Message rs = c.writeAndGetResponse(Message.newBuilder().setId(IDGen.msg()).setRqCvid(rq).build()).get(5000);
		if (rs.getRsCvid() == null)
			throw new MessageFlowException(RQ_Cvid.class, rs, RS_Cvid.class);

		LcvidStore.cvid = rs.getRsCvid().getCvid();
		LcvidStore.lcvid = rs.getRsCvid().getLcvid();

		// add this lcvid to the database if necessary
		LcvidStore.addLcvid(LcvidStore.lcvid);

		ConnectionStore.add(c);

	}
}
