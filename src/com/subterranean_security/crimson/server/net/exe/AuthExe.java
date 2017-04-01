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

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.server.store.Authentication;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.Universal;

public final class AuthExe {
	private static final Logger log = LoggerFactory.getLogger(AuthExe.class);

	private AuthExe() {
	}

	public static void mi_challenge_result(Connector r, Message m) {
		if (r.getState() != ConnectionState.AUTH_STAGE2) {
			log.debug("Rejecting authorization challenge result for connector: {} due to invalid state: {}",
					r.getCvid(), r.getState());
			return;
		}
		if (m.getMiChallengeResult().getResult()) {
			acceptClient(r);
			DeltaExe.ev_profileDelta(r, m.getMiChallengeResult().getPd());
		} else {
			log.debug("Authentication failed");
			r.close();
		}

	}

	public static void mi_auth_request(Connector r, Message m) {
		if (r.getState() != ConnectionState.CONNECTED) {
			log.debug("Rejecting authorization request for connector: {} due to invalid state: {}", r.getCvid(),
					r.getState());
			return;
		} else {
			r.setState(ConnectionState.AUTH_STAGE1);
		}
		MI_AuthRequest auth = m.getMiAuthRequest();

		switch (auth.getType()) {

		case GROUP:
			final AuthenticationGroup group = Authentication.getGroup(auth.getGroupName());
			if (group == null) {
				log.debug("Authentication failed: Invalid Group: {}", auth.getGroupName());
				r.setState(ConnectionState.CONNECTED);
				return;
			} else {
				// authID =
				// Authentication.getGroupMethod(auth.getGroupName()).getId();
			}
			final int mSeqID = IDGen.msg();

			final String magic = RandomUtil.randString(64);
			RQ_GroupChallenge rq = RQ_GroupChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
			r.write(Message.newBuilder().setId(mSeqID).setRqGroupChallenge(rq).build());

			try {
				RS_GroupChallenge rs = r.getResponse(mSeqID).get(7000).getRsGroupChallenge();
				boolean flag = rs.getResult().equals(CryptoUtil.hashSign(magic, group.getGroupKey()));
				try {
					group.destroy();
				} catch (DestroyFailedException e) {
				}

				if (flag) {
					r.setState(ConnectionState.AUTH_STAGE2);
				} else {
					log.info("Challenge 1 failed");
					r.setState(ConnectionState.CONNECTED);
				}
				r.write(Message.newBuilder().setId(mSeqID)
						.setMiChallengeResult(MI_GroupChallengeResult.newBuilder().setResult(flag).build()).build());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.debug("Failed to get challenge from client");
			}

			break;

		case PASSWORD:
			AuthMethod am = Authentication.getPassword(auth.getPassword());
			if (am == null) {
				log.debug("Password authentication failed");
				r.setState(ConnectionState.CONNECTED);
				break;
			} else {
				// authID = am.getId();
				// drop into NO_AUTH
			}
		case NO_AUTH:
			// come on in
			acceptClient(r);
			DeltaExe.ev_profileDelta(r, auth.getPd());
			break;
		default:
			break;

		}

	}

	private static void acceptClient(Connector receptor) {
		receptor.setState(ConnectionState.AUTHENTICATED);
		receptor.setInstance(Universal.Instance.CLIENT);

		try {
			if (ProfileStore.getClient(receptor.getCvid()) == null) {
				ProfileStore.addClient(new ClientProfile(receptor.getCvid()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ProfileStore.getClient(receptor.getCvid()).setAuthID(authID);

		ConnectionStore.add(receptor);
	}

}
