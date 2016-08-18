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
package com.subterranean_security.crimson.sv.permissions;

/**
 * 
 * Client and server permission constants.
 * 
 * @author Tyler Cook
 *
 */
public final class Perm {

	private Perm() {
	}

	public static final int Super = 0;

	public static final class server {

		public static final class generator {
			public static final int generate = 1;
		}

		public static final class fs {
			public static final int read = 2;
			public static final int write = 3;
		}

		public static final class power {
			public static final int modify = 4;
		}

		public static final class settings {
			public static final int modify = 5;
		}

		public static final class network {
			public static final int create_listener = 6;
		}

		public static final class users {
			public static final int view = 15;
			public static final int create = 7;
			public static final int delete = 8;

		}
	}

	public static final class client {

		public static final int visibility = 15;

		public static final class power {
			public static final int modify = 9;
		}

		public static final class fs {
			public static final int read = 10;
			public static final int write = 11;
		}

		public static final class settings {
			public static final int modify = 12;
		}

		public static final class keylogger {
			public static final int modify_settings = 13;
			public static final int read_logs = 14;
		}
	}
}
