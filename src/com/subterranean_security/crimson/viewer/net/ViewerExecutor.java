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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.viewer.ViewerStore;

import io.netty.util.ReferenceCountUtil;

public class ViewerExecutor extends BasicExecutor {

	private ViewerConnector connector;

	public ViewerExecutor(ViewerConnector vc) {
		connector = vc;

		ubt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.uq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasEvProfileDelta()) {
						ViewerStore.Profiles.update(m.getEvProfileDelta());
					} else if (m.hasEvServerInfoDelta()) {
						ViewerStore.Profiles.update(m.getEvServerInfoDelta());
					}
					ReferenceCountUtil.release(m);
				}
			}
		});
		ubt.start();

		nbt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.nq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasMiAssignCvid()) {
						assign_1w(m);
					} else {
						connector.cq.put(m.getId(), m);
					}

				}
			}
		});
		nbt.start();
	}

	private void assign_1w(Message m) {
		Common.cvid = m.getMiAssignCvid().getId();
		ViewerStore.Databases.local.storeObject("cvid", Common.cvid);
	}

}
