package com.subterranean_security.crimson.sc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.universal.Universal;

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
			location = Environment.log.getAbsolutePath() + "/" + Universal.instance.toString().toLowerCase() + ".log";
			break;
		default:
			break;

		}
		System.out.println("Reading log at location: " + location);
		try {
			return FileUtil.readString(new File(location));
		} catch (IOException e) {
			return null;
		}
	}

}