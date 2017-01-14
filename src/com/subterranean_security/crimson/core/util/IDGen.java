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
 * 
 * This class is the central generator for ID numbers.
 * 
 * @author Tyler Cook
 *
 */
public final class IDGen {
	private IDGen() {
	}

	private static int counter = 0;

	public static int msg() {
		if (counter == 16) {
			counter = 0;
		}
		return ++counter;
	}

	public static int cvid() {
		return RandomUtil.nextInt();
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

	public static int authenticationMethod() {
		return Math.abs(RandomUtil.nextInt());
	}

}
