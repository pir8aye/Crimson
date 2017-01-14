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

package com.subterranean_security.crimson.core.platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.ObjectTransfer;
import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.IDGen;

/**
 * This class provides convenient access to the local filesystem.
 * 
 * @author Tyler Cook
 *
 */
public class LocalFS {

	private static final Logger log = LoggerFactory.getLogger(LocalFS.class);

	private Path ref;

	private int fmid;

	public int getFmid() {
		return fmid;
	}

	private boolean mtime;
	private boolean size;

	public LocalFS(boolean size, boolean mtime) {
		this(System.getProperty("user.home"));
		this.size = size;
		this.mtime = mtime;
	}

	public LocalFS(String start) {
		ref = Paths.get(start);
		fmid = IDGen.fm();
		log.debug("Initialized local filesystem handle (FMID: {}, PATH: {})", fmid, pwd());
	}

	public String pwd() {
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

	public void setPath(String path) {
		Path potential = Paths.get(path);
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
					if (builder.getDir()) {

						try {
							builder.setSize(entry.toFile().list().length);
						} catch (NullPointerException e) {

						}
					} else {
						builder.setSize(Files.size(entry));
					}

				}
				list.add(builder.build());
			}
			return list;
		}
	}

	private static FileInfo fileInfo = new FileInfo();

	public static RS_AdvancedFileInfo getInfo(String path) {
		File f = new File(path);

		RS_AdvancedFileInfo.Builder rs = RS_AdvancedFileInfo.newBuilder();
		rs.setLocalIcon(new String(
				B64Util.encode(ObjectTransfer.Default.serialize(FileSystemView.getFileSystemView().getSystemIcon(f)))));
		rs.setName(f.getName());
		rs.setPath(f.getParent());
		rs.setSize(f.length());
		rs.setMtime(f.lastModified());
		try {
			fileInfo.gather(SigarStore.getSigar(), f.getAbsolutePath());
			rs.setAtime(fileInfo.getAtime());
			rs.setCtime(fileInfo.getCtime());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rs.build();
	}

	public static Outcome delete(Iterable<String> targets, boolean overwrite) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);
		for (String s : targets) {
			if (!FileUtil.delete(new File(s), overwrite)) {
				outcome.setResult(false);
			}
		}

		return outcome.build();
	}

}
