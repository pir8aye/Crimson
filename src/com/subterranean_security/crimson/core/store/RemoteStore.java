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
package com.subterranean_security.crimson.core.store;

import com.subterranean_security.crimson.core.net.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.core.net.stream.remote.RemoteSlave;

public final class RemoteStore {
	private RemoteStore() {
	}

	private static RemoteSlave slave;
	private static RemoteMaster master;

	public static boolean slaveExists() {
		return slave != null;
	}

	public static void setSlave(RemoteSlave rs) {
		slave = rs;
	}

	public static RemoteSlave getSlave() {
		return slave;
	}

}