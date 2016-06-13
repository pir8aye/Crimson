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
package com.subterranean_security.crimson.core.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.thavam.util.concurrent.BlockingHashMap;

import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class BasicConnector implements AutoCloseable {

	public abstract void write(Message m);

	// Buffers
	public final BlockingQueue<Message> nq = new LinkedBlockingQueue<Message>();
	public final BlockingHashMap<Integer, Message> cq = new BlockingHashMap<Integer, Message>();
	public final BlockingQueue<Message> uq = new LinkedBlockingQueue<Message>();

	protected EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Override
	public void close() throws InterruptedException {
		workerGroup.shutdownGracefully();

	}

}
