/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.common;

import com.subterranean_security.crimson.core.proto.NotificationPolicyOuterClass.NotificationPolicy;
import com.subterranean_security.crimson.universal.stores.Database;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.components.Console.LineType;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public final class UINotification {
	private UINotification() {
	}

	private static NotificationPolicy policy;

	static {
		try {
			policy = (NotificationPolicy) Database.getFacility().getObject("policy.notification");
		} catch (Exception e) {

			// default policy
			policy = NotificationPolicy.newBuilder().setOnClientDisconnect(false).setOnNewClientConnect(true)
					.setOnOldClientConnect(false).setOnException(true).build();
		}
	}

	public static void addConsoleGood(String s) {
		MainFrame.main.panel.console.addLine(s, LineType.GREEN);
	}

	public static void addConsoleInfo(String s) {
		MainFrame.main.panel.console.addLine(s, LineType.BLUE);
	}

	public static void addConsoleBad(String s) {
		MainFrame.main.panel.console.addLine(s, LineType.ORANGE);
	}

	public static NotificationPolicy getPolicy() {
		return policy;
	}

	public static void setPolicy(NotificationPolicy np) {
		policy = np;
	}

}
