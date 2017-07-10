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
package com.subterranean_security.crimson.client.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.exe.C_AuthExe;
import com.subterranean_security.crimson.client.exe.FileExe;
import com.subterranean_security.crimson.client.exe.MiscExe;
import com.subterranean_security.crimson.client.exe.TorrentExe;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;

public class ClientExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ClientExecutor.class);

	public ClientExecutor() {
		super();
		initUnauth();
	}

	public void initUnauth() {
		setExecutors(new C_AuthExe(connector, this));
	}

	public void initAuth() {
		setExecutors(new FileExe(connector), new MiscExe(connector), new TorrentExe(connector));
	}

}
