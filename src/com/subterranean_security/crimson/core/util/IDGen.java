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

public enum IDGen {
	;
	private static byte counter = 0;

	public static synchronized int get() {
		if (counter == 16) {
			counter = 0;
		} else {
			counter++;
		}
		return counter;
	}

	private static Random random = new Random();

	public static int getCvid() {
		return random.nextInt();
	}

	public static int getListenerID() {
		return random.nextInt();
	}

}
