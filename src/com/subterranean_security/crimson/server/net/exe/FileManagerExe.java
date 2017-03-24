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

import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.core.proto.FileManager.RS_Delete;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.server.net.Receptor;
import com.subterranean_security.crimson.server.store.ProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

public final class FileManagerExe {
	private static final Logger log = LoggerFactory.getLogger(FileManagerExe.class);

	private FileManagerExe() {
	}

	public static void rq_file_listing(Receptor receptor, Message m) {
		ViewerProfile vp = ProfileStore.getViewer(receptor.getCvid());

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
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list())).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public static void rs_file_listing(Receptor receptor, Message m) {

	}

	public static void rs_file_handle(Receptor receptor, Message m) {

	}

	public static void rq_file_handle(Receptor receptor, Message m) {
		ViewerProfile vp = ProfileStore.getViewer(receptor.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", receptor.getCvid());
			return;
		}
		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsFileHandle(RS_FileHandle.newBuilder().setFmid(FileManagerStore.add(new LocalFS(true, true))))
				.build());

	}

	public static void rq_delete(Receptor receptor, Message m) {

		// check permissions
		if (!ProfileStore.getViewer(receptor.getCvid()).getPermissions().getFlag(Perm.server.fs.write)) {
			receptor.handle.write(Message.newBuilder().setId(m.getId())
					.setRsDelete(RS_Delete.newBuilder()
							.setOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions")))
					.build());
			return;
		}

		receptor.handle.write(Message.newBuilder().setId(m.getId())
				.setRsDelete(RS_Delete.newBuilder()
						.setOutcome(LocalFS.delete(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite())))
				.build());
	}

	public static void rq_advanced_file_info(Receptor receptor, Message m) {
		receptor.handle.write(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(LocalFS.getInfo(m.getRqAdvancedFileInfo().getFile())).build());
	}

}
