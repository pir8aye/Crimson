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
package com.subterranean_security.crimson.viewer;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.util.FileLocking;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerState {
	;
	// we decided not to expend any effort hiding this
	public static boolean trialMode;

	private static boolean online;

	public static boolean isOnline() {
		return online;
	}

	public static void goOffline() {
		if (online && !ShutdownHook.shuttingdown) {
			online = false;
			MainFrame.main.np.addNote("disconnection: Connection to server terminated. Offline mode enabled.");
		}
	}

	public static void goOnline() {
		if (!online) {
			online = true;
		}
	}

	/**
	 * Searches for local servers
	 * 
	 * @return true when a server instance is detected that was not started by
	 *         the viewer
	 */
	public static boolean findLocalServerInstance() {
		return FileLocking.lockExists(Common.Instance.SERVER);
	}

}
