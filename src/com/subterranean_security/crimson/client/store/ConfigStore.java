package com.subterranean_security.crimson.client.store;

import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.util.B64Util;
import com.subterranean_security.crimson.universal.stores.Database;

public final class ConfigStore {
	private ConfigStore() {
	}

	private static ClientConfig ic;

	public static ClientConfig getConfig() {
		return ic;
	}

	public static void loadConfig() {
		try {
			ic = ClientConfig.parseFrom(B64Util.decode(Database.getFacility().getString("ic")));
		} catch (Exception e) {
			System.exit(0);
		}
	}
	
	public static void updateConfig(ClientConfig.Builder config){
		
	}

	public static void saveIC() {
		Database.getFacility().store("ic", new String(B64Util.encode(getConfig().toByteArray())));
	}

}
