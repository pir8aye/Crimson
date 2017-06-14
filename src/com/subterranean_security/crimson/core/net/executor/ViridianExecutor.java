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
package com.subterranean_security.crimson.core.net.executor;

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.MSG.Message;

import io.netty.util.ReferenceCountUtil;

public class ViridianExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ViridianExecutor.class);

	public ViridianExecutor() {
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

					connector.addNewResponse(m);

					ReferenceCountUtil.release(m);
				});

			}
		});

	}
}
