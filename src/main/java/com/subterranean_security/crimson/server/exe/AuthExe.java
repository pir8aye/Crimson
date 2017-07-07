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

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.exception.MessageFlowException;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.M1_AuthAttempt;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_KeyChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.universal.Universal;

/**
 * Client authentication handlers.
 * 
 * @author cilki
 * @since 4.0.0
 */
public final class AuthExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(AuthExe.class);

	/**
	 * The size of the random String used in Key authentication
	 */
	public static final int MAGIC_LENGTH = 128;

	private int authID;
	private AuthStage currentStage = AuthStage.UNAUTHENTICATED;

	private enum AuthStage {
		UNAUTHENTICATED, GROUP_STAGE1, GROUP_STAGE2, AUTHENTICATED;
	}

	public AuthExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	@Override
	// TODO put this back
	public void m1_challenge_result(Message m) {
		if (currentStage != AuthStage.GROUP_STAGE2) {
			log.debug("Rejecting authorization challenge result for connector: {} due to invalid state: {}",
					connector.getCvid(), currentStage);
			rejectClient();
			return;
		}

		if (m.getMiChallengeResult() == null) {
			throw new MessageFlowException(null, m);
		} else if (m.getMiChallengeResult().getResult()) {
			acceptClient();
			DeltaExe.ev_profileDelta(connector, m.getMiChallengeResult().getPd());
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
			rejectClient();
			return;
		}

		M1_AuthAttempt auth = m.getM1AuthAttempt();

		switch (auth.getType()) {
		case GROUP:
			currentStage = AuthStage.GROUP_STAGE1;

			KeyAuthGroup group = AuthStore.getGroup(auth.getGroupName());
			if (group == null) {
				log.debug("Authentication failed because the client supplied an unknown group: {}",
						auth.getGroupName());
				rejectClient();
				return;
			}

			authID = AuthStore.getGroupMethod(auth.getGroupName()).getId();

			int mSeqID = IDGen.msg();

			String magic = RandomUtil.randString(MAGIC_LENGTH);

			RS_KeyChallenge response = null;
			try {
				Message rs = connector.writeAndGetResponse(Message.newBuilder().setId(mSeqID)
						.setRqKeyChallenge(RQ_KeyChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic))
						.build()).get(TimeoutConstants.DEFAULT);

				if (rs.getRsKeyChallenge() == null)
					throw new MessageFlowException(RQ_KeyChallenge.class, rs);

				response = rs.getRsKeyChallenge();
			} catch (MessageTimeout | InterruptedException e1) {
				log.debug("");
				rejectClient();
			}

			boolean flag = response.getResult().equals(CryptoUtil.hashSign(magic, group.getGroupKey()));
			try {
				group.destroy();
			} catch (DestroyFailedException e) {
			}

			if (flag) {
				currentStage = AuthStage.GROUP_STAGE2;
			} else {
				log.info("Challenge 1 failed");
				rejectClient();
			}
			connector.write(Message.newBuilder().setId(mSeqID)
					.setMiChallengeResult(MI_GroupChallengeResult.newBuilder().setResult(flag)).build());

			break;

		case PASSWORD:
			AuthMethod am = AuthStore.getPassword(auth.getPassword());
			if (am == null) {
				log.debug("Password authentication failed");
				rejectClient();

			} else {
				authID = am.getId();
				acceptClient();
				DeltaExe.ev_profileDelta(connector, auth.getPd());
			}
			break;
		case NO_AUTH:
			// come on in
			authID = 0;

			acceptClient();
			DeltaExe.ev_profileDelta(connector, auth.getPd());
			break;
		default:
			log.error("Unsupported authentication type: {}", auth.getType());
			break;

		}

	}

	private void acceptClient() {
		parent.initAuth();

		currentStage = AuthStage.AUTHENTICATED;
		connector.setState(ConnectionState.AUTHENTICATED);
		connector.setInstance(Universal.Instance.CLIENT);

		// ProfileStore.getClient(receptor.getCvid()).setAuthID(authID);

		// ConnectionStore.add(receptor);
	}

	private void rejectClient() {
		currentStage = AuthStage.UNAUTHENTICATED;
		connector.setState(ConnectionState.CONNECTED);
	}

	@Override
	public void rq_create_auth_group(Message m) {
		Outcome outcome = AuthStore.create(m.getRqCreateAuthGroup().getAuthMethod());

		connector.write(Message.newBuilder().setId(m.getId()).setRsOutcome(outcome));

	}

	@Override
	public void rq_remove_auth_group(Message m) {
		AuthStore.remove(m.getRqRemoveAuthGroup().getId());
		// TODO check if removed
		connector.write(Message.newBuilder().setRsOutcome(Outcome.newBuilder().setResult(true)));
	}

}
