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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtil {
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
		FileInputStream fis = new FileInputStream(sourceFile);
		copy(fis.getChannel(), destFile);
		fis.close();
	}

	// TODO test this method
	public static void copy(InputStream sourceFile, File destFile) throws IOException {
		copy(((FileInputStream) sourceFile).getChannel(), destFile);
	}

	// TODO directories!!
	private static void copy(FileChannel source, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel destination = null;

		try (FileOutputStream fos = new FileOutputStream(destFile)) {
			destination = fos.getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static String getHash(String filename, String type) {
		MessageDigest complete = null;
		try {
			InputStream fis = new FileInputStream(filename);
			byte[] buffer = new byte[1024];
			complete = MessageDigest.getInstance(type);
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
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

	public static boolean overwrite(File f) {
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "w");
			for (long i = 0; i < raf.length(); i++) {
				raf.writeByte(0);// TODO random
			}
			raf.close();
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

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified
	 * by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts an entry from a zip file specified by the zipFilePath to a
	 * directory specified by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzipFile(String zipFilePath, String targetEntry, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory() && entry.getName().equals(targetEntry)) {
				// if the entry is a file, extracts it
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
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

	public static void substitute(File f, String target, String replacement) {
		try {
			FileReader fr = new FileReader(f);
			String s;
			String totalStr = "";
			try (BufferedReader br = new BufferedReader(fr)) {

				while ((s = br.readLine()) != null) {
					totalStr += s;
				}
				totalStr = totalStr.replaceAll(target, replacement);
				FileWriter fw = new FileWriter(f);
				fw.write(totalStr);
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}