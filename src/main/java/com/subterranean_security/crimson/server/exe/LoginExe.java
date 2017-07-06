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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.cloud.net.exe.CloudLoginExe;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_CloudUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_Login;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ServerDatabaseStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class LoginExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(LoginExe.class);

	public LoginExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	public void rq_login(Message m) {
		Outcome.Builder outcome = Outcome.newBuilder().setTime(System.currentTimeMillis());

		log.debug("Processing login request from: {} (CVID: {})", connector.getRemoteIP(), connector.getCvid());
		ViewerProfile vp = null;
		RS_CloudUser cloud = null;

		// by attempting to login, this connector has revealed it is a viewer
		connector.setInstance(Universal.Instance.VIEWER);

		// validate username
		String user = m.getRqLogin().getUsername();
		if (!ValidationUtil.username(user)) {
			log.debug("The username ({}) is invalid", user);
			passOrFail(m.getId(), vp, outcome.setResult(false).setComment("Invalid username"));
			return;
		}

		// pass if server is in example mode
		if (Boolean.parseBoolean(System.getProperty("mode.example", "false"))) {
			vp = new ViewerProfile(connector.getCvid());
			user = "user_" + Math.abs(RandomUtil.nextInt());

			passOrFail(m.getId(), vp, outcome.setResult(true));
			return;
		}

		// find user
		vp = ServerProfileStore.getViewer(user);
		if (vp == null)
			log.debug("No ViewerProfile was found in the local database");
		else
			log.debug("Found local ViewerProfile for user: {}", user);

		// check cloud
		if (vp == null && Boolean.parseBoolean(System.getProperty("mode.cloud", "false"))) {
			// check if the cloud server has this profile
			cloud = CloudLoginExe.getCloudUser(user);
			if (cloud != null) {
				// create ViewerProfile
				vp = new ViewerProfile(connector.getCvid());
				vp.set(AK_VIEWER.USER, user);
				vp.getPermissions().addFlag(Perm.server.generator.generate_jar).addFlag(Perm.server.fs.read);
				ServerProfileStore.addViewer(vp);
			}

		}

		// if profile is still not found
		if (vp == null) {
			log.debug("The user ({}) could not be found", user);
			passOrFail(m.getId(), vp, outcome.setResult(false).setComment("User does not exist"));
			return;
		} else {
			vp.setCvid(connector.getCvid());
		}

		Outcome authOutcome = (cloud == null) ? handleAuthentication(m, user) : handleCloudAuthentication(m, cloud);

		passOrFail(m.getId(), vp, outcome.setResult(authOutcome.getResult()));

	}

	private Outcome handleAuthentication(Message m, String user) {
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

			MessageFuture future = connector
					.writeAndGetResponse(Message.newBuilder().setId(m.getId()).setRqLoginChallenge(challenge).build());

			outcome.setResult(ServerDatabaseStore.getDatabase().validLogin(user,
					future.get(5000).getRsLoginChallenge().getResult()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private Outcome handleCloudAuthentication(Message m, RS_CloudUser cloud) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		try {
			log.debug("Issuing cloud user challenge");
			MessageFuture future = connector.writeAndGetResponse(Message.newBuilder().setId(m.getId())
					.setRqLoginChallenge(RQ_LoginChallenge.newBuilder().setCloud(true).setSalt(cloud.getSalt()))
					.build());

			outcome.setResult(future.get(5000).getRsLoginChallenge().getResult().equals(cloud.getPassword()));
		} catch (Exception e) {
			outcome.setResult(false).setComment(e.getMessage());
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	private void passLogin(int id, ViewerProfile vp, Outcome outcome) {
		log.debug("Accepting login: " + vp.get(AK_VIEWER.USER) + " (" + vp.getCvid() + ")");

		parent.initAuth();

		// this connection is now authenticated
		connector.setState(ConnectionState.AUTHENTICATED);
		// ConnectionStore.add(connector);

		updateViewerProfile(vp);

		long lastLogin = vp.getLastLoginTime();

		try {
			connector.write(Message.newBuilder().setId(id)
					.setRsLogin(RS_Login.newBuilder().setResponse(outcome)
							.setSpd(ServerProfileStore.getServer().getUpdates(lastLogin, vp))
							.setVpd(vp.getViewerUpdates(lastLogin)))
					.build());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateViewerProfile(ViewerProfile vp) {
		vp.set(AK_VIEWER.LOGIN_IP, connector.getRemoteIP());
		vp.set(AK_VIEWER.LOGIN_TIME, new Date().toString());
	}

	private void failLogin(int id, Outcome outcome) {
		log.debug("Rejecting login from: {} ", connector.getRemoteIP());
		connector.write(Message.newBuilder().setId(id).setRsLogin(RS_Login.newBuilder().setResponse(outcome)).build());
		connector.close();
	}

	private void passOrFail(int id, ViewerProfile vp, Outcome.Builder outcome) {
		// set time
		outcome.setTime(System.currentTimeMillis() - outcome.getTime());

		if (outcome.getResult()) {
			passLogin(id, vp, outcome.build());
		} else {
			failLogin(id, outcome.build());
		}
	}

}
