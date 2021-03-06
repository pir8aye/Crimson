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
package com.subterranean_security.crimson.core.util;

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.universal.Universal;

public final class LogUtil {
	private LogUtil() {
	}

	/**
	 * Set system properties for logback and trigger the configuration
	 */
	public static void configure() {
		System.setProperty("logging.directory", Environment.log.getAbsolutePath());
		System.setProperty("logging.level", DEV_MODE ? "debug" : "error");
		System.setProperty("logging.instance", Universal.instance.toString());

		// trigger
		LoggerFactory.getLogger(LogUtil.class);
	}
}
