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
package com.subterranean_security.crimson.sv.net;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.MSG;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Listener {

	private ChannelFuture future;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private ListenerConfig config;

	public Listener(ListenerConfig lc) {
		if (lc == null)
			throw new IllegalArgumentException();

		config = lc;
	}

	/**
	 * @return The ListenerConfig associated with this listener
	 */
	public ListenerConfig getConfig() {
		return config;
	}

	/**
	 * Start this listener
	 * 
	 * @pre: isActive() == false
	 * @post: isActive() == true
	 */
	public void start() {
		if (!isActive()) {
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)//
					.childHandler(new ReceiverInitializer())//
					.option(ChannelOption.SO_BACKLOG, 128)//
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			future = b.bind(config.getPort());

		}
	}

	/**
	 * Stop this listener
	 * 
	 * @pre: isActive() == true
	 * @post: isActive() == false
	 */
	public void stop() {
		if (isActive()) {
			try {
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}
		}
	}

	/**
	 * Get the activity of this listener
	 * 
	 * @return True is this listener has been started and not yet stopped
	 */
	public boolean isActive() {
		return bossGroup != null && workerGroup != null;
	}

}

class ReceiverInitializer extends ChannelInitializer<SocketChannel> {

	private SslContext sslCtx;

	public ReceiverInitializer() {
		try {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		Connector connector = new Connector(BasicExecutor.getInstanceExecutor());

		// initially assign a random cvid
		connector.setCvid(IDGen.cvid());
		ConnectionStore.add(connector);

		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}

		if (Universal.debugRawNetwork) {
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
