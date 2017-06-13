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

import static com.subterranean_security.crimson.universal.Flags.LOG_NET_RAW;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.proto.MSG;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Connector extends Observable {

	private static final Logger log = LoggerFactory.getLogger(Connector.class);

	public static abstract class Config {

		public static enum ConnectionType {
			SOCKET, DATAGRAM;

			public Class<? extends Channel> getChannel() {
				switch (this) {
				case DATAGRAM:
					return NioDatagramChannel.class;
				case SOCKET:
					return NioSocketChannel.class;
				default:
					return null;
				}

			}
		}

	}

	private int cvid;

	private Universal.Instance instance;
	private Config.ConnectionType type;
	private ConnectionState state;
	private CertificateState certState;

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

	public Connector(BasicExecutor exe, BasicHandler han, boolean forceCerts) {
		// initialize state
		state = ConnectionState.NOT_CONNECTED;

		// initialize message buffers
		msgQueue = new LinkedBlockingQueue<Message>();
		responseMap = new HashMap<Integer, MessageFuture>();

		workerGroup = new NioEventLoopGroup();

		// initialize handler
		this.handler = han;
		handler.setConnector(this);

		// initialize executor
		this.executor = exe;
		executor.setConnector(this);
		executor.start();
	}

	public Connector(BasicExecutor executor) {
		this(executor, true);
	}

	public Connector(BasicExecutor executor, boolean forceCerts) {
		this(executor, new BasicHandler(), forceCerts);
	}

	private boolean FORCE_CERTIFICATES;

	public boolean isForceCerts() {
		return FORCE_CERTIFICATES;
	}

	public void connect(Config.ConnectionType type, String host, int port)
			throws InterruptedException, ConnectException {
		connect(type, host, port, true);
	}

	public void connect(Config.ConnectionType type, String host, int port, boolean forceCerts)
			throws InterruptedException, ConnectException {

		if (getState() == ConnectionState.NOT_CONNECTED) {
			this.type = type;
			this.FORCE_CERTIFICATES = forceCerts;

			new Bootstrap().channel(type.getChannel()).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.group(workerGroup).handler(new InitiatorInitializer(host, port, forceCerts)).connect(host, port)
					.sync();

			// wait for handshake to complete
			int sleepTime = 0;
			while (certState == null) {
				sleepTime += 70;
				Thread.sleep(70);
				if (sleepTime > 5000) {
					log.debug("Timeout while waiting for handshake");
					setCertState(CertificateState.REFUSED);
					setState(ConnectionState.NOT_CONNECTED);
					break;
				}
			}
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

	public CertificateState getCertState() {
		return certState;
	}

	public void setState(ConnectionState state) {
		if (this.state != state) {
			log.debug("[CVID {}] Connector state changed: {}->{}", cvid, this.state, state);
			this.state = state;

			setChanged();
			notifyObservers(state);
		}
	}

	public void setCertState(CertificateState certState) {
		if (this.certState != certState) {
			log.debug("[CVID {}] Certificate state changed: {}->{}", cvid, this.certState, certState);
			this.certState = certState;

			setChanged();
			notifyObservers(certState);
		}
	}

	public Config.ConnectionType getType() {
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

	public enum ConnectionState {
		// TODO remove auth stages
		NOT_CONNECTED, CONNECTED, AUTHENTICATED, AUTH_STAGE1, AUTH_STAGE2;
	}

	public enum CertificateState {
		/**
		 * The certificate has been validated.
		 */
		VALID,

		/**
		 * The certificate is either revoked, expired, invalid, self-signed, or
		 * missing. The connector has nevertheless established a connection.
		 */
		INVALID,

		/**
		 * The certificate is INVALID and the connector refused to proceed
		 * because FORCE_CERTIFICATES was set.
		 */
		REFUSED;
	}

	public X509Certificate getPeerCertificate() throws SSLPeerUnverifiedException {
		SslHandler sslHandler = (SslHandler) handler.getChannel().pipeline().get("ssl");
		return (X509Certificate) sslHandler.engine().getSession().getPeerCertificates()[0];
	}

	class InitiatorInitializer extends ChannelInitializer<SocketChannel> {

		private final String host;
		private final int port;
		private final boolean forceCerts;

		public InitiatorInitializer(String host, int port, boolean forceCerts) {
			this.host = host;
			this.port = port;
			this.forceCerts = forceCerts;
		}

		private X509Certificate getRoot() throws CertificateException, IOException {
			try (InputStream in = Connector.class
					.getResourceAsStream("/com/subterranean_security/crimson/core/net/certs/root.cert")) {

				return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
			}
		}

		@Override
		public void initChannel(SocketChannel ch) {
			ChannelPipeline p = ch.pipeline();

			if (forceCerts) {
				try {
					p.addLast("ssl", SslContextBuilder.forClient().trustManager(getRoot()).build()
							.newHandler(ch.alloc(), host, port));
				} catch (CertificateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					p.addLast("ssl", SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
							.build().newHandler(ch.alloc(), host, port));
				} catch (SSLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (LOG_NET_RAW) {
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
