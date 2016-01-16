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
package com.subterranean_security.crimson.client.network;

import java.util.concurrent.TimeUnit;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.GetInfo;
import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.network.BasicExecutor;
import com.subterranean_security.crimson.core.network.ConnectionState;
import com.subterranean_security.crimson.core.proto.msg.Auth.ChallengeResult_1W;
import com.subterranean_security.crimson.core.proto.msg.Auth.Challenge_RQ;
import com.subterranean_security.crimson.core.proto.msg.Auth.Challenge_RS;
import com.subterranean_security.crimson.core.proto.msg.Gen.Group;
import com.subterranean_security.crimson.core.proto.msg.MSG.Message;
import com.subterranean_security.crimson.core.utility.CUtil;
import com.subterranean_security.crimson.core.utility.Crypto;
import com.subterranean_security.crimson.core.utility.IDGen;
import com.subterranean_security.crimson.server.ServerStore.Connections;

import io.netty.util.ReferenceCountUtil;

public class ClientExecutor extends BasicExecutor {

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

	// TODO Check connection status to prevent attacks
	private void challenge_rq(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		Group group = null;
		try {
			group = (Group) Client.clientDB.getObject("group");
		} catch (Exception e1) {
			e1.printStackTrace();
			Logger.debug("Unable to get group information");
			return;
		}
		String result = Crypto.sign(m.getChallengeRq().getMagic(), group.getKey());
		Challenge_RS rs = Challenge_RS.newBuilder().setResult(result).build();
		connector.handle.write(Message.newBuilder().setId(m.getId()).setChallengeRs(rs).build());
	}

	private void challengeResult_1w(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getChallengeresult1W().getResult()) {
			Logger.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		Group group = null;
		try {
			group = (Group) Client.clientDB.getObject("group");
		} catch (Exception e1) {
			e1.printStackTrace();
			Logger.debug("Unable to get group information");
			return;
		}
		final String key = group.getKey();

		// Send authentication challenge
		final int id = IDGen.get();

		final String magic = CUtil.Misc.nameGen(64);
		Challenge_RQ rq = Challenge_RQ.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.handle.write(Message.newBuilder().setId(id).setChallengeRq(rq).build());

		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.cq.take(id, 7, TimeUnit.SECONDS);
					if (rs != null) {
						if (!rs.getChallengeRs().getResult().equals(Crypto.sign(magic, key))) {
							Logger.info("Server challenge failed");
							flag = false;
						}

					} else {
						Logger.debug("No response to challenge");
						flag = false;
					}
				} catch (InterruptedException e) {
					Logger.debug("No response to challenge");
					flag = false;
				}

				ChallengeResult_1W.Builder oneway = ChallengeResult_1W.newBuilder().setResult(flag);
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
