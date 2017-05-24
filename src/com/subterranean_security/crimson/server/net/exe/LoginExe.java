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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_Login;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.SMSG.RS_CloudUser;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.server.store.ServerDatabaseStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.services.Services;

public final class LoginExe {
	private static final Logger log = LoggerFactory.getLogger(LoginExe.class);

	private LoginExe() {
	}

	public static Outcome rq_login(Connector receptor, Message m) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		log.debug("Processing login request from: " + receptor.getRemoteIP());
		ViewerProfile vp = null;
		RS_CloudUser cloud = null;

		// by attempting to login, this receptor has revealed it is a viewer
		receptor.setInstance(Universal.Instance.VIEWER);

		// validate username
		String user = m.getRqLogin().getUsername();
		if (!ValidationUtil.username(user))
			return outcome.setResult(false).setComment("The provided username is invalid")
					.setTime(System.currentTimeMillis() - t1).build();

		if (Boolean.parseBoolean(System.getProperty("mode.example", "false"))) {
			vp = new ViewerProfile(receptor.getCvid());
			user = "user_" + Math.abs(RandomUtil.nextInt());
			passLogin(receptor, m.getId(), vp);

			return outcome.setResult(true).setTime(System.currentTimeMillis() - t1).build();
		}

		// find user
		vp = ProfileStore.getViewer(user);

		if (vp == null) {
			if (Boolean.parseBoolean(System.getProperty("mode.cloud", "false"))) {
				// check if the cloud server has this profile
				cloud = Services.getCloudUser(user);
				if (cloud != null) {
					// create ViewerProfile
					vp = new ViewerProfile(receptor.getCvid());
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

	private static Outcome handleAuthentication(Connector receptor, Message m, String user) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			log.debug("Issuing user challenge");
			RQ_LoginChallenge.Builder challenge = RQ_LoginChallenge.newBuilder().setCloud(false);
			if (ServerDatabaseStore.getDatabase().userExists(user)) {
				challenge.setSalt(ServerDatabaseStore.getDatabase().getSalt(user));
			} else {
				throw new Exception("Provided user could not be found");
			}

			MessageFuture future = receptor
					.writeAndGetResponse(Message.newBuilder().setId(m.getId()).setRqLoginChallenge(challenge).build());

			outcome.setResult(ServerDatabaseStore.getDatabase().validLogin(user,
					future.get(5000).getRsLoginChallenge().getResult()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static Outcome handleCloudAuthentication(Connector receptor, Message m, RS_CloudUser cloud) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			log.debug("Issuing cloud user challenge");
			MessageFuture future = receptor.writeAndGetResponse(Message.newBuilder().setId(m.getId())
					.setRqLoginChallenge(RQ_LoginChallenge.newBuilder().setCloud(true).setSalt(cloud.getSalt()))
					.build());

			outcome.setResult(future.get(5000).getRsLoginChallenge().getResult().equals(cloud.getPassword()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private static void passLogin(Connector receptor, int id, ViewerProfile vp) {
		try {
			log.debug("Accepting login: " + vp.get(AKeySimple.VIEWER_USER));
			// this connection is now authenticated
			receptor.setState(ConnectionState.AUTHENTICATED);
			ConnectionStore.add(receptor);

			updateViewerProfile(receptor, vp);

			Date lastLogin = vp.getLastLoginTime();

			receptor.write(Message.newBuilder().setId(id)
					.setRsLogin(RS_Login.newBuilder().setResponse(Outcome.newBuilder().setResult(true))
							.setSpd(ProfileStore.getServer().getUpdates(lastLogin, vp)))
					.build());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void updateViewerProfile(Connector receptor, ViewerProfile vp) {
		vp.set(AKeySimple.VIEWER_LOGIN_IP, receptor.getRemoteIP());
		vp.set(AKeySimple.VIEWER_LOGIN_TIME, new Date().toString());
	}

	private static void failLogin(Connector receptor, int id, ViewerProfile vp, Outcome outcome) {
		log.debug("Rejecting login: " + vp.get(AKeySimple.VIEWER_USER));
		receptor.write(Message.newBuilder().setId(id).setRsLogin(RS_Login.newBuilder().setResponse(outcome)).build());
		receptor.close();
	}

}
