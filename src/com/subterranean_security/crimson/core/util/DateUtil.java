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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.subterranean_security.crimson.universal.Universal;

public final class DateUtil {
	private DateUtil() {
	}

	public static boolean isSameDay(Date d1, Date d2) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		return formatter.format(d1).equals(formatter.format(d2));
	}

	public static int uptime() {
		Date now = new Date();
		return (int) (now.getTime() - Universal.start.getTime()) / 1000;
	}

	public static String datediff(Date d1, Date d2) {
		long seconds = 0;
		if (d1.getTime() > d2.getTime()) {
			seconds = (d1.getTime() - d2.getTime()) / 1000;
		} else if (d1.getTime() < d2.getTime()) {
			seconds = (d2.getTime() - d1.getTime()) / 1000;
		}

		int months = (int) (seconds / 2592000);
		seconds -= months * 2592000;
		int days = (int) (seconds / 86400);
		seconds -= days * 86400;
		int hours = (int) (seconds / 3600);
		seconds -= hours * 3600;
		int minutes = (int) (seconds / 60);
		seconds -= minutes * 60;

		String result = "";
		switch (months) {
		case 0:
			break;
		case 1:
			result += months + " month ";
			break;
		default:
			result += months + " months ";
			break;
		}
		switch (days) {
		case 0:
			break;
		case 1:
			result += days + " day ";
			break;
		default:
			result += days + " days ";
			break;
		}
		switch (hours) {
		case 0:
			break;
		case 1:
			result += hours + " hour ";
			break;
		default:
			result += hours + " hours ";
			break;
		}
		switch (minutes) {
		case 0:
			break;
		case 1:
			result += minutes + " minute ";
			break;
		default:
			result += minutes + " minutes ";
			break;
		}
		return result;
	}

}
