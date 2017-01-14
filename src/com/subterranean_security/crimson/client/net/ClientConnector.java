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

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_AuthRequest;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.util.IDGen;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
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
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		b.group(workerGroup)//
				.channel(NioSocketChannel.class)//
				.handler(new ClientInitializer(host, port, handle));

		b.connect(host, port).sync();
		setState(ConnectionState.CONNECTED);

		// SSL Connection established

		AuthType authType = Client.ic.getAuthType();

		MI_AuthRequest.Builder auth = MI_AuthRequest.newBuilder().setCvid(Common.cvid).setType(authType);

		switch (authType) {
		case GROUP:
			AuthenticationGroup group = Client.getGroup();
			auth.setGroupName(group.getName());
			try {
				group.destroy();
			} catch (DestroyFailedException e) {
			}
			setState(ConnectionState.AUTH_STAGE1);
			handle.write(Message.newBuilder().setId(IDGen.msg()).setMiAuthRequest(auth).build());
			break;
		case NO_AUTH:
			setState(ConnectionState.AUTHENTICATED);
			handle.write(Message.newBuilder().setId(IDGen.msg())
					.setMiAuthRequest(auth.setPd(Platform.fig())).build());

			break;
		case PASSWORD:
			auth.setPassword(Client.ic.getPassword());

			setState(ConnectionState.AUTH_STAGE1);
			handle.write(Message.newBuilder().setId(IDGen.msg()).setMiAuthRequest(auth).build());
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
