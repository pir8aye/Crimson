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
package com.subterranean_security.crimson.viewer.net.listener;

import java.util.Observable;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.CertificateState;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.listener.ConnectionEventListener;
import com.subterranean_security.crimson.viewer.ViewerState;

public class ViewerConnectionEventListener extends ConnectionEventListener {

	@Override
	public void update(Observable o, Object arg) {
		super.update(o, arg);

		Connector connector = (Connector) o;

		if (arg instanceof ConnectionState) {
			if (connector.getCvid() == 0) {
				switch ((ConnectionState) arg) {
				case NOT_CONNECTED:
					ViewerState.goOffline();
					break;
				default:
					break;

				}
			}

		} else if (arg instanceof CertificateState) {

		}
	}

}