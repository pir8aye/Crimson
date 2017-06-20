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
package com.subterranean_security.crimson.server.net.exe;

import java.util.NoSuchElementException;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.proto.core.net.sequences.Login.RS_ServerInfo;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public final class ServerInfoExe {

	private static RS_ServerInfo.Builder rs_server_info;

	public static void rq_server_info(Connector connector) {
		if (rs_server_info == null)
			refreshServerInfo();

		connector.write(Message.newBuilder().setRsServerInfo(rs_server_info).build());
	}

	public static void refreshServerInfo() {
		rs_server_info = RS_ServerInfo.newBuilder().setVersion(Universal.version);

		try {
			rs_server_info.setBanner(DatabaseStore.getDatabase().getString("banner.text"));
		} catch (NoSuchElementException e) {
			rs_server_info.clearBanner();
		}

		try {
			rs_server_info.setBannerImage(DatabaseStore.getDatabase().getString("banner.image"));
		} catch (NoSuchElementException e) {
			rs_server_info.clearBannerImage();
		}

	}
}
