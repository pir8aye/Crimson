/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.universal;

public final class Flags {

	/**
	 * Implications of DEV_MODE:
	 * <ul>
	 * <li>the log level is set to DEBUG</li>
	 * <li>a test client will be automatically generated on server startup</li>
	 * <li>the EULA will not be shown</li>
	 * </ul>
	 */
	public static final boolean DEV_MODE = true;

	/**
	 * Implications of LOG_NET_RAW:
	 * <ul>
	 * <li>raw, undecoded network I/O will be logged</li>
	 * </ul>
	 */
	public static final boolean LOG_NET_RAW = false;

	/**
	 * Implications of LOG_NET:
	 * <ul>
	 * <li>decoded network I/O will be logged</li>
	 * </ul>
	 */
	public static final boolean LOG_NET = true;

}
