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
package com.subterranean_security.crimson.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public enum Logger {
	;
	private static ArrayList<String> sessionLog = new ArrayList<String>();
	private static PrintWriter log;
	private static boolean logging = true;
	private static boolean logFile = false;

	public static void info(String s) {
		log("[I][" + new Date().toString() + "] " + s);
	}

	public static void error(String s) {
		log("[E][" + new Date().toString() + "] " + s);
	}

	public static void rerror(String s) {
		// report this unusual error
		// TODO
		error(s);
	}

	public static void debug(String s) {
		if (Common.debug) {
			log("[D][" + new Date().toString() + "] " + s);
		}
	}

	public static void debugZip(String s) {

	}

	public static ArrayList<String> getSessionLog() {
		return sessionLog;
	}

	public static ArrayList<String> getFullLog() {
		return null;
	}

	private static void log(String s) {
		if (!logging) {
			return;
		}
		sessionLog.add(s);
		System.out.println(s);

		if (logFile) {
			// write to logfile
			if (log == null) {
				try {
					log = new PrintWriter(new FileOutputStream(Common.log, true));
				} catch (FileNotFoundException e) {
					return;
				}
			}
			log.append(s + "\n");
			log.flush();
		}

	}

	public static void close() {
		logging = false;
		try {
			log.flush();
			log.close();
		} catch (Exception e) {

		}
	}

	public static void ferror(String string) {
		log("[FATAL] " + string);
		System.exit(0);

	}

	public static void setLogging(boolean l) {
		logging = l;
	}
}
