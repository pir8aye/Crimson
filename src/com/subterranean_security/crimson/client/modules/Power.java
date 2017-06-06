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
package com.subterranean_security.crimson.client.modules;

import java.io.File;
import java.net.URISyntaxException;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.misc.HCP;
import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;

public final class Power {

	private Power() {
	}

	public static Outcome shutdown() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			command = "poweroff";
			break;
		case WIN:
			command = "shutdown /p /f";

			break;
		default:
			break;
		}

		try {
			HCP.run(HCP.HCP_BASE, command, 1);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static Outcome restart() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			command = "restart";
			break;
		case WIN:
			command = "shutdown /r /p";
			break;
		default:
			break;
		}

		try {
			HCP.run(HCP.HCP_BASE, command, 1);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static Outcome hibernate() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			return Outcome.newBuilder().setResult(false).setComment("Hibernate is unsupported on this platform")
					.build();
		case WIN:
			command = "shutdown /h /p";
			break;
		default:
			break;
		}

		try {
			HCP.run(HCP.HCP_BASE, command, 1);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static Outcome standby() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
		case WIN:
			return Outcome.newBuilder().setResult(false).setComment("Standby is unsupported on this platform").build();
		default:
			break;
		}

		try {
			HCP.run(HCP.HCP_BASE, command, 1);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static Outcome uninstall() {
		String[] auxilaryCmd = new String[] {};
		switch (Platform.osFamily) {
		case WIN:
			try {
				auxilaryCmd = new String[] { "reg delete HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v "
						+ new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
								.getName()
						+ " /f" };
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		default:
			break;
		}

		try {
			HCP.uninstall(new String[] { Environment.base.getAbsolutePath() }, auxilaryCmd, 5);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static Outcome restartProcess() {
		try {
			HCP.run(HCP.HCP_BASE, Platform.osFamily.getJavaw() + " -jar \""
					+ new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
							.getAbsolutePath()
					+ "\"", 4);
		} catch (Exception e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

}
