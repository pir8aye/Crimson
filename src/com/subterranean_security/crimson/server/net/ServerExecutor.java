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

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallengeResult_1W;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallenge_RQ;
import com.subterranean_security.crimson.core.proto.net.Auth.GroupChallenge_RS;
import com.subterranean_security.crimson.core.proto.net.Delta.ProfileDelta_EV;
import com.subterranean_security.crimson.core.proto.net.FM.FileListing_RS;
import com.subterranean_security.crimson.core.proto.net.Gen.GenReport;
import com.subterranean_security.crimson.core.proto.net.Gen.Generate_RS;
import com.subterranean_security.crimson.core.proto.net.Gen.Group;
import com.subterranean_security.crimson.core.proto.net.Login.Login_RS;
import com.subterranean_security.crimson.core.proto.net.Login.ServerInfoDelta_EV;
import com.subterranean_security.crimson.core.proto.net.MSG.Message;
import com.subterranean_security.crimson.core.proto.net.State.StateChange_RQ;
import com.subterranean_security.crimson.core.storage.ClientDB;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.server.ServerStore;
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
					if (m.hasLoginRq()) {
						login_rq(m);
					} else if (m.hasGenerateRq()) {
						generate_rq(m);
					} else if (m.hasStateChangeRq()) {
						stateChange_rq(m);
					} else if (m.hasChallengeRq()) {
						challenge_rq(m);
					} else if (m.hasAuth1W()) {
						auth_1w(m);
					} else if (m.hasChallengeresult1W()) {
						challengeResult_1w(m);
					} else if (m.hasFileListingRq()) {
						file_listing_rq(m);
					} else if (m.hasFileListingRs()) {
						file_listing_rs(m);
					} else {
						receptor.cq.put(m.getId(), m);
					}

					ReferenceCountUtil.release(m);
				}
			}
		});
		nbt.start();
	}

	private void profileDelta(ProfileDelta_EV pd) {
		if (pd.getClientid() == 0) {
			// no id has been assigned yet
			int newId = ServerStore.Profiles.nextID();
			pd = ProfileDelta_EV.newBuilder().mergeFrom(pd).setClientid(newId).build();
			ServerCommands.setSvid(receptor, newId);
		}
		for (Receptor r : ServerStore.Connections.connections) {
			// somehow check permissions TODO

			if (r.getInstance() == Instance.VIEWER) {
				r.handle.write(Message.newBuilder().setUrgent(true).setProfileDeltaEv(pd).build());

			}
		}

	}

	private void challengeResult_1w(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		if (m.getChallengeresult1W().getResult()) {
			receptor.setState(ConnectionState.AUTHENTICATED);
			receptor.setInstance(Instance.CLIENT);
			ServerStore.Connections.add(receptor);
			profileDelta(m.getChallengeresult1W().getInitialInfo());
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
		switch (m.getAuth1W().getType()) {
		case GROUP:
			final Group group = ServerStore.Authentication.getGroup(m.getAuth1W().getGroupName());
			if (group == null) {
				log.debug("Authentication failed: Invalid Group: {}", m.getAuth1W().getGroupName());
				receptor.setState(ConnectionState.CONNECTED);
				return;
			}
			final int id = IDGen.get();

			final String magic = CUtil.Misc.randString(64);
			GroupChallenge_RQ rq = GroupChallenge_RQ.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
			receptor.handle.write(Message.newBuilder().setId(id).setChallengeRq(rq).build());

			new Thread(new Runnable() {
				public void run() {
					try {
						GroupChallenge_RS rs = receptor.cq.take(id, 7, TimeUnit.SECONDS).getChallengeRs();
						boolean flag = rs.getResult().equals(Crypto.sign(magic, group.getKey()));
						if (flag) {
							receptor.setState(ConnectionState.AUTH_STAGE2);
						} else {
							log.info("Challenge 1 failed");
							receptor.setState(ConnectionState.CONNECTED);
						}
						receptor.handle.write(Message.newBuilder().setId(id)
								.setChallengeresult1W(GroupChallengeResult_1W.newBuilder().setResult(flag).build())
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
			String password = m.getAuth1W().getPassword();
			if (ServerStore.Authentication.tryPassword(password)) {
				receptor.setState(ConnectionState.AUTHENTICATED);
				receptor.setInstance(Instance.CLIENT);
				ServerStore.Connections.add(receptor);
			} else {
				log.debug("Authentication failed");
				receptor.setState(ConnectionState.CONNECTED);
			}
			break;
		case NO_AUTH:
			// come on in
			receptor.setState(ConnectionState.AUTHENTICATED);
			receptor.setInstance(Instance.CLIENT);
			ServerStore.Connections.add(receptor);
			break;
		default:
			break;

		}

	}

	private void challenge_rq(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		GroupChallenge_RQ rq = m.getChallengeRq();
		Group group = ServerStore.Authentication.getGroup(rq.getGroupName());

		GroupChallenge_RS rs = GroupChallenge_RS.newBuilder().setResult(Crypto.sign(rq.getMagic(), group.getKey()))
				.build();
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setChallengeRs(rs).build());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void login_rq(Message m) {
		Login_RS.Builder rs = Login_RS.newBuilder().setResponse(
				ServerStore.Databases.system.validLogin(m.getLoginRq().getUsername(), m.getLoginRq().getHash()));
		if (rs.getResponse()) {
			log.debug("Accepting Login");
			receptor.setInstance(Instance.VIEWER);
			receptor.setState(ConnectionState.AUTHENTICATED);
			ViewerProfile vp = null;
			try {
				vp = ServerStore.Profiles.getViewer(m.getLoginRq().getSvid());
			} catch (Exception e1) {
				int svid = ServerStore.Profiles.nextID();
				ServerCommands.setSvid(receptor, svid);
				vp = new ViewerProfile(svid);
				vp.setUser(m.getLoginRq().getUsername());
				ServerStore.Profiles.addViewer(vp);
			}
			ServerStore.Connections.add(receptor);
			ClientDB vdb = ServerStore.Databases.loaded_viewers
					.get(ServerStore.Databases.system.getUID(m.getLoginRq().getUsername()));
			ServerInfoDelta_EV.Builder builder = ServerInfoDelta_EV.newBuilder();

			ArrayList<String> ips = vp.getLogin_ip();
			ArrayList<Date> times = vp.getLogin_times();
			if (times.size() != 0) {
				log.debug("Last login: " + times.get(0));
				builder.setLastLogin(times.get(0).getTime());
			}

			// TODO set last ip
			times.add(new Date());
			ips.add("cha.nge.me");// TODO

			builder.setServerStatus(Server.isRunning());
			rs.setInitialInfo(builder.build());
		}
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setLoginRs(rs).build());
		if (!rs.getResponse()) {
			log.info("Rejecting Login");
			receptor.close();
		}
	}

	private void stateChange_rq(Message m) {
		StateChange_RQ rq = m.getStateChangeRq();
		switch (rq.getType()) {
		case POWER:
			break;
		case SERVER:
			Server.setState(rq.getChange());
			break;
		default:
			break;

		}
	}

	private void generate_rq(Message m) {

		byte[] res = null;
		Generator g = null;
		try {
			g = new Generator(m.getGenerateRq().getInternalConfig());
			res = g.getResult();
		} catch (Exception e) {
			log.info("Could not generate installer");
			e.printStackTrace();

			GenReport.Builder gr = GenReport.newBuilder();
			gr.setResult(false).setGenTime(0).setComment("An unexpected error has occured");
			Generate_RS.Builder rs = Generate_RS.newBuilder().setReport(gr.build());
			receptor.handle.write(Message.newBuilder().setId(m.getId()).setGenerateRs(rs).build());
			return;
		}
		Generate_RS.Builder rs = Generate_RS.newBuilder().setInstaller(ByteString.copyFrom(res))
				.setReport(g.getReport());
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setGenerateRs(rs).build());
	}

	private void file_listing_rq(Message m) {
		ViewerProfile vp = null;
		try {
			vp = ServerStore.Profiles.getViewer(m.getFileListingRq().getClientid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (m.getFileListingRq().hasClientid()) {
			if (vp.getPermissions().verify(m.getFileListingRq().getClientid(), "fs.read")) {
				System.out.println("Permissions error");
				return;
			}
			Receptor r = ServerStore.Connections.getConnection(m.getFileListingRq().getClientid());
			r.handle.write(m);

		} else {
			if (vp.getPermissions().verify("srv.fs.read")) {
				System.out.println("Permissions error");
				return;
			}
			receptor.handle.write(Message.newBuilder().setFileListingRs(
					FileListing_RS.newBuilder().addAllListing(null).setViewerid(m.getFileListingRq().getViewerid()))
					.build());
		}

	}

	private void file_listing_rs(Message m) {
		Receptor r = ServerStore.Connections.getConnection(m.getFileListingRq().getViewerid());
		r.handle.write(m);
	}

}
