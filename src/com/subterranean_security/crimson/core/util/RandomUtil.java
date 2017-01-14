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

import java.util.Random;

public final class RandomUtil {
	private RandomUtil() {
	}

	private static Random insecureRandom = new Random();

	public static int rand(int lower, int upper) {
		return insecureRandom.nextInt(upper - lower + 1) + lower;
	}

	public static long rand(long lower, long upper) {
		return nextLong(upper - lower + 1) + lower;
	}

	public static long nextLong(long n) {
		// error checking and 2^x checking removed for simplicity.
		long bits, val;
		do {
			bits = (insecureRandom.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	/**
	 * Generates a random ASCII string of given length
	 *
	 * @param characters
	 *            length of string
	 * @return random string
	 */
	public static String randString(int characters) {
		StringBuffer filename = new StringBuffer();
		for (int i = 0; i < characters; i++) {
			// append a random character
			char c = (char) (insecureRandom.nextInt(25) + 97);
			filename.append(c);
		}

		return filename.toString();
	}

	public static int nextInt() {
		return insecureRandom.nextInt();
	}

	public static void clearChar(char[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) insecureRandom.nextInt();
		}
	}

	public static void clearByte(byte[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (byte) insecureRandom.nextInt();
		}
	}
}
