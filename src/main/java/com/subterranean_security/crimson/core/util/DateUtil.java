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

	private static final long SECONDS_IN_MINUTE = 60;
	private static final long SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
	private static final long SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;
	private static final long SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;
	private static final long SECONDS_IN_YEAR = SECONDS_IN_WEEK * 52;

	public static boolean isSameDay(Date d1, Date d2) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		return formatter.format(d1).equals(formatter.format(d2));
	}

	public static int uptime() {
		Date now = new Date();
		return (int) (now.getTime() - Universal.start.getTime()) / 1000;
	}

	public static String timeBetween(Date d1, Date d2) {
		return timeBetween(d1.getTime(), d2.getTime());
	}

	/**
	 * Return the time between two dates (in any order) as a nicely formatted
	 * string
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static String timeBetween(long d1, long d2) {
		long seconds = Math.abs(d1 - d2) / 1000;

		int years = (int) (seconds / SECONDS_IN_YEAR);
		seconds -= years * SECONDS_IN_YEAR;
		int weeks = (int) (seconds / SECONDS_IN_WEEK);
		seconds -= weeks * SECONDS_IN_WEEK;
		int days = (int) (seconds / SECONDS_IN_DAY);
		seconds -= days * SECONDS_IN_DAY;
		int hours = (int) (seconds / SECONDS_IN_HOUR);
		seconds -= hours * SECONDS_IN_HOUR;
		int minutes = (int) (seconds / SECONDS_IN_MINUTE);
		seconds -= minutes * SECONDS_IN_MINUTE;

		StringBuffer result = new StringBuffer();
		switch (years) {
		case 0:
			break;
		case 1:
			result.append(years);
			result.append(" year ");
			break;
		default:
			result.append(years);
			result.append(" years ");
			break;
		}
		switch (weeks) {
		case 0:
			break;
		case 1:
			result.append(weeks);
			result.append(" week ");
			break;
		default:
			result.append(weeks);
			result.append(" weeks ");
			break;
		}
		switch (days) {
		case 0:
			break;
		case 1:
			result.append(days);
			result.append(" day ");
			break;
		default:
			result.append(days);
			result.append(" days ");
			break;
		}
		switch (hours) {
		case 0:
			break;
		case 1:
			result.append(hours);
			result.append(" hour ");
			break;
		default:
			result.append(hours);
			result.append(" hours ");
			break;
		}
		switch (minutes) {
		case 0:
			break;
		case 1:
			result.append(minutes);
			result.append(" minute ");
			break;
		default:
			result.append(minutes);
			result.append(" minutes ");
			break;
		}
		return result.substring(0, result.length() - 1);
	}

}
