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
package com.subterranean_security.crimson.sv.net;

import static com.subterranean_security.crimson.universal.Flags.LOG_NET_RAW;

import java.io.Serializable;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

public class DatagramListener implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient ChannelFuture future;
	private transient EventLoopGroup bossGroup;

	private ListenerConfig config;

	public DatagramListener(ListenerConfig lc) {
		config = lc;
		start();
	}

	public void start() {
		bossGroup = new NioEventLoopGroup();

		Bootstrap b = new Bootstrap();
		b.group(bossGroup).channel(NioServerSocketChannel.class)//
				.handler(new ReceiverInitializer())//
				.option(ChannelOption.SO_BACKLOG, 128)//
				.option(ChannelOption.SO_KEEPALIVE, true);

		future = b.bind(config.getPort());

	}

	public void close() throws InterruptedException {
		try {
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
		}
	}

	class ReceiverInitializer extends ChannelInitializer<DatagramChannel> {

		public ReceiverInitializer() {

		}

		@Override
		public void initChannel(DatagramChannel ch) throws Exception {
			Connector connector = new Connector(BasicExecutor.getInstanceExecutor());

			// initially assign a random cvid
			// connector.setCvid(IDGen.cvid());
			// ConnectionStore.add(connector);

			ChannelPipeline p = ch.pipeline();

			if (LOG_NET_RAW) {
				p.addLast(new LoggingHandler(Connector.class));
			}

			p.addLast(new ProtobufVarint32FrameDecoder());
			p.addLast(new ProtobufDecoder(MSG.Message.getDefaultInstance()));

			p.addLast(new ProtobufVarint32LengthFieldPrepender());
			p.addLast(new ProtobufEncoder());

			p.addLast(connector.getHandler());

			connector.setState(ConnectionState.CONNECTED);
		}
	}

}
