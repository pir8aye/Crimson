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
package com.subterranean_security.crimson.core.attribute.group;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.misc.Updatable;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;

public class AttributeGroup extends Updatable implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * All Attribute Groups start out modern
	 */
	private boolean modern = true;

	/**
	 * Maps AttributeKeys to Attributes
	 */
	private Map<AttributeKey, Attribute<Object>> attributes;

	public AttributeGroup() {

		attributes = new HashMap<AttributeKey, Attribute<Object>>();
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

	public String getStr(AttributeKey key) {
		return (String) getAttribute(key).get();
	}

	public int getInt(AttributeKey key) {
		return (int) getAttribute(key).get();
	}

	public void set(AttributeKey key, Object value) {
		getAttribute(key).set(value);
	}

	public Attribute<Object> getAttribute(AttributeKey key) {
		if (!hasAttribute(key))
			addAttribute(key, key.fabricate());

		return attributes.get(key);
	}

	public void addAttribute(AttributeKey key, Attribute<Object> attribute) {
		attributes.put(key, attribute);
	}

	@Override
	public String toString() {
		return attributes.toString();
	}

	@Override
	// Could be improved by sorting
	public Object getUpdates(long time) {
		PDFactory pd = new PDFactory();

		for (AttributeKey key : attributes.keySet()) {
			Attribute<Object> a = attributes.get(key);
			if (a.getTimestamp() > time) {
				pd.add(key, a.get());
			}
		}
		return pd;
	}

	@Override
	public void merge(Object updates) {
		// TODO Auto-generated method stub

	}

}
