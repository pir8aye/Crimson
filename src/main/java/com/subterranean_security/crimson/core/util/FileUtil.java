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
package com.subterranean_security.crimson.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.SigarException;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_AdvancedFileInfo;

/**
 * Utilities for manipulating files on the local system.
 * 
 * @author cilki
 * @since 3.0.0
 */
public final class FileUtil {

	private FileUtil() {
	}

	/**
	 * Copies the source file or directory to the destination file or directory.
	 * This method should only be used for small copy jobs.
	 *
	 * @param source
	 *            The source file or directory
	 * @param dest
	 *            The destination file or directory
	 * @throws IOException
	 */
	public static void copy(File source, File dest) throws IOException {
		if (source == null)
			throw new IllegalArgumentException();
		if (!source.exists())
			throw new FileNotFoundException();
		if (dest == null)
			throw new IllegalArgumentException();

		recursiveCopy(source, dest);
	}

	private static void recursiveCopy(File source, File dest) throws IOException {
		if (source.isFile()) {
			if (dest.isFile())
				Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			else
				Files.copy(source.toPath(), Paths.get(dest.getAbsolutePath(), source.getName()),
						StandardCopyOption.COPY_ATTRIBUTES);
		} else {
			if (!dest.exists())
				dest.mkdir();
			else if (!dest.isDirectory()) {
				throw new IllegalArgumentException("Cannot copy a directory to a file");
			}

			for (String child : source.list()) {
				recursiveCopy(new File(source, child), new File(dest, child));
			}
		}
	}

	public static String hash(String filename, String type) {
		MessageDigest complete = null;
		try (InputStream fis = new FileInputStream(filename)) {
			byte[] buffer = new byte[1024];
			complete = MessageDigest.getInstance(type);
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		byte[] b = complete.digest();

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

	/**
	 * Delete a file or directory
	 *
	 * @param f
	 *            the file or directory to delete
	 * @return true on success false on failure
	 */
	public static boolean delete(String f) {
		return delete(new File(f));
	}

	/**
	 * Alias for delete(file, false)
	 *
	 * @param file
	 * @return true on success false on failure
	 */
	public static boolean delete(File file) {
		return delete(file, false);
	}

	/**
	 * Recursively delete a file or directory. This method should only be used for
	 * small directory trees.
	 *
	 * @param file
	 *            The file or directory to delete.
	 * @param overwrite
	 *            If true, attempt to overwrite the file.
	 * 
	 * @return true on success false on failure
	 */
	public static boolean delete(File file, boolean overwrite) {
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						delete(files[i]);
					} else {
						if (overwrite) {
							overwrite(files[i]);
						}
						files[i].delete();
					}
				}
			}
		}
		return (file.delete());
	}

	/**
	 * Alias for deleteAll(targets, false)
	 * 
	 * @param targets
	 * @return
	 */
	public static Outcome deleteAll(Iterable<String> targets) {
		return deleteAll(targets, false);
	}

	/**
	 * Delete multiple files.
	 * 
	 * @param targets
	 *            Files and directories to be deleted.
	 * @param overwrite
	 *            When true, a (futile) attempt is made to overwrite deleted files.
	 * @return Result is true if every files was deleted.
	 */
	public static Outcome deleteAll(Iterable<String> targets, boolean overwrite) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);
		for (String s : targets) {
			if (!delete(new File(s), overwrite)) {
				outcome.setResult(false);
			}
		}
		return outcome.build();
	}

	/**
	 * Attempt to overwrite a file. There is no guarantee that bytes will be written
	 * to their original physical locations, so this method may not be effective.
	 * 
	 * @param f
	 * @return
	 */
	public static boolean overwrite(File f) {
		byte[] zeros = new byte[4096];

		try (RandomAccessFile raf = new RandomAccessFile(f, "w")) {
			for (long i = 0; i < raf.length(); i += zeros.length) {
				raf.write(zeros);
			}
			for (long i = 0; i < raf.length() % zeros.length; i++) {
				raf.writeByte(0);
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Writes a byte array to the given file.
	 * 
	 * @param bytes
	 *            Bytes to write.
	 * @param target
	 *            Where the byte array will be written.
	 * @throws IOException
	 */
	public static void write(byte[] bytes, File target) throws IOException {
		try (FileOutputStream out = new FileOutputStream(target)) {
			out.write(bytes);
		}
	}

	/**
	 * Read a file into memory.
	 * 
	 * @param file
	 *            The target File.
	 * @return A byte[] containing the contents of file.
	 * @throws IOException
	 */
	public static byte[] read(File file) throws IOException {
		return read(Paths.get(file.getAbsolutePath()));
	}

	/**
	 * Read a file into memory.
	 * 
	 * @param path
	 *            The target File.
	 * @return A byte[] containing the contents of file.
	 * @throws IOException
	 */
	public static byte[] read(Path path) throws IOException {
		return Files.readAllBytes(path);
	}

	/**
	 * Read a file as a series of lines.
	 * 
	 * @param file
	 *            The target File.
	 * @return A list of lines in the target File.
	 * @throws IOException
	 */
	public static List<String> readLines(File file) throws IOException {
		ArrayList<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	/**
	 * Read a file as a single String.
	 * 
	 * @param file
	 *            The target File.
	 * @return A String representing the entire contents of the target File.
	 * @throws IOException
	 */
	public static String readString(File file) throws IOException {
		StringBuffer sb = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static boolean isValidInstallPath(String s) {
		File f = new File(s);
		if (f.exists()) {
			f = new File(f.getAbsolutePath() + "/testDirectory");
			if (f.mkdir()) {
				f.delete();
				return true;
			} else {
				return false;
			}
		} else {
			return isValidInstallPath(f.getParent());
		}
	}

	public static RS_AdvancedFileInfo getInfo(String path) {
		if (path == null)
			throw new IllegalArgumentException();

		File f = new File(path);

		RS_AdvancedFileInfo.Builder rs = RS_AdvancedFileInfo.newBuilder();
		rs.setLocalIcon(Base64.getEncoder()
				.encodeToString(SerialUtil.serialize(FileSystemView.getFileSystemView().getSystemIcon(f))));
		rs.setName(f.getName());
		rs.setPath(f.getParent());
		rs.setSize(f.length());
		rs.setMtime(f.lastModified());

		FileInfo fileInfo = new FileInfo();
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

}