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
package com.subterranean_security.crimson.viewer.net;

import java.net.ConnectException;
import java.net.InetSocketAddress;

import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ViewerConnector extends BasicConnector {

	public ViewerHandler handle = new ViewerHandler(this);
	public ViewerExecutor executor = new ViewerExecutor(this);

	public ViewerConnector(String host, int port) throws InterruptedException, ConnectException {

		Bootstrap b = new Bootstrap();
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		b.group(workerGroup)//
				.channel(NioSocketChannel.class)//
				.handler(new ViewerInitializer(host, port, handle));

		b.connect(host, port).sync();

	}

	@Override
	public void write(Message m) {
		handle.write(m);
	}

	public String getRemoteAddress() {
		return ((InetSocketAddress) handle.channel.remoteAddress()).getAddress().getHostAddress();
	}

}
