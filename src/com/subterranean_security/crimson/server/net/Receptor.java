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

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.thavam.util.concurrent.BlockingHashMap;

import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.CUtil;

public class Receptor implements AutoCloseable {

	private static final Logger log = CUtil.Logging.getLogger(Receptor.class);

	private Instance instance;
	private int cvid;

	public ServerHandler handle;
	public ServerExecutor executor = new ServerExecutor(this);

	// Buffers
	public final BlockingQueue<Message> uq = new LinkedBlockingQueue<Message>();
	public final BlockingQueue<Message> nq = new LinkedBlockingQueue<Message>();
	public final BlockingHashMap<Integer, Message> cq = new BlockingHashMap<Integer, Message>();

	// state
	private ConnectionState state = ConnectionState.CONNECTED;

	public void setState(ConnectionState cs) {
		log.debug("New connection state: {} (CVID: {})", cs.toString(), cvid);
		state = cs;
	}

	public ConnectionState getState() {
		return state;
	}

	public Receptor(ServerHandler handle) {
		this.handle = handle;
	}

	public void close() {

	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance i) {
		instance = i;
	}

	public int getCvid() {
		return cvid;
	}

	public String getRemoteAddress() {
		return ((InetSocketAddress) handle.channel.remoteAddress()).getAddress().getHostAddress();
	}

	public void setCvid(int cvid) {
		this.cvid = cvid;
	}

}
