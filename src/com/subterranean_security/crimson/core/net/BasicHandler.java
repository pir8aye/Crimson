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

import java.net.InetSocketAddress;

import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class BasicHandler extends SimpleChannelInboundHandler<Message> {

	protected Channel channel;
	protected Connector connector;

	public void write(Message msg) {
		channel.writeAndFlush(msg);
	}

	public void write(Message.Builder msg) {
		write(msg.build());
	}

	public String getRemoteIP() {
		return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
	}

	public int getRemotePort() {
		return ((InetSocketAddress) channel.remoteAddress()).getPort();
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
	};

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
		connector.setState(ConnectionState.NOT_CONNECTED);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		cause.printStackTrace();
		connector.setState(ConnectionState.NOT_CONNECTED);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Message msg) throws Exception {
		connector.msgQueue.add(msg);
	}

	// TODO is this needed?
	// @Override
	// public void channelReadComplete(ChannelHandlerContext ctx) {
	// ctx.flush();
	// }

}
