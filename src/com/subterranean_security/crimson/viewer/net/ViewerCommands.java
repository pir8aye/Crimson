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
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RQ_Generate;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.Listener.RQ_AddListener;
import com.subterranean_security.crimson.core.proto.Login.RQ_Login;
import com.subterranean_security.crimson.core.proto.Login.RS_LoginChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.State.RQ_ChangeServerState;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerCommands {
	;
	private static final Logger log = LoggerFactory.getLogger(ViewerCommands.class);

	public static boolean login(String user, char[] pass) {
		int id = IDGen.get();

		ViewerRouter.route(Message.newBuilder().setId(id)
				.setRqLogin(RQ_Login.newBuilder().setSvid(Common.cvid).setUsername(user)).build());

		try {
			Message lcrq = ViewerRouter.getReponse(0, id, 5);
			if (lcrq.hasRqLoginChallenge()) {

				String result = Crypto.hashPass(pass, lcrq.getRqLoginChallenge().getSalt());
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
					ViewerStore.Profiles.server.amalgamate(lrs.getRsLogin().getInitialInfo());
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

	public static boolean changeServerState(StringBuffer error, StateType st) {
		log.debug("Changing server state: {}", st.toString());
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqChangeServerState(RQ_ChangeServerState.newBuilder().setNewState(st)), 3);
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsChangeServerState().getResult()) {
				if (m.getRsChangeServerState().hasComment()) {
					error.append(m.getRsChangeServerState().getComment());
				}
			} else {
				return true;
			}
		} catch (InterruptedException e) {
			error.append("Interrupted");
		}
		log.debug("error: {}", error.toString());
		return false;
	}

	public static boolean changeClientState(StringBuffer error, StateType st) {
		log.debug("Changing client state: {}", st.toString());
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqChangeServerState(RQ_ChangeServerState.newBuilder().setNewState(st)), 3);
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsChangeServerState().getResult()) {
				if (m.getRsChangeServerState().hasComment()) {
					error.append(m.getRsChangeServerState().getComment());
				}
			} else {
				return true;
			}
		} catch (InterruptedException e) {
			error.append("Interrupted");
		}
		log.debug("error: {}", error.toString());
		return false;
	}

	public static boolean addListener(StringBuffer error, ListenerConfig lf) {
		try {
			Message m = ViewerRouter
					.routeAndWait(Message.newBuilder().setRqAddListener(RQ_AddListener.newBuilder().setConfig(lf)), 3);
			if (!m.getRsAddListener().getResult()) {
				if (m.getRsAddListener().hasComment()) {
					error.append(m.getRsAddListener().getComment());
				}
				return false;
			}
		} catch (InterruptedException e) {
			error.append("Interrupted");
			return false;
		}

		return true;
	}

	public static void generate(ClientConfig config, String output, Date creation) {
		int id = IDGen.get();
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
					MainFrame.main.np.addNote("Info: Generation complete! Click for report.", r);
					CUtil.Files.writeFile(rs.getRsGenerate().getInstaller().toByteArray(), new File(output));
				} else {
					MainFrame.main.np.addNote("Error: Generation failed! Click for report.", r);
					log.error("Could not generate an installer");
				}

			} else {
				MainFrame.main.np.addNote("Error: Generation Timed Out!");
				log.error("Could not generate an installer. Check the network.");
			}
		} catch (InterruptedException e) {
			log.debug("Generation interrupted");
		} catch (IOException e) {
			log.error("Failed to write the installer");
			e.printStackTrace();
		}

	}

	public static enum FM {
		;
		public static ArrayList<FileListlet> down(String s, boolean mtime, boolean size) {
			int id = IDGen.get();
			ViewerRouter.route(Message.newBuilder().setId(id).build());
			// TODO finish message sequence
			return null;
		}
	}

}
