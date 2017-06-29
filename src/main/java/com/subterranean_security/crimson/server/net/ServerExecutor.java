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
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_CreateAuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_RemoveAuthMethod;
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
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.net.exe.AuthExe;
import com.subterranean_security.crimson.server.net.exe.CvidExe;
import com.subterranean_security.crimson.server.net.exe.DeltaExe;
import com.subterranean_security.crimson.server.net.exe.FileManagerExe;
import com.subterranean_security.crimson.server.net.exe.ListenerExe;
import com.subterranean_security.crimson.server.net.exe.LoginExe;
import com.subterranean_security.crimson.server.net.exe.NetworkExe;
import com.subterranean_security.crimson.server.net.exe.ServerInfoExe;
import com.subterranean_security.crimson.server.net.exe.StreamExe;
import com.subterranean_security.crimson.server.net.exe.UserExe;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
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
							StreamExe.mi_stream_start(m);
							break;
						case MI_STREAM_STOP:
							StreamExe.mi_stream_stop(m);
							break;
						case MI_TRIGGER_PROFILE_DELTA:
							DeltaExe.mi_trigger_profile_delta(connector, m);
							break;
						case RQ_ADD_LISTENER:
							ListenerExe.rq_add_listener(connector, m);
							break;
						case RQ_ADD_USER:
							UserExe.rq_add_user(connector, m);
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
							UserExe.rq_edit_user(connector, m);
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
							ListenerExe.rq_remove_listener(m);
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

		// apprise viewers
		NetworkStore.broadcastTo(Universal.Instance.VIEWER,
				new PDFactory(connector.getCvid()).add(AK_SERVER.ACTIVE_LISTENERS, ListenerStore.getActive())
						.add(AK_SERVER.INACTIVE_LISTENERS, ListenerStore.getInactive()).buildMsg());
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
