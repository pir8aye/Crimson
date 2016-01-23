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
package com.subterranean_security.crimson.viewer.network;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.msg.FM.FileListlet;
import com.subterranean_security.crimson.core.proto.msg.Gen.ClientConfig;
import com.subterranean_security.crimson.core.proto.msg.Gen.GenReport;
import com.subterranean_security.crimson.core.proto.msg.Gen.Generate_RQ;
import com.subterranean_security.crimson.core.proto.msg.Login.Login_RQ;
import com.subterranean_security.crimson.core.proto.msg.MSG.Message;
import com.subterranean_security.crimson.core.proto.msg.State.STATES;
import com.subterranean_security.crimson.core.proto.msg.State.StateChange_RQ;
import com.subterranean_security.crimson.core.utility.CUtil;
import com.subterranean_security.crimson.core.utility.IDGen;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;
import com.subterranean_security.crimson.viewer.ui.screen.login.LoginDialog;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerCommands {
	;
	private static final Logger log = LoggerFactory.getLogger("com.subterranean_security.crimson.viewer.network.ViewerCommands");

	public static boolean login(String user, char[] pass) {
		int id = IDGen.get();
		Login_RQ.Builder rq = Login_RQ.newBuilder().setUsername(user).setHash(new String(pass));// TODO

		ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).setLoginRq(rq).build());
		try {
			Message rs = ViewerConnector.connector.cq.take(id, 7, TimeUnit.SECONDS);
			if (rs != null) {
				log.debug("Received login response: " + rs.getLoginRs().getResponse());
				if (rs.getLoginRs().getResponse()) {
					LoginDialog.initial = rs.getLoginRs().getInitialInfo();
					return true;
				}

			}
		} catch (InterruptedException e) {
			log.debug("Login interrupted");
		}
		return false;
	}

	public static void changeServerState(boolean state) {
		int id = IDGen.get();
		StateChange_RQ.Builder rq = StateChange_RQ.newBuilder().setChange(state).setType(STATES.SERVER);
		ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).setStateChangeRq(rq).build());
	}

	public static boolean getServerState() {
		return true;
	}

	public static void generate(ClientConfig config, String output, Date creation) {
		int id = IDGen.get();
		Generate_RQ.Builder rq = Generate_RQ.newBuilder().setInternalConfig(config);

		ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).setGenerateRq(rq).build());

		try {
			Message rs = ViewerConnector.connector.cq.take(id, 20, TimeUnit.SECONDS);
			if (rs != null) {
				// success
				final GenReport gr = rs.getGenerateRs().getReport();
				Runnable r = new Runnable() {
					public void run() {
						Report rep = new Report(gr);
						rep.setVisible(true);

					}
				};

				if (gr.getResult()) {
					MainFrame.main.np.addNote("Info: Generation complete! Click for report.", r);
					CUtil.Files.writeFile(rs.getGenerateRs().getInstaller().toByteArray(), new File(output));
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
			ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).build());
			// TODO finish message sequence
			return null;
		}
	}

}
