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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.subterranean_security.crimson.core.proto.FileManager.FileListlet;
import com.subterranean_security.crimson.core.util.IDGen;

/**
 * This class provides a convenient handle on the local filesystem.
 * 
 * @test: LocalFSTest
 */
public class LocalFS {

	/**
	 * The current directory
	 */
	private Path ref;

	/**
	 * The unique file-manager ID
	 */
	private int id;

	/**
	 * @return The file-manager ID associated with this LocalFS
	 */
	public int getId() {
		return id;
	}

	/**
	 * If modification times should be included in file listings
	 */
	private boolean mtimes;

	/**
	 * If file sizes should be included in file listings
	 */
	private boolean sizes;

	public LocalFS(boolean sizes, boolean mtimes) {
		this(System.getProperty("user.home"));
		this.sizes = sizes;
		this.mtimes = mtimes;
	}

	public LocalFS(String start) {
		if (start == null)
			throw new IllegalArgumentException();

		ref = Paths.get(start);
		id = IDGen.fm();
	}

	/**
	 * @return The absolute path to the present working directory
	 */
	public String pwd() {
		return ref.toString();
	}

	/**
	 * Move the working directory up a single level.
	 * 
	 * @return True if the working directory has been changed, false otherwise
	 */
	public boolean up() {
		Path potential = ref.getParent();
		if (potential != null) {
			ref = potential;
			return true;
		}
		return false;
	}

	/**
	 * Move the working directory down into the specified directory.
	 * 
	 * @param directory
	 *            The desired directory relative to the current working
	 *            directory
	 * @return True if the working directory has been changed, false otherwise
	 */
	public boolean down(String directory) {
		if (directory == null)
			throw new IllegalArgumentException();

		Path potential = Paths.get(ref.toString(), directory);
		if (Files.isDirectory(potential) && Files.exists(potential)) {
			ref = potential;
			return true;
		}
		return false;
	}

	/**
	 * Move the working directory to the specified path
	 * 
	 * @param path
	 *            The absolute path which will become the new working directory
	 * @return True if the working directory has been changed, false otherwise
	 */
	public boolean setPath(String path) {
		if (path == null)
			throw new IllegalArgumentException();

		Path potential = Paths.get(path);
		if (Files.isDirectory(potential) && Files.exists(potential)) {
			ref = potential;
			return true;
		}
		return false;
	}

	/**
	 * @return A listing of the files and directories in the working directory
	 * @throws IOException
	 */
	public List<FileListlet> list() throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(ref)) {
			List<FileListlet> list = new ArrayList<FileListlet>();
			for (Path entry : stream) {
				FileListlet.Builder listlet = FileListlet.newBuilder();
				listlet.setName(entry.getFileName().toString());
				listlet.setDir(Files.isDirectory(entry));

				if (mtimes) {
					listlet.setMtime(Files.getLastModifiedTime(entry).toMillis());
				}
				if (sizes) {
					if (listlet.getDir()) {
						listlet.setSize(entry.toFile().list().length);
					} else {
						listlet.setSize(Files.size(entry));
					}

				}
				list.add(listlet.build());
			}
			return list;
		}
	}
}
