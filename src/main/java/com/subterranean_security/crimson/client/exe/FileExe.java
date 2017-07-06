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
package com.subterranean_security.crimson.client.exe;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_Delete;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

public class FileExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(FileExe.class);

	public FileExe(Connector connector) {
		super(connector);
	}

	public void file_listing_rq(Message m) {

		RQ_FileListing rq = m.getRqFileListing();
		log.debug("file_listing_rq. fmid: " + rq.getFmid());
		LocalFS lf = FileManagerStore.get(rq.getFmid());
		if (rq.hasUp() && rq.getUp()) {
			lf.up();
		} else if (rq.hasDown()) {
			if (rq.hasFromRoot() && rq.getFromRoot()) {
				lf.setPath(rq.getDown());
			} else {
				lf.down(rq.getDown());
			}

		}
		try {
			NetworkStore.route(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list()))
					.setSid(m.getRid()).setRid(m.getSid()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rq_file_handle(Message m) {
		log.debug("rq_file_handle");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsFileHandle(RS_FileHandle.newBuilder().setFmid(FileManagerStore.add(new LocalFS(true, true)))));
	}

	public void rq_advanced_file_info(Message m) {
		log.debug("rq_advance_file_info");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(FileUtil.getInfo(m.getRqAdvancedFileInfo().getFile())));
	}

	public void rq_delete(Message m) {
		log.debug("rq_delete");
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsDelete(RS_Delete.newBuilder().setOutcome(
						FileUtil.deleteAll(m.getRqDelete().getTargetList(), m.getRqDelete().getOverwrite()))));
	}

}
