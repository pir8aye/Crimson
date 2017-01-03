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

import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class BasicHandler extends SimpleChannelInboundHandler<Message> {

	public volatile Channel channel;

	public void write(Message msg) {
		channel.writeAndFlush(msg);
	}

	public void write(Message.Builder msg) {
		channel.writeAndFlush(msg.build());
	}

	public String getRemoteAddress() {
		return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
	}

}
