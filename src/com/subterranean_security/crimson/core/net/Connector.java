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
package com.subterranean_security.crimson.core.net;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.MSG;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Connector extends Observable {

	private static final Logger log = LoggerFactory.getLogger(Connector.class);

	private int cvid;

	private Universal.Instance instance;
	private ConnectionType type;
	private ConnectionState state;
	private EventLoopGroup workerGroup;

	private BasicExecutor executor;
	private BasicHandler handler;

	/**
	 * All incoming messages are dropped into this queue and wait for processing
	 */
	public BlockingQueue<Message> msgQueue;

	/**
	 * When a response message is desired, a MessageFuture is placed into this
	 * map. If the BasicExecutor cannot execute a message and a corresponding
	 * entry in this map exists, the MessageFuture is removed and notified.
	 */
	private Map<Integer, MessageFuture> responseMap;

	public Connector(BasicExecutor executor, BasicHandler handler) {
		// initialize state
		state = ConnectionState.NOT_CONNECTED;
		workerGroup = new NioEventLoopGroup();

		// initialize message buffers
		msgQueue = new LinkedBlockingQueue<Message>();
		responseMap = new HashMap<Integer, MessageFuture>();

		// initialize executor and handler
		this.executor = executor;
		this.handler = handler;

		executor.setConnector(this);
		handler.setConnector(this);

		executor.start();
	}

	public Connector(BasicExecutor executor) {
		this(executor, new BasicHandler());
	}

	public void connect(ConnectionType type, String host, int port) throws InterruptedException, ConnectException {
		if (getState() == ConnectionState.NOT_CONNECTED) {
			this.type = type;
			Bootstrap b = new Bootstrap();
			switch (type) {
			case DATAGRAM:
				b.channel(NioDatagramChannel.class);
				break;
			case SOCKET:
				b.channel(NioSocketChannel.class);
				break;
			default:
				break;
			}

			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
			b.group(workerGroup).handler(new InitiatorInitializer(host, port));

			b.connect(host, port).sync();

			setState(ConnectionState.CONNECTED);
		}

	}

	public void addNewResponse(Message m) {
		if (responseMap.containsKey(m.getId())) {
			responseMap.remove(m.getId()).setMessage(m);
		} else {
			// dropping this message because no thread is waiting for it
		}
	}

	public MessageFuture getResponse(int id) {
		if (!responseMap.containsKey(id)) {
			responseMap.put(id, new MessageFuture());
		}
		return responseMap.get(id);
	}

	public MessageFuture writeAndGetResponse(Message m) {
		write(m);
		return getResponse(m.getId());
	}

	public void write(Message m) {
		handler.write(m);
	}

	public String getRemoteIP() {
		return handler.getRemoteIP();
	}

	public int getRemotePort() {
		return handler.getRemotePort();
	}

	public BasicHandler getHandler() {
		return handler;
	}

	public void close() {
		setState(ConnectionState.NOT_CONNECTED);

		workerGroup.shutdownGracefully();
		executor.stop();
		deleteObservers();
	}

	public ConnectionState getState() {
		return state;
	}

	public void setState(ConnectionState state) {
		if (this.state != state) {
			log.debug("Connector state changed: {}->{}", this.state, state);
			this.state = state;

			setChanged();
			notifyObservers(state);
		}
	}

	public ConnectionType getType() {
		return type;
	}

	public Universal.Instance getInstance() {
		return instance;
	}

	public void setInstance(Universal.Instance instance) {
		this.instance = instance;
	}

	public int getCvid() {
		return cvid;
	}

	public void setCvid(int cvid) {
		this.cvid = cvid;
	}

	public enum ConnectionType {
		SOCKET, DATAGRAM;
	}

	public enum ConnectionState {
		// TODO remove auth stages
		NOT_CONNECTED, CONNECTED, AUTHENTICATED, AUTH_STAGE1, AUTH_STAGE2;
	}

	class InitiatorInitializer extends ChannelInitializer<SocketChannel> {

		private SslContext sslCtx;
		private final String host;
		private final int port;

		public InitiatorInitializer(String host, int port) {
			try {
				this.sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.host = host;
			this.port = port;
		}

		@Override
		public void initChannel(SocketChannel ch) {
			ChannelPipeline p = ch.pipeline();
			if (sslCtx != null) {
				p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
			}

			if (Universal.debugRawNetwork) {
				p.addLast(new LoggingHandler(Connector.class));
			}

			p.addLast(new ProtobufVarint32FrameDecoder());
			p.addLast(new ProtobufDecoder(MSG.Message.getDefaultInstance()));

			p.addLast(new ProtobufVarint32LengthFieldPrepender());
			p.addLast(new ProtobufEncoder());

			p.addLast(handler);
		}
	}

}
