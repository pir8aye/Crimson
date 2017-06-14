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
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;

import javax.swing.filechooser.FileSystemView;

import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.SigarException;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;

public final class FileUtil {
	public static FileInfo fileInfo = new FileInfo();

	private FileUtil() {
	}

	/**
	 * Copies sourceFile to destFile
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copy(File sourceFile, File destFile) throws IOException {
		if (sourceFile == null)
			throw new IllegalArgumentException();
		if (destFile == null)
			throw new IllegalArgumentException();

		Files.copy(sourceFile.toPath(), destFile.toPath());
	}

	public static String getHash(String filename, String type) {
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
	 * Recursively delete a file or directory
	 *
	 * @param f
	 *            the file or directory to delete
	 * @return true on success false on failure
	 */
	public static boolean delete(String f) {
		return delete(new File(f));
	}

	/**
	 * Recursively delete a file or directory
	 *
	 * @param f
	 *            the file or directory to delete
	 * @return true on success false on failure
	 */
	public static boolean delete(File f) {
		return delete(f, false);
	}

	/**
	 * Recursively delete a file or directory
	 *
	 * @param f
	 *            the file or directory to delete
	 * @return true on success false on failure
	 */
	public static boolean delete(File f, boolean overwrite) {

		if (f.exists()) {
			File[] files = f.listFiles();
			if (null != files) {
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
		return (f.delete());
	}

	/**
	 * Attempt to overwrite a file. There is no guarantee that bytes will be
	 * written to their original physical locations, so this method may not be
	 * effective.
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
	 * Writes the byte array at the given location
	 * 
	 * @param b
	 * @param target
	 * @throws IOException
	 */
	public static void writeFile(byte[] b, File target) throws IOException {
		try (FileOutputStream fileOuputStream = new FileOutputStream(target)) {
			fileOuputStream.write(b);
		}
	}

	public static byte[] readFile(File f) throws IOException {

		java.nio.file.Path path = Paths.get(f.getAbsolutePath());
		return java.nio.file.Files.readAllBytes(path);

	}

	public static ArrayList<String> readFileLines(File f) throws IOException {
		ArrayList<String> a = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {
				a.add(line);
			}
		}
		return a;
	}

	public static String readFileString(File f) throws IOException {
		StringBuffer sb = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
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

	public static Outcome deleteAll(Iterable<String> targets, boolean overwrite) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);
		for (String s : targets) {
			if (!delete(new File(s), overwrite)) {
				outcome.setResult(false);
			}
		}

		return outcome.build();
	}

}