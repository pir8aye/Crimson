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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RQ_Generate;
import com.subterranean_security.crimson.core.proto.Login.RQ_Login;
import com.subterranean_security.crimson.core.proto.Login.RS_LoginChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
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
		int svid;
		try {
			svid = ViewerStore.Databases.local.getInteger("svid");
		} catch (Exception e1) {
			svid = 0;
		}

		ViewerConnector.connector.handle.write(Message.newBuilder().setId(id)
				.setRqLogin(RQ_Login.newBuilder().setSvid(svid).setUsername(user)).build());

		try {
			Message lcrq = ViewerConnector.connector.cq.take(id, 5, TimeUnit.SECONDS);
			if (lcrq.hasRqLoginChallenge()) {
				log.debug("Received login challenge: {}", lcrq.getRqLoginChallenge().getSalt());

				String result = Crypto.hashPass(pass, lcrq.getRqLoginChallenge().getSalt());
				log.debug("Sending hash: " + result);
				ViewerConnector.connector.handle.write(Message.newBuilder().setId(id)
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
			Message lrs = ViewerConnector.connector.cq.take(id, 5, TimeUnit.SECONDS);
			if (lrs.hasRsLogin()) {
				log.debug("Received login response: " + lrs.getRsLogin().getResponse());
				if (lrs.getRsLogin().getResponse()) {
					ViewerStore.ServerInfo.integrate(lrs.getRsLogin().getInitialInfo());
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

	public static void changeServerState(boolean state) {
	}

	public static boolean getServerState() {
		return true;
	}

	public static void generate(ClientConfig config, String output, Date creation) {
		int id = IDGen.get();
		RQ_Generate.Builder rq = RQ_Generate.newBuilder().setInternalConfig(config);

		ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).setRqGenerate(rq).build());

		try {
			Message rs = ViewerConnector.connector.cq.take(id, 20, TimeUnit.SECONDS);
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
			ViewerConnector.connector.handle.write(Message.newBuilder().setId(id).build());
			// TODO finish message sequence
			return null;
		}
	}

}
