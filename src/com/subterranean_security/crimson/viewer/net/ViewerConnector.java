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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLException;

import org.thavam.util.concurrent.BlockingHashMap;

import com.subterranean_security.crimson.core.net.BasicConnector;
import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ViewerConnector extends BasicConnector {

	// Buffers
	public final BlockingQueue<Message> nq = new LinkedBlockingQueue<Message>();
	public final BlockingHashMap<Integer, Message> cq = new BlockingHashMap<Integer, Message>();
	public final BlockingQueue<Message> uq = new LinkedBlockingQueue<Message>();

	public ViewerHandler handle = new ViewerHandler(this);
	public ViewerExecutor executor = new ViewerExecutor(this);

	public ViewerConnector(String host, int port) throws InterruptedException, SSLException {

		Bootstrap b = new Bootstrap();
		b.group(workerGroup)//
				.channel(NioSocketChannel.class)//
				.handler(new ViewerInitializer(host, port, handle));

		b.connect(host, port).sync();

	}

	@Override
	public void write(Message m) {
		handle.write(m);
	}

}
