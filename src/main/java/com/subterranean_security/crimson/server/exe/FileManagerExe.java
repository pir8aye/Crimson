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
package com.subterranean_security.crimson.server.exe;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.sv.store.ProfileStore;

/**
 * @author cilki
 * @since 4.0.0
 */
public final class FileManagerExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(FileManagerExe.class);

	public FileManagerExe(Connector connector) {
		super(connector);
	}

	public void rq_file_listing(Message m) {
		ViewerProfile vp = ProfileStore.getViewer(connector.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", connector.getCvid());
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
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list())).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void rs_file_listing(Message m) {

	}

	public void rs_file_handle(Message m) {

	}

	public void rq_file_handle(Message m) {
		ViewerProfile vp = ProfileStore.getViewer(connector.getCvid());

		if (!vp.getPermissions().getFlag(Perm.server.fs.read)) {
			log.warn("Denied unauthorized file access to server from viewer: {}", connector.getCvid());
			return;
		}
		connector.write(Message.newBuilder().setId(m.getId())
				.setRsFileHandle(RS_FileHandle.newBuilder().setFmid(FileManagerStore.add(new LocalFS(true, true))))
				.build());

	}

	public void rq_delete(Message m) {

		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(Perm.server.fs.write)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions")));
			return;
		}

		connector.write(Message.newBuilder().setId(m.getId())
				.setRsOutcome(FileUtil.deleteAll(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite()))
				.build());
	}

	public void rq_advanced_file_info(Message m) {
		connector.write(Message.newBuilder().setId(m.getId()).setTo(m.getFrom()).setFrom(m.getTo())
				.setRsAdvancedFileInfo(FileUtil.getInfo(m.getRqAdvancedFileInfo().getFile())).build());
	}

}
