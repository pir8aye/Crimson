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
import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.util.IconUtil;

/**
 * A {@code Profile} is a generic container for the attributes of some distinct
 * entity.
 * 
 * @author cilki
 * @since 4.0.0
 */
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
	 * Get the value of the specified singular String {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public String get(SingularKey key) {
		return (String) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified singular boolean {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBool(SingularKey key) {
		return (boolean) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified singular int {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(SingularKey key) {
		return (int) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified singular long {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(SingularKey key) {
		return (long) getAttribute(key).get();
	}

	public void set(SingularKey key, Object value) {
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
	public Attribute<Object> getAttribute(SingularKey key) {
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

	/**
	 * @return The instance's CVID
	 */
	public int getCvid() {
		return getInt(AK_META.CVID);
	}

	public void setCvid(int cvid) {
		set(AK_META.CVID, cvid);
	}

	/**
	 * @return True if the instance is connected to the server (online)
	 */
	public boolean getOnline() {
		return getBool(AK_META.ONLINE);
	}

	protected transient ImageIcon osIcon16;

	/**
	 * @return A 16px OS icon
	 */
	public ImageIcon getOsIcon16() {
		if (osIcon16 == null) {
			// load icon or fallback
			osIcon16 = UIUtil.getIconOrFallback(IconUtil.getOsIconPath(get(AK_OS.NAME), 16),
					"icons16/platform/" + get(AK_OS.FAMILY).toLowerCase() + ".png");
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
			monitorIcon16 = UIUtil.getIconOrFallback(IconUtil.getMonitorIconPath(get(AK_OS.NAME), 16),
					"icons16/platform/monitors/" + get(AK_OS.FAMILY).toLowerCase() + ".png");
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
			monitorIcon32 = UIUtil.getIconOrFallback(IconUtil.getMonitorIconPath(get(AK_OS.NAME), 32),
					"icons32/platform/monitors/" + get(AK_OS.FAMILY).toLowerCase() + ".png");
		}
		return monitorIcon32;

	}

	protected transient ImageIcon locationIcon16;

	/**
	 * @return A 16px location icon
	 */
	public ImageIcon getLocationIcon16() {
		if (locationIcon16 == null && get(AK_NET.EXTERNAL_IPV4) != null) {

			if (ValidationUtil.privateIP(get(AK_NET.EXTERNAL_IPV4))) {
				locationIcon16 = UIUtil.getIcon("icons16/general/localhost.png");
				locationIcon16.setDescription("Private IP");
			} else {
				try {
					locationIcon16 = UIUtil
							.getIcon("icons16/flags/" + get(AK_LOC.IPLOC_COUNTRYCODE).toLowerCase() + ".png");
					locationIcon16.setDescription(get(AK_LOC.IPLOC_COUNTRY));
				} catch (NullPointerException e) {
					Reporter.report(Reporter.newReport()
							.setCrComment("No location icon found: " + get(AK_LOC.IPLOC_COUNTRYCODE).toLowerCase())
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
