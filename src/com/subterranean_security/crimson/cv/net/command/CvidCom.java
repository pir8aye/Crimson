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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.LCVID;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.proto.CVID.RQ_Cvid;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.IDGen;

public final class CvidCom {
	private static final Logger log = LoggerFactory.getLogger(CvidCom.class);

	private CvidCom() {
	}

	public static void getCvid(Connector c) throws Timeout, InterruptedException {
		RQ_Cvid.Builder rq = RQ_Cvid.newBuilder().addAllLcvid(LCVID.getLcvidSet());

		log.debug("Requesting CVID from server. LCVID count: {}", rq.getLcvidCount());

		Message rs = c.writeAndGetResponse(Message.newBuilder().setId(IDGen.msg()).setRqCvid(rq).build()).get(5000);
		if (rs.hasRsCvid()) {
			Common.cvid = rs.getRsCvid().getCvid();

			if (rs.getRsCvid().getLcvid() != null) {
				// add this lcvid to the database
				LCVID.addLcvid(rs.getRsCvid().getLcvid());
			}

		} else {
			// invalid message
			// throw something
		}

		log.debug("CVID: {}", Common.cvid);
	}
}
