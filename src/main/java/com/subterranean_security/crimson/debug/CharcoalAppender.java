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
package com.subterranean_security.crimson.debug;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

//TODO rename
public final class CharcoalAppender {

	public static PipedInputStream in;

	public static void redirectStdout() {
		try {
			in = new PipedInputStream();
			PrintStream out = new PrintStream(new PipedOutputStream(in), true);
			System.setOut(out);
			System.setErr(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void redirectLogger() throws IOException {
		in = new PipedInputStream();

		// Get LoggerContext from SLF4J
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		// Encoder
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		encoder.setPattern("[%-5level][%logger{0}] %msg%n");
		encoder.start();

		// OutputStreamAppender
		OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<>();
		appender.setName("OutputStream Appender");
		appender.setContext(context);
		appender.setEncoder(encoder);
		appender.setOutputStream(new PipedOutputStream(in));
		appender.start();

		Logger log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		log.addAppender(appender);

	}

}
