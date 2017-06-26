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
package com.subterranean_security.crimson.core.attribute;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;

public class AttributeGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * All Attribute Groups start out modern
	 */
	private boolean modern = true;

	/**
	 * Maps AttributeKeys to Attributes
	 */
	private Map<AttributeKey, Attribute> attributes;

	private int groupType;
	private String groupID;

	public AttributeGroup(int groupType, String groupID) {
		this.groupType = groupType;
		this.groupID = groupID;

		attributes = new HashMap<AttributeKey, Attribute>();
	}

	public AttributeGroup(int groupType, String groupID, int suggestedSize) {
		this.groupType = groupType;
		this.groupID = groupID;

		attributes = new HashMap<AttributeKey, Attribute>(suggestedSize + 1, 1.0f);
	}

	public boolean isModern() {
		return modern;
	}

	public void setModern(boolean modern) {
		this.modern = modern;
	}

	public boolean hasAttribute(AttributeKey key) {
		return attributes.containsKey(key);
	}

	public String get(AttributeKey key) {
		return getAttribute(key).get();
	}

	public void set(AttributeKey key, String value) {
		getAttribute(key).set(value);
	}

	public Attribute getAttribute(AttributeKey key) {
		if (!hasAttribute(key))
			addAttribute(key, key.fabricate());

		return attributes.get(key);
	}

	public void addAttribute(AttributeKey key, Attribute attribute) {
		attributes.put(key, attribute);
	}

	/**
	 * Gets the updated attributes by checking the timestamp of every present
	 * attribute. This method could be improved by using a parallel SortedSet
	 * sorted by timestamps, but this method is also called rarely.
	 * 
	 * @param start
	 * @return
	 */
	public AttributeGroupContainer getUpdated(Date start) {
		AttributeGroupContainer.Builder container = AttributeGroupContainer.newBuilder().setGroupType(groupType)
				.setGroupId(groupID);

		for (AttributeKey key : attributes.keySet()) {
			Attribute a = attributes.get(key);
			if (a.getTimestamp().after(start)) {
				container.putAttribute(key.getFullID(), a.get());
			}
		}
		return container.build();
	}

	public void absorb(AttributeGroupContainer container) {
		setModern(true);
		Map<Integer, String> attr = container.getAttributeMap();
		for (Integer keyID : attr.keySet()) {
			AttributeKey key = AttributeKey.getKey(keyID);
			if (!hasAttribute(key)) {
				addAttribute(key, key.fabricate());
			}
			getAttribute(key).set(attr.get(keyID));
		}

	}

	/**
	 * Get the underlying storage container for this AttributeGroup
	 * 
	 * @return
	 */
	public Map<AttributeKey, Attribute> getAttributeMap() {
		return null;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}

}
