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
package com.subterranean_security.crimson.sv.keylogger;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.stream.subscriber.SubscriberSlave;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger.Keylogger;

public class LogCallback {
	Object target;

	public LogCallback(Object target) {
		this.target = target;
	}

	public void launch(EV_KEvent k) {
		switch (Common.instance) {

		case SERVER:
			((SubscriberSlave) target).trigger(k);
			break;
		case VIEWER:
			((Keylogger) target).addKEvent(k);
			break;

		}

	}
}
