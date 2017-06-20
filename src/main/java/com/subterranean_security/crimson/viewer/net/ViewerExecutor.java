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

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;

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
					if (LOG_NET) {
						log.debug("Received: {}", m.toString());
					}

					switch (m.getMsgOneofCase()) {
					case EV_ENDPOINT_CLOSED:
						StreamStore.removeStreamsByCVID(m.getEvEndpointClosed().getCVID());
						break;
					case EV_KEVENT:
						ev_kevent(m);
						break;
					case EV_PROFILE_DELTA:
						ViewerProfileStore.update(m.getEvProfileDelta());
						break;
					case EV_SERVER_PROFILE_DELTA:
						ViewerProfileStore.update(m.getEvServerProfileDelta());
						break;
					case EV_STREAM_DATA:
						System.out.println("Got stream data");
						Stream s = StreamStore.getStream(m.getEvStreamData().getStreamID());
						if (s != null) {
							s.received(m);
						}
						break;
					case EV_VIEWER_PROFILE_DELTA:
						ViewerProfileStore.update(m.getEvViewerProfileDelta());
						break;
					default:
						connector.addNewResponse(m);
						break;

					}

					ReferenceCountUtil.release(m);
				});

			}
		});

	}

	private void ev_kevent(Message m) {
		ClientProfile cp = ViewerProfileStore.getClient(m.getSid());
		if (cp != null) {
			cp.getKeylog().addEvent(m.getEvKevent());
		} else {
			log.debug("Failed to find client: " + m.getSid());
		}
	}

}
