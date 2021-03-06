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
package com.subterranean_security.crimson.sv.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.ImageIcon;

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.util.IconUtil;

public abstract class Profile extends Observable implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, AttributeGroup>[] groups;

	public Profile() {
		// initialize attribute groups
		AttributeKey.Type[] types = AttributeKey.Type.values();
		groups = new HashMap[types.length];

		for (int i = 0; i < types.length; i++) {
			groups[i] = new HashMap<String, AttributeGroup>();
		}
	}

	public Map<String, AttributeGroup> getGroups(AttributeKey key) {
		return getGroups(key.getGroupType());
	}

	public Map<String, AttributeGroup> getGroups(int type) {
		return groups[type];
	}

	public List<AttributeGroup> getGroupList(AttributeKey.Type type) {
		return getGroupList(type.ordinal());
	}

	public List<AttributeGroup> getGroupList(int type) {
		ArrayList<AttributeGroup> list = new ArrayList<AttributeGroup>();
		list.addAll(groups[type].values());
		return list;
	}

	public AttributeGroup getGroup(AttributeKey key, String groupId) {
		return getGroup(key.getGroupType(), groupId);
	}

	public AttributeGroup getGroup(int type, String groupId) {
		return groups[type].get(groupId);
	}

	/**
	 * Get the value of the specified attribute for the singular group
	 * 
	 * @param attribute
	 * @return
	 */
	public String get(AKeySimple attribute) {
		return getAttribute(attribute).get();
	}

	public void set(AKeySimple key, String value) {
		getAttribute(key).set(value);

		setChanged();
		notifyObservers(key);
	}

	public void setAttr(int key, String value) {

	}

	/**
	 * Get the specified attribute from the singular group
	 * 
	 * @param key
	 * @return
	 */
	public Attribute getAttribute(AKeySimple key) {
		return getAttribute(key, "");
	}

	/**
	 * Get the specified attribute from the specified group
	 * 
	 * @param key
	 * @param groupID
	 * @return
	 */
	public Attribute getAttribute(AttributeKey key, String groupID) {
		Map<String, AttributeGroup> map = groups[key.getGroupType()];
		if (!map.containsKey(groupID)) {
			map.put(groupID, new AttributeGroup(key.getGroupType(), groupID));
		}
		return map.get(groupID).getAttribute(key);
	}

	public void amalgamate(EV_ProfileDelta c) {

		// If FIG, then set all groups to old
		// if (c.hasFig() && c.getFig()) {
		// for (HashMap<String, AttributeGroup> g : groups) {
		// for (AttributeGroup ag : g.values()) {
		// ag.setModern(false);
		// }
		// }
		// }

		for (AttributeGroupContainer container : c.getGroupList()) {
			Map<String, AttributeGroup> groupMap = groups[container.getGroupType()];
			if (!groupMap.containsKey(container.getGroupId())) {
				groupMap.put(container.getGroupId(),
						new AttributeGroup(container.getGroupType(), container.getGroupId()));
			}

			groupMap.get(container.getGroupId()).absorb(container);

		}

	}

	public EV_ProfileDelta getUpdates(Date start) {
		if (start == null)
			start = new Date(0);

		EV_ProfileDelta.Builder pd = EV_ProfileDelta.newBuilder().setCvid(cvid);

		// Attribute Groups
		for (Map<String, AttributeGroup> map : groups) {
			for (AttributeGroup group : map.values()) {
				AttributeGroupContainer container = group.getUpdated(start);
				if (container.getAttributeCount() > 0) {
					pd.addGroup(container);
				}
			}
		}

		return pd.build();

	}

	protected Date lastUpdate = new Date();

	public Date getLastUpdate() {
		return lastUpdate;
	}

	protected int cvid;

	public int getCvid() {
		return cvid;
	}

	public void setCvid(int cvid) {
		this.cvid = cvid;
		set(AKeySimple.CLIENT_CID, "" + cvid);
		System.out.println("Profile assigned new CVID: " + cvid);
	}

	protected transient ImageIcon osIcon16;

	/**
	 * @return A 16px OS icon
	 */
	public ImageIcon getOsIcon16() {
		if (osIcon16 == null) {
			// load icon or fallback
			osIcon16 = UIUtil.getIconOrFallback(IconUtil.getOsIconPath(get(AKeySimple.OS_NAME), 16),
					"icons16/platform/" + get(AKeySimple.OS_FAMILY).toLowerCase() + ".png");
		}
		return osIcon16;
	}

	protected transient ImageIcon monitorIcon16;

	/**
	 * @return A 16px OS monitor icon
	 */
	public ImageIcon getMonitorIcon16() {
		if (monitorIcon16 == null) {
			// load icon or fallback
			monitorIcon16 = UIUtil.getIconOrFallback(IconUtil.getMonitorIconPath(get(AKeySimple.OS_NAME), 16),
					"icons16/platform/monitors/" + get(AKeySimple.OS_FAMILY).toLowerCase() + ".png");
		}
		return monitorIcon16;
	}

	protected transient ImageIcon monitorIcon32;

	/**
	 * @return A 32px OS monitor icon
	 */
	public ImageIcon getMonitorIcon32() {
		if (monitorIcon32 == null) {
			// load icon or fallback
			monitorIcon32 = UIUtil.getIconOrFallback(IconUtil.getMonitorIconPath(get(AKeySimple.OS_NAME), 32),
					"icons32/platform/monitors/" + get(AKeySimple.OS_FAMILY).toLowerCase() + ".png");
		}
		return monitorIcon32;

	}

	protected transient ImageIcon locationIcon16;

	/**
	 * @return A 16px location icon
	 */
	public ImageIcon getLocationIcon16() {
		if (locationIcon16 == null && get(AKeySimple.NET_EXTERNALIP) != null) {

			if (ValidationUtil.privateIP(get(AKeySimple.NET_EXTERNALIP))) {
				locationIcon16 = UIUtil.getIcon("icons16/general/localhost.png");
				locationIcon16.setDescription("Private IP");
			} else {
				try {
					locationIcon16 = UIUtil
							.getIcon("icons16/flags/" + get(AKeySimple.IPLOC_COUNTRYCODE).toLowerCase() + ".png");
					locationIcon16.setDescription(get(AKeySimple.IPLOC_COUNTRY));
				} catch (NullPointerException e) {
					Reporter.report(Reporter.newReport()
							.setCrComment("No location icon found: " + get(AKeySimple.IPLOC_COUNTRYCODE).toLowerCase())
							.build());

					// fall back to default
					locationIcon16 = UIUtil.getIcon("icons16/flags/un.png");
					locationIcon16.setDescription("Unknown");
				}

			}
		}
		return locationIcon16;

	}

}
