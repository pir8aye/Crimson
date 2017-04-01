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
package com.subterranean_security.crimson.viewer.net;

import java.util.Observable;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.ConnectionStore.ConnectionEventListener;
import com.subterranean_security.crimson.viewer.ViewerState;

public final class ViewerConnectionStore {
	private ViewerConnectionStore() {
	}

	public static void initialize() {
		ConnectionStore.initialize(new ViewerConnectionEventListener());
	}

	private static class ViewerConnectionEventListener implements ConnectionEventListener {

		@Override
		public void update(Observable o, Object arg) {
			Connector connector = (Connector) o;
			ConnectionState state = (ConnectionState) arg;

			if (connector.getCvid() == 0) {
				switch (state) {
				case NOT_CONNECTED:
					ViewerState.goOffline();
					break;
				default:
					break;

				}
			}
		}

	}
}
