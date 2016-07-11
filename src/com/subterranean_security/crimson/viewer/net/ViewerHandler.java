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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.BasicHandler;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.viewer.ViewerState;

import io.netty.channel.ChannelHandlerContext;

public class ViewerHandler extends BasicHandler {

	private static final Logger log = LoggerFactory.getLogger(ViewerHandler.class);

	private ViewerConnector connector;

	public ViewerHandler(ViewerConnector vc) {
		connector = vc;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
	};

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ViewerState.goOffline();
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ViewerState.goOffline();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Message msg) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("RECEIVE \n{}", msg.toString());
		}

		if (msg.hasUrgent()) {
			connector.uq.add(msg);
		} else {
			connector.nq.add(msg);
		}

	}

	public void execute(Message msg) {
		try {
			channelRead0(null, msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
