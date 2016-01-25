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
package com.subterranean_security.crimson.server.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.thavam.util.concurrent.BlockingHashMap;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.msg.MSG.Message;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.sv.Profile;

public class Receptor implements AutoCloseable {

	private Instance instance;

	public ServerHandler handle;
	public ServerExecutor executor = new ServerExecutor(this);

	// Buffers
	public final BlockingQueue<Message> nq = new LinkedBlockingQueue<Message>();
	public final BlockingHashMap<Integer, Message> cq = new BlockingHashMap<Integer, Message>();
	public final BlockingQueue<Message> uq = new LinkedBlockingQueue<Message>();

	// state
	private ConnectionState state = ConnectionState.CONNECTED;

	public void setState(ConnectionState cs) {
		state = cs;
	}

	public ConnectionState getState() {
		return state;
	}

	private int profile;

	public Receptor(ServerHandler handle) {
		this.handle = handle;
	}

	public void close() {

	}

	public Profile getProfile() {
		try {
			return (Profile) ServerStore.Databases.system.get(profile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance i) {
		instance = i;
	}

	public int getClientid() {
		return getProfile().getClientid();
	}

}
