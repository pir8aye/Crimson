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
 * Permission constants. Lowercase classes are used to make permissions look
 * better.
 */
public final class Perm {
	private Perm() {
	}

	/**
	 * The ultimate permission which is equivalent to granting all permissions
	 */
	public static final short Super = 0;

	public static final class server {

		public static final class generator {
			public static final short generate_jar = 11;
			public static final short generate_exe = 12;
			public static final short generate_sh = 13;
			public static final short generate_qr = 14;
		}

		public static final class fs {
			public static final short read = 21;
			public static final short write = 22;
		}

		public static final class power {
			public static final short modify = 31;
		}

		public static final class settings {
			public static final short modify = 41;
		}

		public static final class network {
			public static final short view = 51;
			public static final short create_listener = 52;
			public static final short create_auth_group = 53;
		}

		public static final class users {
			public static final short view = 61;
			public static final short create = 62;
			public static final short delete = 63;

		}
	}

	public static final class client {

		/**
		 * Permission to see client in list/graph
		 */
		public static final short visibility = 300;

		public static final class power {
			public static final short shutdown = 311;
			public static final short restart = 312;
		}

		public static final class fs {
			public static final short read = 321;
			public static final short write = 322;
		}

		public static final class settings {
			public static final short modify = 331;
		}

		public static final class keylogger {
			public static final short modify_settings = 341;
			public static final short read_logs = 342;
		}
	}
}
