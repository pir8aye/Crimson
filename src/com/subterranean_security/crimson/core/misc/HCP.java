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
package com.subterranean_security.crimson.core.misc;

import java.io.File;
import java.io.IOException;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.TempUtil;

public class HCP {
	private HCP() {
	}

	public static final int HCP_BASE = 0;
	public static final int HCP_ANON = 1;

	public static void uninstall(String[] paths, String[] commands, int sleep) {
		String input = "sleep=" + sleep;
		for (String p : paths) {
			input += " \"delete=" + p + "\"";
		}

		for (String c : commands) {
			input += " \"run=" + c + "\"";
		}

		launchAnon(input);
	}

	public static void update(String installer, String[] deletions, String[] runOnCompletion, int sleep) {
		String input = "sleep=" + sleep;
		input += " \"run=" + Platform.osFamily.getJavaw() + " -jar " + installer + "\"";
		for (String d : deletions) {
			input += " \"delete=" + d + "\"";
		}

		for (String r : runOnCompletion) {
			input += " \"runOnCompletion=" + r + "\"";
		}

		launchAnon(input);
	}

	public static void run(int hcp_location, String command) {
		run(hcp_location, command, 0);
	}

	public static void run(int hcp_location, String command, int sleep) {
		String input = "sleep=" + sleep + " \"run=" + command + "\"";
		switch (hcp_location) {
		case HCP_BASE:
			launch(input);
			break;
		case HCP_ANON:
			launchAnon(input);
			break;
		}
	}

	private static void launchAnon(String input) {
		File tmp = new File(TempUtil.getDir().getAbsolutePath() + "/hcp.jar");
		System.out.println("Installing HCP: " + tmp.getAbsolutePath());
		try {
			FileUtil.copy(new File(Environment.base.getAbsolutePath() + "/lib/java/c01.jar"), tmp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		input = Platform.osFamily.getJavaw() + " -jar -Djava.awt.headless=true \"" + tmp.getAbsolutePath() + "\" "
				+ input;
		System.out.println("HCP INPUT: (" + input + ")");
		try {
			Runtime.getRuntime().exec(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void launch(String input) {
		input = Platform.osFamily.getJavaw() + " -jar -Djava.awt.headless=true \""
				+ new File(Environment.base.getAbsolutePath() + "/lib/java/c01.jar").getAbsolutePath() + "\" " + input;
		System.out.println("HCP INPUT: (" + input + ")");
		try {
			Runtime.getRuntime().exec(input).waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
