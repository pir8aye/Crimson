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

package com.subterranean_security.crimson.core.fm;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.IDGen;

/**
 * @author subterranean For file system browsing
 *
 */
public class LocalFilesystem {

	private static final Logger log = CUtil.Logging.getLogger(LocalFilesystem.class);

	private Path ref;

	private int fmid;

	private boolean mtime;
	private boolean size;

	public LocalFilesystem(boolean size, boolean mtime) {
		this("..");
		this.size = size;
		this.mtime = mtime;
	}

	public LocalFilesystem(String start) {
		ref = Paths.get(start);
		fmid = IDGen.getFmid();
		log.debug("Initialized local filesystem handle (FMID: {}, PATH: {})", fmid, pwd());
	}

	public String pwd() {
		log.debug("Returning pwd: " + ref.toString());
		return ref.toString();
	}

	public void up() {
		Path potential = ref.getParent();
		if (potential != null) {
			ref = potential;
		}
	}

	public void down(String name) {
		Path potential = Paths.get(ref.toString(), name);
		if (Files.isDirectory(potential) && Files.exists(potential)) {
			ref = potential;
		}

	}

	public ArrayList<FileListlet> list() throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(ref)) {
			ArrayList<FileListlet> list = new ArrayList<FileListlet>();
			for (Path entry : stream) {
				FileListlet.Builder builder = FileListlet.newBuilder();
				builder.setName(entry.getFileName().toString());
				builder.setDir(Files.isDirectory(entry));
				if (mtime) {
					builder.setMtime(Files.getLastModifiedTime(entry).toMillis());
				}
				if (size) {
					builder.setSize(Files.size(entry));
				}
				list.add(builder.build());
			}
			return list;
		}
	}

	public int getFmid() {
		return fmid;
	}

}
