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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.security.auth.DestroyFailedException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_RemoveAuthMethod;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.ProfileTimestamp;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.core.proto.FileManager.RS_Delete;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.core.proto.Generator.RS_Generate;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.proto.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Listener.RS_AddListener;
import com.subterranean_security.crimson.core.proto.Log.LogFile;
import com.subterranean_security.crimson.core.proto.Log.LogType;
import com.subterranean_security.crimson.core.proto.Log.RS_Logs;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_Login;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.SMSG.RS_CloudUser;
import com.subterranean_security.crimson.core.proto.State.RS_ChangeServerState;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Users.RQ_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RS_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RS_EditUser;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.subscriber.SubscriberSlave;
import com.subterranean_security.crimson.core.util.AuthenticationGroup;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.ServerState;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.server.stream.SInfoSlave;
import com.subterranean_security.crimson.sv.Listener;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.services.Services;

import io.netty.util.ReferenceCountUtil;

public class ServerExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ServerExecutor.class);

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
					if (m.hasRid() && m.getRid() != 0) {
						// route
						try {
							ServerStore.Connections.getConnection(m.getRid()).handle.write(m);
						} catch (NullPointerException e) {
							log.debug("Could not forward message to CVID: {}", m.getRid());
						}
					} else if (m.hasEvProfileDelta()) {
						ev_profileDelta(m.getEvProfileDelta());
					} else if (m.hasEvKevent()) {
						ev_kevent(m);
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
					if (m.hasRid() && m.getRid() != 0) {
						// route
						try {
							ServerStore.Connections.getConnection(m.getRid()).handle.write(m);
						} catch (NullPointerException e) {
							log.debug("Could not forward message to CVID: {}", m.getRid());
						}
					} else if (m.hasRqLogin()) {
						new Thread(new Runnable() {
							public void run() {
								rq_login(m);
							}
						}).start();

					} else if (m.hasRqGenerate()) {
						rq_generate(m);
					} else if (m.hasRqGroupChallenge()) {
						rq_group_challenge(m);
					} else if (m.hasMiAuthRequest()) {
						mi_auth_request(m);
					} else if (m.hasMiChallengeresult()) {
						mi_challenge_result(m);
					} else if (m.hasRqFileListing()) {
						rq_file_listing(m);
					} else if (m.hasRsFileListing()) {
						rs_file_listing(m);
					} else if (m.hasRqAdvancedFileInfo()) {
						rq_advanced_file_info(m);
					} else if (m.hasMiStreamStart()) {
						mi_stream_start(m);
					} else if (m.hasMiStreamStop()) {
						mi_stream_stop(m);
					} else if (m.hasRqAddListener()) {
						rq_add_listener(m);
					} else if (m.hasRqRemoveListener()) {
						rq_remove_listener(m);
					} else if (m.hasRqAddUser()) {
						rq_add_user(m);
					} else if (m.hasRqEditUser()) {
						rq_edit_user(m);
					} else if (m.hasRqChangeServerState()) {
						rq_change_server_state(m);
					} else if (m.hasRqChangeClientState()) {
						rq_change_client_state(m);
					} else if (m.hasRqFileHandle()) {
						rq_file_handle(m);
					} else if (m.hasRsFileHandle()) {
						rs_file_handle(m);
					} else if (m.hasRqDelete()) {
						rq_delete(m);
					} else if (m.hasRqKeyUpdate()) {
						rq_key_update(m);
					} else if (m.hasMiTriggerProfileDelta()) {
						mi_trigger_profile_delta(m);
					} else if (m.hasRqCreateAuthMethod()) {
						rq_create_auth_method(m);
					} else if (m.hasRqRemoveAuthMethod()) {
						rq_remove_auth_method(m);
					} else if (m.hasRqLogs()) {
						rq_logs(m);
					} else {
						receptor.cq.put(m.getId(), m);
					}

					ReferenceCountUtil.release(m);
				}
			}

		});
		nbt.start();
	}

	private void ev_kevent(Message m) {
		try {
			ServerStore.Profiles.getClient(receptor.getCvid()).getKeylog().addEvent(m.getEvKevent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void ev_profileDelta(EV_ProfileDelta pd) {

		if (pd.hasExtIp()) {
			if (!receptor.getRemoteAddress().equals("127.0.0.1")) {
				if (pd.getExtIp().equals("0.0.0.0")) {
					pd = EV_ProfileDelta.newBuilder().mergeFrom(pd).setExtIp(receptor.getRemoteAddress()).build();
				}
				try {
					HashMap<String, String> location = CUtil.Location.resolve(pd.getExtIp());
					pd = EV_ProfileDelta.newBuilder().mergeFrom(pd).setCountryCode(location.get("countrycode"))
							.setCountry(location.get("countryname")).build();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (pd.getCvid() != receptor.getCvid()) {
			pd = EV_ProfileDelta.newBuilder().mergeFrom(pd).setCvid(receptor.getCvid()).build();
		}

		ServerStore.Profiles.getClient(receptor.getCvid()).amalgamate(pd);

		for (int svid : ServerStore.Connections.getKeySet()) {
			Receptor r = ServerStore.Connections.getConnection(svid);
			// somehow check permissions TODO

			if (r.getInstance() == Instance.VIEWER) {
				r.handle.write(Message.newBuilder().setUrgent(true).setEvProfileDelta(pd).build());

			}
		}

	}

	private void mi_trigger_profile_delta(Message m) {

		for (ClientProfile cp : ServerStore.Profiles.getClientsUnderAuthority(receptor.getCvid())) {
			boolean flag = true;
			for (ProfileTimestamp pt : m.getMiTriggerProfileDelta().getProfileTimestampList()) {
				if (pt.getCvid() == cp.getCvid()) {
					log.debug("Updating client in viewer");
					receptor.handle.write(Message.newBuilder().setUrgent(true)
							.setEvProfileDelta(cp.getUpdates(new Date(pt.getTimestamp()))).build());
					flag = false;
					continue;
				}
			}
			if (flag) {
				log.debug("Sending new client to viewer");
				receptor.handle.write(
						Message.newBuilder().setUrgent(true).setEvProfileDelta(cp.getUpdates(new Date(0))).build());
			}

		}

	}

	private void mi_challenge_result(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		if (m.getMiChallengeresult().getResult()) {
			aux_acceptClient();
			ev_profileDelta(m.getMiChallengeresult().getPd());
		} else {
			log.debug("Authentication failed");
			receptor.setState(ConnectionState.CONNECTED);
		}

	}

	private void mi_auth_request(Message m) {
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
			final AuthenticationGroup group = ServerStore.Authentication.getGroup(auth.getGroupName());
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
						boolean flag = rs.getResult().equals(Crypto.hashSign(magic, group.getGroupKey()));
						try {
							group.destroy();
						} catch (DestroyFailedException e) {
						}

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
			aux_acceptClient();
			ev_profileDelta(auth.getPd());
			break;
		default:
			break;

		}

	}

	private void mi_stream_start(Message m) {

		Param p = m.getMiStreamStart().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(new SInfoSlave(p));
		}
		if (p.hasSubscriberParam()) {
			StreamStore.addStream(new SubscriberSlave(p));
		}

	}

	private void mi_stream_stop(Message m) {
		StreamStore.removeStream(m.getMiStreamStop().getStreamID());
	}

	private void rq_key_update(Message m) {
		// TODO check permissions

		RQ_KeyUpdate rq = m.getRqKeyUpdate();
		Date target = new Date(rq.getStartDate());

		ClientProfile cp = ServerStore.Profiles.getClient(rq.getCid());
		if (cp != null) {
			for (EV_KEvent k : cp.getKeylog().getEventsAfter(target)) {
				receptor.handle.write(Message.newBuilder().setUrgent(true).setSid(rq.getCid()).setEvKevent(k).build());
			}
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(true)).build());
		} else {
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
		}

	}

	private void rq_group_challenge(Message m) {
		if (receptor.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		RQ_GroupChallenge rq = m.getRqGroupChallenge();
		AuthenticationGroup group = ServerStore.Authentication.getGroup(rq.getGroupName());

		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder()
				.setResult(Crypto.signGroupChallenge(rq.getMagic(), group.getPrivateKey())).build();
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void rq_login(Message m) {
		receptor.setInstance(Instance.VIEWER);

		String user = m.getRqLogin().getUsername();
		ViewerProfile vp = ServerStore.Profiles.getViewer(user);
		EV_ServerProfileDelta.Builder sid = EV_ServerProfileDelta.newBuilder();
		EV_ViewerProfileDelta.Builder vid = EV_ViewerProfileDelta.newBuilder();

		RS_CloudUser cloud = null;
		if (ServerState.isCloudMode() && vp == null) {
			cloud = Services.getCloudUser(user);
			if (vp == null) {
				// create ViewerProfile
				vp = new ViewerProfile(IDGen.getCvid());
				vp.setUser(user);
				ServerStore.Profiles.addViewer(vp);
			}

		}

		boolean pass = false;

		try {
			if (!ServerState.isExampleMode()) {
				pass = false;

				if (vp != null) {
					ServerCommands.setCvid(receptor, vp.getCvid());
				} else {
					log.error("No profile found for user: {}", user);
					pass = false;
					return;
				}

				RQ_LoginChallenge.Builder lcrq = RQ_LoginChallenge.newBuilder().setCloud(cloud != null);
				if (lcrq.getCloud()) {
					lcrq.setSalt(cloud.getSalt());
				} else if (ServerStore.Databases.system.userExists(user)) {
					lcrq.setSalt(ServerStore.Databases.system.getSalt(user));
				} else {
					pass = false;
					return;
				}

				receptor.handle.write(Message.newBuilder().setId(m.getId()).setRqLoginChallenge(lcrq).build());
				Message lcrs = null;
				try {
					lcrs = receptor.cq.take(m.getId(), 5, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					log.error("No response to login challenge");
					pass = false;
					return;
				}
				if (cloud == null) {
					pass = ServerStore.Databases.system.validLogin(user, lcrs.getRsLoginChallenge().getResult());
				} else {
					log.debug("Got cloud hash: " + cloud.getPassword());
					pass = lcrs.getRsLoginChallenge().getResult().equals(cloud.getPassword());
				}

			} else {
				pass = true;
				receptor.setCvid(IDGen.getCvid());
				user = "user_" + Math.abs(CUtil.Misc.rand());
			}

			if (pass) {
				log.debug("Accepting Login");
				receptor.setState(ConnectionState.AUTHENTICATED);

				ServerStore.Connections.add(receptor);

				vp.setIp(receptor.getRemoteAddress());
				vid.setUser(vp.getUser());
				vid.setLoginIp(vp.getIp());
				vid.setLoginTime(vp.getLoginTime().getTime());

				if (vp.getLastLoginIp() != null) {
					vid.setLastLoginIp(vp.getLastLoginIp());
				}
				if (vp.getLastLoginTime() != null) {
					vid.setLastLoginTime(vp.getLastLoginTime().getTime());
				}

				for (Listener l : ServerStore.Listeners.listeners) {
					sid.addListener(l.getConfig());
				}

				try {
					for (Integer i : ServerStore.Profiles.getViewerKeyset()) {
						ViewerProfile vpi = ServerStore.Profiles.getViewer(i);
						EV_ViewerProfileDelta.Builder b = EV_ViewerProfileDelta.newBuilder().setUser(vpi.getUser())
								.setLoginIp(vp.getPermissions().getFlag(Perm.Super) ? vpi.getIp() : "<hidden>")
								.setLoginTime(
										vp.getPermissions().getFlag(Perm.Super) ? vpi.getLoginTime().getTime() : 0);
						for (int j : vpi.getPermissions().extract()) {
							b.addViewerPermissions(j);
						}
						sid.addViewerUser(b);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sid.setServerStatus(ServerStore.Listeners.isRunning());
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

	private void rq_generate(Message m) {

		byte[] res = null;
		Generator g = new Generator();
		try {
			if (m.getRqGenerate().hasSendToCid()) {
				g.generate(m.getRqGenerate().getInternalConfig(), m.getRqGenerate().getSendToCid());
			} else {
				g.generate(m.getRqGenerate().getInternalConfig());
			}

			res = g.getResult();

			RS_Generate.Builder rs = RS_Generate.newBuilder().setInstaller(ByteString.copyFrom(res))
					.setReport(g.getReport());

			if (m.getRqGenerate().hasSendToCid()) {
				ServerStore.Connections.getConnection(m.getRqGenerate().getSendToCid()).handle
						.write(Message.newBuilder().setRsGenerate(rs).build());
				receptor.handle.write(Message.newBuilder().setId(m.getId())
						.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
			} else {
				receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsGenerate(rs).build());
			}
		} catch (Exception e) {
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
		}

	}

	private void rq_file_listing(Message m) {
		ViewerProfile vp = ServerStore.Profiles.getViewer(receptor.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", receptor.getCvid());
			return;
		}

		RQ_FileListing rq = m.getRqFileListing();
		LocalFilesystem lf = ServerStore.LocalFilesystems.get(rq.getFmid());
		if (rq.hasUp() && rq.getUp()) {
			lf.up();
		} else if (rq.hasDown()) {
			lf.down(rq.getDown());
		}

		try {
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list())).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	// TODO router?
	private void rs_file_listing(Message m) {
		Receptor r = ServerStore.Connections.getConnection(m.getSid());
		r.handle.write(m);
	}

	private void rs_file_handle(Message m) {
		log.debug("Got rs_file_handle for VID: " + m.getSid());
		Receptor r = ServerStore.Connections.getConnection(m.getSid());
		r.handle.write(m);

	}

	private void rq_file_handle(Message m) {
		ViewerProfile vp = ServerStore.Profiles.getViewer(receptor.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", receptor.getCvid());
			return;
		}
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsFileHandle(
				RS_FileHandle.newBuilder().setFmid(ServerStore.LocalFilesystems.add(new LocalFilesystem(true, true))))
				.build());

	}

	private void rq_delete(Message m) {
		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsDelete(RS_Delete.newBuilder().setOutcome(
						LocalFilesystem.delete(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite())))
				.build());
	}

	private void rq_advanced_file_info(Message m) {
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(LocalFilesystem.getInfo(m.getRqAdvancedFileInfo().getFile())).build());
	}

	private void rq_add_listener(Message m) {
		// TODO check permissions
		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsAddListener(RS_AddListener.newBuilder().setResult(true)).build());
		ServerStore.Listeners.listeners.add(new Listener(m.getRqAddListener().getConfig()));
		Message update = Message.newBuilder().setUrgent(true).setEvServerProfileDelta(
				EV_ServerProfileDelta.newBuilder().addListener(m.getRqAddListener().getConfig())).build();
		ServerStore.Connections.sendToAll(Instance.VIEWER, update);

	}

	private void rq_remove_listener(Message m) {

	}

	private void rq_add_user(Message m) {
		// TODO check permissions
		receptor.handle.write(
				Message.newBuilder().setId(m.getId()).setRsAddUser(RS_AddUser.newBuilder().setResult(true)).build());

		ServerStore.Databases.system.addLocalUser(m.getRqAddUser().getUser(), m.getRqAddUser().getPassword(),
				new ViewerPermissions(m.getRqAddUser().getPermissionsList()));

		Message update = Message.newBuilder().setUrgent(true)
				.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder()
						.addViewerUser(EV_ViewerProfileDelta.newBuilder().setUser(m.getRqAddUser().getUser())
								.addAllViewerPermissions(m.getRqAddUser().getPermissionsList())))
				.build();
		ServerStore.Connections.sendToAll(Instance.VIEWER, update);

	}

	private void rq_edit_user(Message m) {
		// TODO check permissions
		receptor.handle.write(
				Message.newBuilder().setId(m.getId()).setRsEditUser(RS_EditUser.newBuilder().setResult(true)).build());

		RQ_AddUser rqad = m.getRqEditUser().getUser();

		ViewerProfile vp = null;

		try {
			vp = ServerStore.Profiles.getViewer(rqad.getUser());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EV_ViewerProfileDelta.Builder b = EV_ViewerProfileDelta.newBuilder().setUser(rqad.getUser())
				.addAllViewerPermissions(m.getRqAddUser().getPermissionsList());

		if (rqad.getPermissionsCount() != 0) {
			vp.getPermissions().load(rqad.getPermissionsList());
			b.addAllViewerPermissions(rqad.getPermissionsList());
		}

		if (rqad.hasPassword() && ServerStore.Databases.system.validLogin(rqad.getUser(), Crypto.hashCrimsonPassword(
				m.getRqEditUser().getOldPassword(), ServerStore.Databases.system.getSalt(rqad.getUser())))) {
			ServerStore.Databases.system.changePassword(rqad.getUser(), rqad.getPassword());

		}

		Message update = Message.newBuilder().setUrgent(true)
				.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().addViewerUser(b)).build();

		ServerStore.Connections.sendToAll(Instance.VIEWER, update);

	}

	private void rq_change_server_state(Message m) {
		// TODO check permissions
		String comment = "";
		boolean result = true;

		switch (m.getRqChangeServerState().getNewState()) {
		case FUNCTIONING_OFF:
			ServerStore.Listeners.stop();
			break;
		case FUNCTIONING_ON:
			ServerStore.Listeners.start();
			break;
		case RESTART:
			break;
		case SHUTDOWN:
			break;
		case UNINSTALL:
			break;
		default:
			break;
		}

		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRsChangeServerState(RS_ChangeServerState
				.newBuilder().setOutcome(Outcome.newBuilder().setResult(result).setComment(comment))).build());

		// notify viewers
		ServerStore.Connections.sendToAll(Instance.VIEWER,
				Message.newBuilder()
						.setEvServerProfileDelta(
								EV_ServerProfileDelta.newBuilder().setServerStatus(ServerStore.Listeners.isRunning()))
						.build());
	}

	private void rq_change_client_state(Message m) {

	}

	private void rq_create_auth_method(Message m) {
		Outcome outcome = ServerStore.Authentication.create(m.getRqCreateAuthMethod().getAuthMethod());

		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsCreateAuthMethod(RS_CreateAuthMethod.newBuilder().setOutcome(outcome)).build());

	}

	private void rq_remove_auth_method(Message m) {
		ServerStore.Authentication.remove(m.getRqRemoveAuthMethod().getId());
		// TODO check if removed
		receptor.handle.write(
				Message.newBuilder().setRsRemoveAuthMethod(RS_RemoveAuthMethod.newBuilder().setResult(true)).build());
	}

	private void rq_logs(Message m) {
		RS_Logs.Builder rs = RS_Logs.newBuilder();
		if (m.getRqLogs().hasLog()) {
			rs.addLog(LogFile.newBuilder().setName(m.getRqLogs().getLog())
					.setLog(Logsystem.getLog(m.getRqLogs().getLog())));
		} else {
			for (LogType lt : Logsystem.getApplicableLogs()) {
				rs.addLog(LogFile.newBuilder().setName(lt).setLog(Logsystem.getLog(lt)));
			}
		}
		receptor.handle.write(Message.newBuilder().setRsLogs(rs).build());
	}

	private void aux_acceptClient() {
		receptor.setState(ConnectionState.AUTHENTICATED);
		receptor.setInstance(Instance.CLIENT);

		try {
			if (ServerStore.Profiles.getClient(receptor.getCvid()) == null) {
				ServerStore.Profiles.addClient(new ClientProfile(receptor.getCvid()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ServerStore.Connections.add(receptor);
	}

}
