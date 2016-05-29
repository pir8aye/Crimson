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
package com.subterranean_security.crimson.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.subterranean_security.crimson.client.net.ClientCommands;
import com.subterranean_security.crimson.core.proto.Generator.NetworkTarget;

public class Installer {

	public static HashMap<String, String> ic;
	public static String jarPath;

	private static boolean debug = new File("/debug.txt").exists();

	public static void main(String[] args) {
		try {
			ic = readInternal();
		} catch (Exception e) {
			System.out.println("Fatal: Could not read internal.txt");
			return;
		}

		try {
			if (!isInstalled()) {
				if (!packagedLibsFound()) {
					try {
						Client.connectionRoutine(getInternalNts());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Cannot download libraries due to invalid internal syntax");
						System.exit(0);
					}
					ClientCommands.downloadLibs();
				}
				if (install()) {
					System.out.println("Install Success");
				} else {
					System.out.println("Install Failed");
				}
				return;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Client.main(args);

	}

	public static boolean packagedLibsFound() {
		return Installer.class.getResourceAsStream("/com/subterranean_security/crimson/client/res/bin/lib.zip") != null;
	}

	private static HashMap<String, String> readInternal() throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		InputStream in = Client.class.getResourceAsStream("/com/subterranean_security/crimson/client/internal.txt");
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = input.readLine()) != null) {
			String[] parts = line.split("<>");
			map.put(parts[0], parts[1]);
		}
		input.close();
		return map;
	}

	private static ArrayList<NetworkTarget> getInternalNts() throws Exception {
		ArrayList<NetworkTarget> nt = new ArrayList<NetworkTarget>();
		for (String s : ic.keySet()) {
			if (s.equals("nt")) {
				String[] parts = ic.get(s).split(":");
				nt.add(NetworkTarget.newBuilder().setServer(parts[0]).setPort(Integer.parseInt(parts[1])).build());
			}
		}

		return nt;
	}

	public static boolean isInstalled() throws URISyntaxException {
		jarPath = Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		String target = ic.get("base_win").replaceAll("\\%USERNAME\\%", System.getProperty("user.name"));// TODO
																											// multiple
																											// platforms
		File t1 = (new File(jarPath)).getParentFile();
		File t2 = new File(target);
		System.out.println("Testing for equality: " + t1.getAbsolutePath() + " and: " + t2.getAbsolutePath());
		return (new File(jarPath)).getParentFile().equals(new File(target));
	}

	public static boolean install() {
		System.out.println("Starting installation");
		String base = ic.get("base_win").replaceAll("\\%USERNAME\\%", System.getProperty("user.name"));// TODO
																										// multiple
																										// platforms
		if (!(new File(base)).mkdirs()) {
			System.out.println("Failed to create install base");
			return false;
		}

		if (!base.endsWith(File.separator)) {
			base += File.separator;
		}

		(new File(base + "var")).mkdirs();
		(new File(base + "tmp")).mkdirs();
		(new File(base + "lib")).mkdirs();

		System.out.println("Extracting lib.zip");

		extract("com/subterranean_security/crimson/client/res/bin/lib.zip", base + "lib/lib.zip");
		try {
			unzip(base + "lib/lib.zip", base + "lib/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		System.out.println("Extracting client database");
		extract("com/subterranean_security/crimson/client/res/bin/client.db", base + "var/client.db");

		System.out.println("Copying client jar");
		File client = new File(base + "/client.jar");
		try {
			copyClientJar(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to copy client jar");
			return false;
		}

		return execute(client);
	}

	private static boolean execute(File f) {
		// TODO platform independence
		try {
			if (!debug) {
				Runtime.getRuntime().exec("javaw -jar " + f.getAbsolutePath());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void extract(String res, String dest) {
		InputStream stream = Client.class.getClassLoader().getResourceAsStream(res);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dest);
			byte[] buf = new byte[2048];
			int r = stream.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = stream.read(buf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void unzip(String zipFilePath, String destDirectory) throws IOException {
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

	private static void copyClientJar(File destFile) throws IOException {
		FileInputStream fis = new FileInputStream(new File(jarPath));
		FileChannel source = fis.getChannel();
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel destination = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destFile);
			destination = fos.getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

	}

}
