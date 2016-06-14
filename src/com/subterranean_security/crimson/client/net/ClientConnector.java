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

import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.ClientAuth.AuthType;
import com.subterranean_security.crimson.core.proto.ClientAuth.Group;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.IDGen;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientConnector extends BasicConnector {

	private static final Logger log = LoggerFactory.getLogger(ClientConnector.class);

	public ClientHandler handle = new ClientHandler(this);
	public ClientExecutor executor = new ClientExecutor(this);

	// state
	private ConnectionState state = ConnectionState.NOT_CONNECTED;

	public void setState(ConnectionState cs) {
		log.debug("New connection state: {}", cs.toString());
		state = cs;
	}

	public ConnectionState getState() {
		return state;
	}

	public ClientConnector(String host, int port) throws InterruptedException, ConnectException {

		Bootstrap b = new Bootstrap();
		b.group(workerGroup)//
				.channel(NioSocketChannel.class)//
				.handler(new ClientInitializer(host, port, handle));

		b.connect(host, port).sync();
		setState(ConnectionState.CONNECTED);

		// SSL Connection established

		AuthType authType = null;
		try {
			authType = (AuthType) Client.clientDB.getObject("auth.type");
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		MI_AuthRequest.Builder auth = MI_AuthRequest.newBuilder().setCvid(Common.cvid).setType(authType);

		switch (authType) {
		case GROUP:
			Group group = null;
			try {
				group = (Group) Client.clientDB.getObject("auth.group");
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			auth.setGroupName(group.getName());
			setState(ConnectionState.AUTH_STAGE1);
			handle.write(Message.newBuilder().setId(IDGen.get()).setMiAuthRequest(auth).build());
			break;
		case NO_AUTH:
			setState(ConnectionState.AUTHENTICATED);
			handle.write(Message.newBuilder().setId(IDGen.get())
					.setMiAuthRequest(auth.setPd(Platform.Advanced.getFullProfile())).build());

			break;
		case PASSWORD:
			try {
				auth.setPassword(Client.clientDB.getString("auth.password"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			setState(ConnectionState.AUTH_STAGE1);
			handle.write(Message.newBuilder().setId(IDGen.get()).setMiAuthRequest(auth).build());
			break;
		default:
			break;

		}

	}

	@Override
	public void write(Message m) {
		handle.write(m);
	}

}
