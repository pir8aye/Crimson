package com.subterranean_security.crimson.sc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Log.LogType;
import com.subterranean_security.crimson.core.util.CUtil;

public enum Logsystem {
	;

	public static ArrayList<LogType> getApplicableLogs() {
		ArrayList<LogType> logs = new ArrayList<LogType>();
		logs.add(LogType.CRIMSON);
		// TODO platform logs
		return logs;
	}

	public static String getLog(LogType log) {
		String location = "";
		switch (log) {
		case AUTH:
			break;
		case BOOT:
			break;
		case CRIMSON:
			location = Common.Directories.varLog.getAbsolutePath() + "/" + Common.instance.toString().toLowerCase()
					+ ".log";
			break;
		default:
			break;

		}
		System.out.println("Reading log at location: " + location);
		try {
			return CUtil.Files.readFileString(new File(location));
		} catch (IOException e) {
			return null;
		}
	}

}