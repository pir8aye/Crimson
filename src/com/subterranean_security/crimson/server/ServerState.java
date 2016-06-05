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
package com.subterranean_security.crimson.server;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.util.CUtil;

public enum ServerState {
	;

	private static final Logger log = CUtil.Logging.getLogger(ServerState.class);

	private static boolean exampleMode = false;
	private static boolean cloudMode = false;

	public static boolean isExampleMode() {
		return exampleMode;
	}

	public static void setExampleMode(boolean exampleMode) {
		ServerState.exampleMode = exampleMode;
		log.debug("Example Mode set: {}", exampleMode);

	}

	public static boolean isCloudMode() {
		return cloudMode;
	}

	public static void setCloudMode(boolean cloudMode) {
		ServerState.cloudMode = cloudMode;
		log.debug("Cloud Mode set: {}", cloudMode);

	}

}
