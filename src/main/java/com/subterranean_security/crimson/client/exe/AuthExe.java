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
package com.subterranean_security.crimson.client.exe;

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * @author cilki
 * @since 4.0.0
 */
public class AuthExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(AuthExe.class);

	/**
	 * The size of the random String used in Key authentication
	 */
	public static final int MAGIC_LENGTH = 128;

	public AuthExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	public void rq_key_challenge(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		KeyAuthGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		String result = CryptoUtil.hashSign(m.getRqKeyChallenge().getMagic(), groupKey);
		RS_KeyChallenge rs = RS_KeyChallenge.newBuilder().setResult(result).build();
		connector.write(Message.newBuilder().setId(m.getId()).setRsKeyChallenge(rs).build());
	}

	public void m1_challengeResult(Message m) {

		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getRsOutcome().getResult()) {
			log.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		KeyAuthGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		// Send authentication challenge
		final int id = IDGen.msg();

		final String magic = RandomUtil.randString(MAGIC_LENGTH);
		RQ_KeyChallenge rq = RQ_KeyChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.write(Message.newBuilder().setId(id).setRqKeyChallenge(rq).build());

		// TODO
		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.getResponse(id).get(7000);

					if (rs != null) {
						if (!CryptoUtil.verifyGroupChallenge(magic, groupKey, rs.getRsKeyChallenge().getResult())) {
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
				} catch (MessageTimeout e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (flag) {
					connector.setState(ConnectionState.AUTHENTICATED);

					oneway.setPd(Platform.fig());
				} else {
					// TODO handle more
					connector.setState(ConnectionState.CONNECTED);
				}
				connector.write(Message.newBuilder().setId(id).setRsOutcome(Outcome.newBuilder().setResult(flag)));

			}
		}).start();

	}

}
