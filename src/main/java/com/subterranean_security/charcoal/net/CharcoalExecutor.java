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
package com.subterranean_security.charcoal.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.charcoal.ui.MainFrame;
import com.subterranean_security.charcoal.ui.components.IPanel;
import com.subterranean_security.charcoal.ui.components.IPanel.State;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.proto.core.net.sequences.Debug.RS_DebugSession;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal.Instance;

import io.netty.util.ReferenceCountUtil;

public class CharcoalExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(CharcoalExecutor.class);

	private IPanel pane;

	public CharcoalExecutor() {
		super();

		dispatchThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.interrupted()) {
					Message m;
					try {
						m = connector.msgQueue.take();
					} catch (InterruptedException e) {
						return;
					}
					pool.submit(() -> {

						switch (m.getMsgOneofCase()) {
						case RQ_DEBUG_SESSION:
							rq_debug_session(m);
							break;
						case EV_DEBUG_LOG_EVENT:
							pane.addLine(m.getEvDebugLogEvent().getLine() + "\n");
							break;
						case EV_PROFILE_DELTA:
							break;
						default:
							connector.addNewResponse(m);
							break;
						}

						ReferenceCountUtil.release(m);
					});
				}
			}
		});

	}

	private void rq_debug_session(Message msg) {

		boolean pass = false;
		for (IPanel lp : MainFrame.open_panels) {
			if (lp.getInstance() == Instance.valueOf(msg.getRqDebugSession().getInstance())) {
				pane = lp;
				lp.setState(State.CONNECTED);
				lp.addLine("Session established with: " + connector.getRemoteIP() + "\n");
				pass = true;
				break;
			}
		}

		connector.write(Message.newBuilder().setRsDebugSession(RS_DebugSession.newBuilder().setResult(pass)).build());
	}
}
