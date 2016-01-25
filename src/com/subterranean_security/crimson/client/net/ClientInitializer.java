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
package com.subterranean_security.crimson.client.network;

import com.subterranean_security.crimson.core.proto.msg.MSG;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private final String host;
	private final int port;
	private final ClientHandler ch;

	public ClientInitializer(SslContext sslCtx, String host, int port, ClientHandler ch) {
		this.sslCtx = sslCtx;
		this.host = host;
		this.port = port;
		this.ch = ch;
	}

	@Override
	public void initChannel(SocketChannel sc) {
		ChannelPipeline p = sc.pipeline();
		if (sslCtx != null) {
//			p.addLast(sslCtx.newHandler(sc.alloc(), host, port));
		}

		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(MSG.Message.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		p.addLast(ch);
	}
}
