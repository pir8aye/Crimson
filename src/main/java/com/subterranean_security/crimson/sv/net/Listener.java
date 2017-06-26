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

import static com.subterranean_security.crimson.universal.Flags.LOG_NET_RAW;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG;

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

/**
 * A TCP listener which binds to a port and handles new connections.
 * 
 * @author cilki
 * @since 1.0.0
 */
public class Listener {
	private static final Logger log = LoggerFactory.getLogger(Listener.class);

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

	private class ReceiverInitializer extends ChannelInitializer<SocketChannel> {

		public ReceiverInitializer() {

		}

		private SslContext getSslContext() {
			if (!config.getCert().isEmpty() && !config.getKey().isEmpty()) {
				try {
					return SslContextBuilder
							.forServer(new ByteArrayInputStream(Base64.getDecoder().decode(config.getCert())),
									new ByteArrayInputStream(Base64.getDecoder().decode(config.getKey())))
							.build();
				} catch (SSLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// fallback to a self signed certificate
			log.debug("Using a self-signed certificate");
			try {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SSLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log.warn("Failed to get a certificate. SSL will NOT be used for this connection!");
			return null;
		}

		@Override
		public void initChannel(SocketChannel ch) throws Exception {
			Connector connector = new Connector(BasicExecutor.getInstanceExecutor());

			// initially assign a random cvid
			// connector.setCvid(IDGen.cvid());
			// ConnectionStore.add(connector);

			ChannelPipeline p = ch.pipeline();

			SslContext sslCtx = getSslContext();
			if (sslCtx != null) {
				p.addLast(sslCtx.newHandler(ch.alloc()));
			}

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
