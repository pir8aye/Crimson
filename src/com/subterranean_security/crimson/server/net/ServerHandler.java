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
package com.subterranean_security.crimson.server.net;

import com.subterranean_security.crimson.core.net.BasicHandler;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.server.ServerStore;

import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends BasicHandler {

	private Receptor receptor;

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
		receptor = new Receptor(this);
		ServerStore.Connections.add(receptor);
	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// TODO sort out different causes
		// cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Message msg) throws Exception {
		if (msg.hasUrgent()) {
			receptor.uq.add(msg);
		} else {
			receptor.nq.add(msg);
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
}
