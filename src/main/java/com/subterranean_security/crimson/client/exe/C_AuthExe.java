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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.exe.AuthExe;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
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
public class C_AuthExe extends AuthExe implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(C_AuthExe.class);

	/**
	 * The size of the random String used in Key authentication
	 */
	public static final int MAGIC_LENGTH = 128;

	public C_AuthExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	@Override
	public void rq_key_challenge(Message m) {
		if (currentStage != AuthStage.GROUP_STAGE1) {
			return;
		}

		byte[] key = ConfigStore.getConfig().getPublicKey().toByteArray();

		String result = CryptoUtil.hashSign(m.getRqKeyChallenge().getMagic(), key);
		connector.write(Message.newBuilder().setId(m.getId())
				.setRsKeyChallenge(RS_KeyChallenge.newBuilder().setResult(result)));
	}

	@Override
	public void m1_challenge_result(Message m) {
		if (currentStage != AuthStage.GROUP_STAGE1) {
			return;
		}

		if (!m.getRsOutcome().getResult()) {
			log.debug("Authentication with server failed");
			rejectClient();
			return;
		} else {
			currentStage = AuthStage.GROUP_STAGE2;
		}

		byte[] key = ConfigStore.getConfig().getPublicKey().toByteArray();

		// Send authentication challenge
		int id = IDGen.msg();

		String magic = RandomUtil.randString(MAGIC_LENGTH);

		boolean flag = true;
		try {

			Message rs = connector
					.writeAndGetResponse(Message.newBuilder().setId(id)
							.setRqKeyChallenge(RQ_KeyChallenge.newBuilder()
									.setGroupName(ConfigStore.getConfig().getGroupName()).setMagic(magic)))
					.get(TimeoutConstants.DEFAULT);

			if (rs != null) {
				if (!CryptoUtil.verifyKeyChallenge(magic, key, rs.getRsKeyChallenge().getResult())) {
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
			acceptClient();
		} else {
			rejectClient();
		}
		connector.write(Message.newBuilder().setId(id).setRsOutcome(Outcome.newBuilder().setResult(flag)));

	}

}
