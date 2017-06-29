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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.misc.Updatable;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.util.IconUtil;

/**
 * A {@code Profile} is a generic container for the attributes of some distinct
 * entity.
 * 
 * @author cilki
 * @since 4.0.0
 */
public abstract class Profile extends Updatable implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<Integer, AttributeGroup> groups;

	public Profile() {
		updated();

		// initialize attribute groups
		groups = new TreeMap<>();

	}

	/**
	 * Get every group of {@code type}
	 * 
	 * @param type
	 * @return
	 */
	public List<AttributeGroup> getGroupsOfType(TypeIndex type) {
		List<AttributeGroup> list = new ArrayList<AttributeGroup>();
		for (int gtid : groups.keySet()) {
			if ((gtid & ((int) Math.pow(2, AttributeKey.TYPE_ID_SPACE) - 1)) == type.ordinal()) {
				list.add(groups.get(gtid));
			}
		}
		return list;
	}

	public Object getObject(AttributeKey key) {
		return getAttribute(key).get();
	}

	/**
	 * Get the value of the specified String {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public String get(AttributeKey key) {
		return (String) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified boolean {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBool(AttributeKey key) {
		return (boolean) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified int {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(AttributeKey key) {
		return (int) getAttribute(key).get();
	}

	/**
	 * Get the value of the specified long {@code Attribute}
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(AttributeKey key) {
		return (long) getAttribute(key).get();
	}

	public void set(AttributeKey key, Object value) {
		getAttribute(key).set(value);

		setChanged();
		notifyObservers(key);
	}

	/**
	 * Get an {@code Attribute} according to its Group/Type ID
	 * 
	 * @param GTID
	 * @return
	 */
	public Attribute<Object> getAttribute(AttributeKey key) {
		int gtid = key.getGTID();

		if (!groups.containsKey(gtid)) {
			groups.put(gtid, new AttributeGroup());
		}
		return groups.get(gtid).getAttribute(key);
	}

	public void merge(EV_ProfileDelta pd) {
		if (pd.getBooleanAttrCount() > 0)
			merge(pd.getBooleanAttrMap());
		if (pd.getStrAttrCount() > 0)
			merge(pd.getStrAttrMap());
		if (pd.getIntAttrCount() > 0)
			merge(pd.getIntAttrMap());
		if (pd.getLongAttrCount() > 0)
			merge(pd.getLongAttrMap());
	}

	private void merge(Map<Integer, ?> map) {
		for (int wireID : map.keySet()) {
			set(AttributeKey.convert(wireID), map.get(wireID));
		}
	}

	@Override
	public EV_ProfileDelta getUpdates(long start) {
		PDFactory pd = new PDFactory(getCvid());

		for (AttributeGroup group : groups.values()) {

		}

		return pd.buildPd();

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

	public void setOnline(boolean online) {
		set(AK_META.ONLINE, online);
	}

	/**
	 * @return True if the instance is connected to the server (online)
	 */
	public boolean isOnline() {
		return getBool(AK_META.ONLINE);
	}

	public OSFAMILY getOSFamily() {
		return OSFAMILY.valueOf(get(AK_OS.FAMILY).toUpperCase());
	}

	public abstract Instance getInstance();

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
