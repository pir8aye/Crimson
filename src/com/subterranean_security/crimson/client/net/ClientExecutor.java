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
package com.subterranean_security.crimson.client.net;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.GetInfo;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallengeResult_1W;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallenge_RQ;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallenge_RS;
import com.subterranean_security.crimson.core.proto.net.Gen.Group;
import com.subterranean_security.crimson.core.proto.net.MSG.Message;
import com.subterranean_security.crimson.core.proto.net.Stream.Param;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;

import io.netty.util.ReferenceCountUtil;

public class ClientExecutor extends BasicExecutor {
	private static final Logger log = CUtil.Logging.getLogger(ClientExecutor.class);

	private ClientConnector connector;

	public ClientExecutor(ClientConnector vc) {
		connector = vc;

		ubt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.uq.take();
					} catch (InterruptedException e) {
						return;
					}

					if (m.hasStreamStartEv()) {
						stream_start_ev(m);
					}

					ReferenceCountUtil.release(m);
				}
			}
		});
		ubt.start();

		nbt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.nq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasChallengeRq()) {
						challenge_rq(m);
					} else if (m.hasChallengeresult1W()) {
						challengeResult_1w(m);
					} else {
						connector.cq.put(m.getId(), m);
					}

					ReferenceCountUtil.release(m);

				}
			}
		});
		nbt.start();
	}

	private void stream_start_ev(Message m) {
		Param p = m.getStreamStartEv().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(p.getStreamID(), new InfoSlave(p));
		}
	}

	private void challenge_rq(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		Group group = null;
		try {
			group = (Group) Client.clientDB.getObject("auth.group");
		} catch (Exception e1) {
			e1.printStackTrace();
			log.debug("Unable to get group information");
			return;
		}
		String result = Crypto.sign(m.getChallengeRq().getMagic(), group.getKey());
		GroupChallenge_RS rs = GroupChallenge_RS.newBuilder().setResult(result).build();
		connector.handle.write(Message.newBuilder().setId(m.getId()).setChallengeRs(rs).build());
	}

	private void challengeResult_1w(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getChallengeresult1W().getResult()) {
			log.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		Group group = null;
		try {
			group = (Group) Client.clientDB.getObject("auth.group");
		} catch (Exception e1) {
			e1.printStackTrace();
			log.debug("Unable to get group information");
			return;
		}
		final String key = group.getKey();

		// Send authentication challenge
		final int id = IDGen.get();

		final String magic = CUtil.Misc.randString(64);
		GroupChallenge_RQ rq = GroupChallenge_RQ.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.handle.write(Message.newBuilder().setId(id).setChallengeRq(rq).build());

		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.cq.take(id, 7, TimeUnit.SECONDS);
					if (rs != null) {
						if (!rs.getChallengeRs().getResult().equals(Crypto.sign(magic, key))) {
							log.info("Server challenge failed");
							flag = false;
						}

					} else {
						log.debug("No response to challenge");
						flag = false;
					}
				} catch (InterruptedException e) {
					log.debug("No response to challenge");
					flag = false;
				}

				GroupChallengeResult_1W.Builder oneway = GroupChallengeResult_1W.newBuilder().setResult(flag);
				if (flag) {
					oneway.setInitialInfo(GetInfo.getStatic());
					connector.setState(ConnectionState.AUTHENTICATED);
				} else {
					connector.setState(ConnectionState.CONNECTED);
				}
				connector.handle.write(Message.newBuilder().setId(id).setChallengeresult1W(oneway.build()).build());
			}
		}).start();

	}

}
