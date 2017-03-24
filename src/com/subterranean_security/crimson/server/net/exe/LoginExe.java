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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_Login;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.SMSG.RS_CloudUser;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.Validation;
import com.subterranean_security.crimson.server.ServerState;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.server.net.ServerCommands;
import com.subterranean_security.crimson.server.store.ConnectionStore;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.services.Services;

public final class LoginExe {
	private static final Logger log = LoggerFactory.getLogger(LoginExe.class);

	private LoginExe() {
	}

	public static Outcome rq_login(Receptor receptor, Message m) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		log.debug("Processing login request from: " + receptor.getRemoteAddress());
		ViewerProfile vp = null;
		RS_CloudUser cloud = null;

		// by attempting to login, this receptor has revealed it is a viewer
		receptor.setInstance(Universal.Instance.VIEWER);

		// validate username
		String user = m.getRqLogin().getUsername();
		if (!Validation.username(user))
			return outcome.setResult(false).setComment("The provided username is invalid")
					.setTime(System.currentTimeMillis() - t1).build();

		if (ServerState.isExampleMode()) {
			vp = new ViewerProfile(IDGen.cvid());
			ServerCommands.setCvid(receptor, vp.getCvid());
			user = "user_" + Math.abs(RandomUtil.nextInt());
			passLogin(receptor, m.getId(), vp);

			return outcome.setResult(true).setTime(System.currentTimeMillis() - t1).build();
		}

		// find user
		vp = ProfileStore.getViewer(user);

		if (vp == null) {
			if (ServerState.isCloudMode()) {
				// check if the cloud server has this profile
				cloud = Services.getCloudUser(user);
				if (cloud != null) {
					// create ViewerProfile
					vp = new ViewerProfile(IDGen.cvid());
					vp.set(AKeySimple.VIEWER_USER, user);
					vp.getPermissions().addFlag(Perm.server.generator.generate).addFlag(Perm.server.fs.read);
					ProfileStore.addViewer(vp);
				}
			}
		}

		// if profile is still not found
		if (vp == null) {
			return outcome.setResult(false).setComment("The provided user could not be found")
					.setTime(System.currentTimeMillis() - t1).build();
		}

		Outcome authOutcome = (cloud == null) ? handleAuthentication(receptor, m, user)
				: handleCloudAuthentication(receptor, m, cloud);

		outcome.setResult(authOutcome.getResult());

		if (outcome.getResult()) {
			passLogin(receptor, m.getId(), vp);
		} else {
			failLogin(receptor, m.getId(), vp, outcome.build());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static Outcome handleAuthentication(Receptor receptor, Message m, String user) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			log.debug("Issuing user challenge");
			RQ_LoginChallenge.Builder challenge = RQ_LoginChallenge.newBuilder().setCloud(false);
			if (DatabaseStore.getDatabase().userExists(user)) {
				challenge.setSalt(DatabaseStore.getDatabase().getSalt(user));
			} else {
				throw new Exception("Provided user could not be found");
			}

			receptor.handle.write(Message.newBuilder().setId(m.getId()).setRqLoginChallenge(challenge).build());
			Message response = receptor.cq.take(m.getId(), 5, TimeUnit.SECONDS);

			outcome.setResult(DatabaseStore.getDatabase().validLogin(user, response.getRsLoginChallenge().getResult()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static Outcome handleCloudAuthentication(Receptor receptor, Message m, RS_CloudUser cloud) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			log.debug("Issuing cloud user challenge");
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRqLoginChallenge(RQ_LoginChallenge.newBuilder().setCloud(true).setSalt(cloud.getSalt()))
					.build());
			Message response = receptor.cq.take(m.getId(), 5, TimeUnit.SECONDS);

			outcome.setResult(response.getRsLoginChallenge().getResult().equals(cloud.getPassword()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static void passLogin(Receptor receptor, int id, ViewerProfile vp) {
		try {
			log.debug("Accepting login: " + vp.get(AKeySimple.VIEWER_USER));
			// this connection is now authenticated
			receptor.setState(ConnectionState.AUTHENTICATED);
			ConnectionStore.add(receptor);

			updateViewerProfile(receptor, vp);

			Date lastLogin = vp.getLastLoginTime();

			receptor.handle.write(Message.newBuilder().setId(id)
					.setRsLogin(RS_Login.newBuilder().setResponse(Outcome.newBuilder().setResult(true))
							.setSpd(ProfileStore.getServer().getUpdates(lastLogin, vp)))
					.build());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void updateViewerProfile(Receptor receptor, ViewerProfile vp) {
		ServerCommands.setCvid(receptor, vp.getCvid());

		vp.set(AKeySimple.VIEWER_LOGIN_IP, receptor.getRemoteAddress());
		vp.set(AKeySimple.VIEWER_LOGIN_TIME, new Date().toString());

	}

	private static void failLogin(Receptor receptor, int id, ViewerProfile vp, Outcome outcome) {
		log.debug("Rejecting login: " + vp.get(AKeySimple.VIEWER_USER));
		receptor.handle
				.write(Message.newBuilder().setId(id).setRsLogin(RS_Login.newBuilder().setResponse(outcome)).build());
		receptor.close();
	}

}
