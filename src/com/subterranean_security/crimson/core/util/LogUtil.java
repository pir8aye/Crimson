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

import java.io.File;
import java.util.logging.LogManager;

import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.nucleus.JarUtil;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.handler.logging.LogLevel;

public final class LogUtil {
	private LogUtil() {
	}

	private static final boolean netlevel = new File("/netdebug.txt").exists();

	public static void configure() {
		File config = new File(Common.Directories.varLog.getAbsolutePath() + "/logback-"
				+ Common.instance.toString().toLowerCase() + ".xml");
		if (!config.exists()) {

			JarUtil.extract(LogUtil.class.getClassLoader(),
					"com/subterranean_security/crimson/core/res/xml/logback.xml", config.getAbsolutePath());
			FileUtil.substitute(config, "%LEVEL%", Common.isDebugMode() ? LogLevel.DEBUG.toString().toLowerCase()
					: LogLevel.INFO.toString().toLowerCase());
			FileUtil.substitute(config, "%LOGDIR%", config.getParent().replaceAll("\\\\", "/"));
			FileUtil.substitute(config, "%INSTANCE%", Common.instance.toString().toLowerCase());
			FileUtil.substitute(config, "%NETLEVEL%",
					netlevel ? LogLevel.DEBUG.toString().toLowerCase() : LogLevel.ERROR.toString().toLowerCase());

		}

		LogManager.getLogManager().reset();
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);

		context.reset();
		try {
			configurator.doConfigure(config.getAbsolutePath());
		} catch (JoranException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}