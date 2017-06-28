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
package com.subterranean_security.crimson.sv.profile;

import java.util.Comparator;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.Trigger;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

/**
 * @author cilki
 * @since 4.0.0
 */
public class ClientProfile extends Profile implements CVProfile {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ClientProfile.class);

	// Transient attributes
	private transient ImageIcon ipLocationIcon;
	private transient ImageIcon osTypeIcon;
	private transient ImageIcon osMonitorIcon;
	private transient boolean initialized;
	private transient int messageLatency;

	private int authID;

	public int getAuthID() {
		return authID;
	}

	public void setAuthID(int authID) {
		this.authID = authID;
	}

	// Keylogger options
	private Log keylog;

	public Log getKeylog() {
		return keylog;
	}

	public ClientProfile(int cid) {
		this();
		this.cvid = cid;
	}

	public ClientProfile() {
		super();
		keylog = new Log();
		set(AKeySimple.CLIENT_ONLINE, "1");

	}

	// TODO remove
	public ClientProfile initialize() {
		if (!initialized) {

			// load keylog
			keylog.pages.setDatabase(DatabaseStore.getDatabase());
			initialized = true;
		}
		return this;
	}

	public int getMessageLatency() {
		return messageLatency;
	}

	public void setMessageLatency(int messageLatency) {
		this.messageLatency = messageLatency;
	}

	public ImageIcon getLocationIcon() {
		return ipLocationIcon;
	}

	public ImageIcon getOsNameIcon() {
		return osTypeIcon;
	}

	public ImageIcon getOsMonitorIcon() {
		return osMonitorIcon;
	}

	/*
	 * 
	 * Convenience methods for attributes requiring type conversion
	 * 
	 */

	public Trigger getKeyloggerTrigger() {
		switch (get(AKeySimple.KEYLOGGER_TRIGGER)) {
		case "0":
			return Trigger.EVENT;
		case "1":
			return Trigger.PERIODIC;
		}
		return null;
	}

	public void setKeyloggerTrigger(Trigger trigger) {
		set(AKeySimple.KEYLOGGER_TRIGGER, "" + trigger.ordinal());
	}

	public int getKeyloggerTriggerValue() {
		return Integer.parseInt(get(AKeySimple.KEYLOGGER_TRIGGER_VALUE));
	}

	public void setKeyloggerTriggerValue(int triggerValue) {
		set(AKeySimple.KEYLOGGER_TRIGGER_VALUE, "" + triggerValue);
	}

	public State getKeyloggerState() {
		switch (get(AKeySimple.KEYLOGGER_STATE)) {
		case "0":
			return State.UNINSTALLED;
		case "1":
			return State.OFFLINE;
		case "2":
			return State.ONLINE;
		}
		return null;
	}

	public void setKeyloggerState(State keyloggerState) {
		set(AKeySimple.KEYLOGGER_STATE, "" + keyloggerState.ordinal());
	}

	public OSFAMILY getOSFamily() {
		return OSFAMILY.valueOf(get(AKeySimple.OS_FAMILY).toUpperCase());
	}

	public void setOnline(boolean online) {
		boolean state = getOnline();
		if (online) {
			if (!state) {
				set(AKeySimple.CLIENT_ONLINE, "1");
			}
		} else {
			if (state) {
				set(AKeySimple.CLIENT_ONLINE, "0");
			}
		}

	}

	public static class CidComparator implements Comparator<ClientProfile> {
		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			return o1.getCvid() - o2.getCvid();
		}
	}

	/**
	 * @author cilki
	 * @since 4.0.0
	 */
	public static class SingularAttributeComparator implements Comparator<ClientProfile> {
		private SingularKey key;

		public SingularAttributeComparator(SingularKey sk) {
			this.key = sk;
		}

		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			if (o1.get(key) == null) {
				return (o2.get(key) == null) ? 0 : 1;
			}
			return o1.get(key).compareTo(o2.get(key));
		}
	}

}
