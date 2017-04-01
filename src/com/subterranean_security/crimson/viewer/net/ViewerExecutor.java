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

import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.store.ProfileStore;

import io.netty.util.ReferenceCountUtil;

public class ViewerExecutor extends BasicExecutor {

	private static final Logger log = LoggerFactory.getLogger(ViewerExecutor.class);

	public ViewerExecutor() {
		super();

		dispatchThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				Message m;
				try {
					m = connector.msgQueue.take();
				} catch (InterruptedException e) {
					log.error("Message dispatch thread interrupted");
					return;
				}

				pool.submit(() -> {
					if (m.hasEvStreamData()) {
						System.out.println("Got stream data");
						Stream s = StreamStore.getStream(m.getEvStreamData().getStreamID());
						if (s != null) {
							s.received(m);
						}
					} else if (m.hasEvEndpointClosed()) {
						StreamStore.removeStreamsByCVID(m.getEvEndpointClosed().getCVID());
					} else if (m.hasEvProfileDelta()) {
						ProfileStore.update(m.getEvProfileDelta());
					} else if (m.hasEvServerProfileDelta()) {
						ProfileStore.update(m.getEvServerProfileDelta());
					} else if (m.hasEvViewerProfileDelta()) {
						ProfileStore.update(m.getEvViewerProfileDelta());
					} else if (m.hasEvKevent()) {
						ev_kevent(m);
					} else {
						connector.addNewResponse(m);
					}
					ReferenceCountUtil.release(m);
				});

			}
		});

	}

	private void ev_kevent(Message m) {
		ClientProfile cp = ProfileStore.getClient(m.getSid());
		if (cp != null) {
			cp.getKeylog().addEvent(m.getEvKevent());
		} else {
			log.debug("Failed to find client: " + m.getSid());
		}
	}

}
