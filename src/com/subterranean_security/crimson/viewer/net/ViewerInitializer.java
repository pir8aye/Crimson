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

import com.subterranean_security.crimson.core.proto.MSG;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class ViewerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private final String host;
	private final int port;
	private final ViewerHandler vh;

	public ViewerInitializer(SslContext sslCtx, String host, int port, ViewerHandler vh) {
		this.sslCtx = sslCtx;
		this.host = host;
		this.port = port;
		this.vh = vh;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			// p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
		}

		// p.addLast(new LoggingHandler(LogLevel.INFO));

		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(MSG.Message.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		p.addLast(vh);
	}
}
