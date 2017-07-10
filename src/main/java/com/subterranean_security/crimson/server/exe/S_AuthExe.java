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

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH.AuthType;
import com.subterranean_security.crimson.core.exe.AuthExe;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.exception.MessageFlowException;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.M1_AuthAttempt;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.M1_ChallengeResult;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.AuthStore;

/**
 * Client authentication handlers.
 * 
 * @author cilki
 * @since 4.0.0
 */
public final class S_AuthExe extends AuthExe implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(S_AuthExe.class);

	public S_AuthExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	@Override
	public void m1_challenge_result(Message m) {
		if (currentStage != AuthStage.GROUP_STAGE2) {
			log.debug("Rejecting authorization challenge result for connector: {} due to invalid state: {}",
					connector.getCvid(), currentStage);
			rejectClient();
			return;
		}

		if (m.getM1ChallengeResult() == null) {
			throw new MessageFlowException(null, m);
		} else if (m.getM1ChallengeResult().getResult()) {
			acceptClient();
		} else {
			log.debug("The client refused to authenticate");
			connector.close();
		}

	}

	@Override
	public void m1_auth_attempt(Message m) {
		if (currentStage != AuthStage.UNAUTHENTICATED) {
			log.debug("Rejecting authorization request for connector: {} due to invalid state: {}", connector.getCvid(),
					currentStage);
			return;
		}

		M1_AuthAttempt m1 = m.getM1AuthAttempt();

		switch (AuthType.valueOf(m1.getAuthType())) {
		case KEY:
			currentStage = AuthStage.GROUP_STAGE1;

			AttributeGroup keyGroup = AuthStore.getKeyGroup(m1.getGroupName());
			if (keyGroup == null) {
				log.debug("Authentication failed because the client supplied an unknown group: {}", m1.getGroupName());
				rejectClient();
				return;
			}

			authID = keyGroup.getInt(AK_AUTH.ID);

			int mSeqID = IDGen.msg();

			String magic = RandomUtil.randString(MAGIC_LENGTH);

			RS_KeyChallenge response = null;
			try {
				Message rs = connector
						.writeAndGetResponse(Message.newBuilder().setId(mSeqID).setRqKeyChallenge(
								RQ_KeyChallenge.newBuilder().setGroupName(keyGroup.getName()).setMagic(magic)))
						.get(TimeoutConstants.DEFAULT);

				if (rs.getRsKeyChallenge() == null)
					throw new MessageFlowException(RQ_KeyChallenge.class, rs);

				response = rs.getRsKeyChallenge();
			} catch (MessageTimeout | InterruptedException e1) {
				log.debug("");
				rejectClient();
			}

			boolean flag = response.getResult().equals(CryptoUtil.hashSign(magic, keyGroup.getGroupKey()));

			if (flag) {
				currentStage = AuthStage.GROUP_STAGE2;
			} else {
				log.info("Challenge 1 failed");
				rejectClient();
			}
			connector.write(Message.newBuilder().setId(mSeqID)
					.setM1ChallengeResult(M1_ChallengeResult.newBuilder().setResult(flag)));

			break;

		case PASSWORD:

			// TODO implement
			rejectClient();
			break;
		case NONE:
			// come on in
			authID = 0;

			acceptClient();
			break;
		default:
			log.error("Unsupported authentication type: {}", m1.getAuthType());
			break;

		}

	}

	@Override
	public void rq_key_challenge(Message m) {
		if (currentStage != AuthStage.GROUP_STAGE2) {
			return;
		}
		RQ_KeyChallenge rq = m.getRqKeyChallenge();

		connector.write(Message.newBuilder().setId(m.getId())
				.setRsKeyChallenge(RS_KeyChallenge.newBuilder().setResult(CryptoUtil.signGroupChallenge(rq.getMagic(),
						AuthStore.getKeyGroup(rq.getGroupName()).getBytes(AK_AUTH.PRIVATE_KEY)))));

	}

	@Override
	public void rq_create_auth_group(Message m) {
		connector.write(Message.newBuilder().setId(m.getId())
				.setRsOutcome(AuthStore.create(m.getRqCreateAuthGroup().getAuthMethod())));
	}

	@Override
	public void rq_remove_auth_group(Message m) {
		connector.write(Message.newBuilder().setRsOutcome(AuthStore.remove(m.getRqRemoveAuthGroup().getId())));
	}

}
