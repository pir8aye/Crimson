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

import java.util.concurrent.TimeUnit;

import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.viewer.ViewerStore;

public enum ViewerRouter {
	;
	public static void route(Message m) {

		try {
			ViewerStore.Connections.get(m.getRid()).write(m);
			return;
		} catch (NullPointerException e) {
			// try server
		}
		// send to server
		ViewerStore.Connections.get(0).write(m);

	}

	public static void route(Message.Builder m) {

		route(m.build());
	}

	public static Message getReponse(int cvid, int mid, int timeout) throws InterruptedException {
		// TODO receive from direct connections
		return ViewerStore.Connections.getVC(0).cq.take(mid, timeout, TimeUnit.SECONDS);
	}

	public static Message routeAndWait(Message.Builder m, int timeout) throws InterruptedException {
		if (!m.hasId()) {
			m.setId(IDGen.msg());
		}
		route(m);
		return getReponse(m.getRid(), m.getId(), timeout);
	}

}
