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

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.proto.Keylogger.State;
import com.subterranean_security.crimson.core.proto.Keylogger.Trigger;
import com.subterranean_security.crimson.core.util.Validation;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class ClientProfile extends Profile {

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

	public ClientProfile initialize() {
		if (!initialized) {
			// load icons
			if (Universal.instance == Universal.Instance.VIEWER) {
				loadIcons();
			}

			// load keylog
			keylog.pages.setDatabase(DatabaseStore.getDatabase());
			initialized = true;
		}
		return this;
	}

	public void loadIcons() {
		// os icon
		if (osTypeIcon == null && get(AKeySimple.OS_NAME) != null) {
			String icon = get(AKeySimple.OS_NAME).replaceAll(" ", "_").toLowerCase();

			// filtering
			if (icon.contains("ubuntu")) {
				icon = "ubuntu";
			}

			// load icons or fallbacks
			osTypeIcon = UIUtil.getIconOrFallback("icons16/platform/" + icon + ".png",
					"icons16/platform/" + get(AKeySimple.OS_FAMILY).toLowerCase() + ".png");
			osMonitorIcon = UIUtil.getIconOrFallback("icons32/platform/monitors/" + icon + ".png",
					"icons32/platform/monitors/" + get(AKeySimple.OS_FAMILY).toLowerCase() + ".png");

			osTypeIcon.setDescription(get(AKeySimple.OS_NAME));
			osMonitorIcon.setDescription(get(AKeySimple.NET_HOSTNAME));

		}

		// location
		if (ipLocationIcon == null && get(AKeySimple.NET_EXTERNALIP) != null) {
			if (Validation.privateIP(get(AKeySimple.NET_EXTERNALIP))) {
				ipLocationIcon = UIUtil.getIcon("icons16/general/localhost.png");
				ipLocationIcon.setDescription("Private IP");
			} else {
				try {
					ipLocationIcon = UIUtil
							.getIcon("flags/" + get(AKeySimple.IPLOC_COUNTRYCODE).toLowerCase() + ".png");
					ipLocationIcon.setDescription(get(AKeySimple.IPLOC_COUNTRY));
				} catch (NullPointerException e) {
					Reporter.report(Reporter.newReport()
							.setCrComment("No location icon found: " + get(AKeySimple.IPLOC_COUNTRYCODE).toLowerCase())
							.build());

					// fall back to default
					ipLocationIcon = UIUtil.getIcon("flags/un.png");
					ipLocationIcon.setDescription("Unknown");
				}

			}

		}

	}

	public int getMessageLatency() {
		return messageLatency;
	}

	public void setMessageLatency(int messageLatency) {
		this.messageLatency = messageLatency;
	}

	public int getCid() {
		return cvid;
	}

	public void setCid(int cid) {
		this.cvid = cid;
		set(AKeySimple.CLIENT_CID, "" + cid);
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

	public boolean getOnline() {
		return get(AKeySimple.CLIENT_ONLINE).equals("1");
	}

	public static class CidComparator implements Comparator<ClientProfile> {
		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			return o1.getCid() - o2.getCid();
		}
	}

	public static class SimpleAttributeComparator implements Comparator<ClientProfile> {
		private AKeySimple sa;

		public SimpleAttributeComparator(AKeySimple sa) {
			this.sa = sa;
		}

		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			if (o1.get(sa) == null) {
				return (o2.get(sa) == null) ? 0 : 1;
			}
			return o1.get(sa).compareTo(o2.get(sa));
		}
	}

}
