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
package com.subterranean_security.crimson.server.net.exe;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_Delete;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

public final class FileManagerExe {
	private static final Logger log = LoggerFactory.getLogger(FileManagerExe.class);

	private FileManagerExe() {
	}

	public static void rq_file_listing(Connector receptor, Message m) {
		ViewerProfile vp = ServerProfileStore.getViewer(receptor.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", receptor.getCvid());
			return;
		}

		RQ_FileListing rq = m.getRqFileListing();
		LocalFS lf = FileManagerStore.get(rq.getFmid());
		if (rq.hasUp() && rq.getUp()) {
			lf.up();
		} else if (rq.hasDown()) {
			lf.down(rq.getDown());
		}

		try {
			receptor.write(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list())).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public static void rs_file_listing(Connector receptor, Message m) {

	}

	public static void rs_file_handle(Connector receptor, Message m) {

	}

	public static void rq_file_handle(Connector receptor, Message m) {
		ViewerProfile vp = ServerProfileStore.getViewer(receptor.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", receptor.getCvid());
			return;
		}
		receptor.write(Message.newBuilder().setId(m.getId())
				.setRsFileHandle(RS_FileHandle.newBuilder().setFmid(FileManagerStore.add(new LocalFS(true, true))))
				.build());

	}

	public static void rq_delete(Connector receptor, Message m) {

		// check permissions
		if (!ServerProfileStore.getViewer(receptor.getCvid()).getPermissions().getFlag(Perm.server.fs.write)) {
			receptor.write(Message.newBuilder().setId(m.getId())
					.setRsDelete(RS_Delete.newBuilder()
							.setOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions")))
					.build());
			return;
		}

		receptor.write(
				Message.newBuilder().setId(m.getId())
						.setRsDelete(RS_Delete.newBuilder().setOutcome(
								FileUtil.deleteAll(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite())))
						.build());
	}

	public static void rq_advanced_file_info(Connector receptor, Message m) {
		receptor.write(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(FileUtil.getInfo(m.getRqAdvancedFileInfo().getFile())).build());
	}

}
