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

/**
 * Unified place for getting different types of ID numbers.
 */
public final class IDGen {
	private IDGen() {
	}

	private static int msg = 0;

	/**
	 * Get an ID for use in a message which requires a response. IDs cycle from
	 * 0 to 16 which saves a few bytes on the wire.
	 * 
	 * @return An ID ranging from 0-16
	 */
	public static int msg() {
		msg++;
		if (msg == 16) {
			msg = 0;
		}
		return msg;
	}

	// TODO incremental
	public static int cvid() {
		return RandomUtil.nextInt();
	}

	/**
	 * Generate a new long-cvid which is just a random string. Collisions are
	 * not checked!
	 * 
	 * @return A 6 character long-cvid
	 */
	public static String lcvid() {
		return RandomUtil.randString(6);
	}

	public static int fm() {
		return RandomUtil.nextInt();
	}

	public static int stream() {
		return RandomUtil.nextInt();
	}

	public static int listener() {
		return Math.abs(RandomUtil.nextInt());
	}

	public static int auth() {
		return Math.abs(RandomUtil.nextInt());
	}

}
