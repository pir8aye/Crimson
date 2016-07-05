package com.subterranean_security.crimson.core;

import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.core.stream.remote.RemoteSlave;

public final class CoreStore {
	private CoreStore() {

	}

	public static class Remote {
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

}
