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
package com.subterranean_security.crimson.client.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.store.ConnectionStore;
import com.subterranean_security.crimson.core.net.BasicHandler;
import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends BasicHandler {

	private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private ClientConnector connector;

	public ClientHandler(ClientConnector cc) {
		this.connector = cc;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
	};

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
		new Thread(new Runnable() {
			public void run() {
				ConnectionStore.connectionRoutine();
			}
		}).start();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		new Thread(new Runnable() {
			public void run() {
				ConnectionStore.connectionRoutine();
			}
		}).start();

	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Message msg) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("RECEIVE \n{}", msg.toString());
		}

		connector.mq.add(msg);
	}

}
