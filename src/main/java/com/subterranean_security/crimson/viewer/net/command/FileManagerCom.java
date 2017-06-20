/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.net.command;

import java.util.ArrayList;

import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.MI_CloseFileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_AdvancedFileInfo;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_Delete;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

public class FileManagerCom {
	public static int getFileHandle(int cid) {
		try {
			Message m = ConnectionStore.routeAndWait(
					Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid).setRqFileHandle(RQ_FileHandle.newBuilder()),
					2);
			if (m != null) {
				return m.getRsFileHandle().getFmid();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Timeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void closeFileHandle(int cid, int fmid) {

		ConnectionStore.route(
				Message.newBuilder().setRid(cid).setMiCloseFileHandle(MI_CloseFileHandle.newBuilder().setFmid(fmid)));

	}

	public static RS_FileListing fm_down(int cid, int fmid, String name, boolean mtime, boolean size) {
		try {
			Message m = ConnectionStore.routeAndWait(Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setDown(name).setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_FileListing fm_up(int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ConnectionStore.routeAndWait(Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setUp(true).setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_FileListing fm_list(int cid, int fmid, boolean mtime, boolean size) {
		try {
			Message m = ConnectionStore.routeAndWait(Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid)
					.setRqFileListing(RQ_FileListing.newBuilder().setFmid(fmid)), 10);
			return m.getRsFileListing();
		} catch (Exception e) {
			return null;
		}
	}

	public static RS_AdvancedFileInfo fm_file_info(int cid, String path) {
		try {
			Message m = ConnectionStore.routeAndWait(Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid)
					.setRqAdvancedFileInfo(RQ_AdvancedFileInfo.newBuilder().setFile(path)), 10);
			return m.getRsAdvancedFileInfo();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Timeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome fm_delete(int cid, ArrayList<String> targets, boolean overwrite) {
		Outcome.Builder outcome = Outcome.newBuilder();
		try {
			Message m = ConnectionStore.routeAndWait(Message.newBuilder().setRid(cid).setSid(LcvidStore.cvid)
					.setRqDelete(RQ_Delete.newBuilder().addAllTarget(targets).setOverwrite(overwrite)), 10);

			if (m == null) {
				outcome.setResult(false).setComment("No response");
			} else {
				return m.getRsDelete().getOutcome();
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (Timeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}
}
