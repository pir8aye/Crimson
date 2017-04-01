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
package com.subterranean_security.crimson.viewer.net.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.proto.Delta.MI_TriggerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.ProfileTimestamp;
import com.subterranean_security.crimson.core.proto.Login.RQ_Login;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_Login;
import com.subterranean_security.crimson.core.proto.Login.RS_LoginChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class LoginCom {
	private static final Logger log = LoggerFactory.getLogger(LoginCom.class);

	private LoginCom() {
	}

	public static Outcome login(String user, String pass) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		int id = IDGen.msg();

		ConnectionStore.route(Message.newBuilder().setId(id).setRqLogin(RQ_Login.newBuilder().setUsername(user)));

		RS_Login loginResponse = null;
		try {

			Message response = ConnectionStore.waitForResponse(0, id, 5);
			if (response == null) {
				return outcome.setComment("Request timeout").build();
			} else if (response.hasRqLoginChallenge()) {
				loginResponse = handleChallenge(response, pass);
			} else if (response.hasRsLogin()) {
				loginResponse = response.getRsLogin();
			}

		} catch (InterruptedException e) {
			return outcome.build();
		} catch (Timeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (loginResponse != null) {
			outcome.setResult(loginResponse.getResponse().getResult());
			if (loginResponse.getResponse().hasComment())
				outcome.setComment(loginResponse.getResponse().getComment());

		} else {
			outcome.setComment("Authentication failed");
		}

		if (outcome.getResult()) {
			passLogin(loginResponse);
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static void passLogin(RS_Login rsLogin) {

		// load interface
		if (MainFrame.main == null) {
			MainFrame.main = new MainFrame();
		}

		ProfileStore.update(rsLogin.getSpd());
		triggerProfileDelta();

	}

	private static RS_Login handleChallenge(Message m, String pass) {
		RQ_LoginChallenge challenge = m.getRqLoginChallenge();
		String result = challenge.getCloud() ? CryptoUtil.hashOpencartPassword(pass, challenge.getSalt())
				: CryptoUtil.hashCrimsonPassword(pass, challenge.getSalt());
		ConnectionStore.route(Message.newBuilder().setId(m.getId())
				.setRsLoginChallenge(RS_LoginChallenge.newBuilder().setResult(result)).build());

		try {
			Message response = ConnectionStore.waitForResponse(0, m.getId(), 5);

			return (response != null && response.hasRsLogin()) ? response.getRsLogin() : null;
		} catch (InterruptedException e) {
			return null;
		} catch (Timeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void triggerProfileDelta() {
		log.debug("Triggering profile delta update");

		// report last update timestamps for current clients
		MI_TriggerProfileDelta.Builder mi = MI_TriggerProfileDelta.newBuilder();
		for (int i = 0; i < ProfileStore.clients.size(); i++) {
			ClientProfile cp = ProfileStore.clients.get(i);

			mi.addProfileTimestamp(
					ProfileTimestamp.newBuilder().setCvid(cp.getCid()).setTimestamp(cp.getLastUpdate().getTime()));
		}
		ConnectionStore.route(Message.newBuilder().setMiTriggerProfileDelta(mi));

	}

}
