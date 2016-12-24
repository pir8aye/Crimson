package com.subterranean_security.crimson.core.profile.group;

import java.io.Serializable;
import java.util.HashMap;

import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
import com.subterranean_security.crimson.sv.profile.attribute.UntrackedAttribute;

public class AttributeGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * All Attribute Groups start out modern
	 */
	private boolean modern = true;

	private HashMap<AttributeGroupType, Attribute> attributes;

	public AttributeGroup() {
		attributes = new HashMap<AttributeGroupType, Attribute>();
	}

	public boolean isModern() {
		return modern;
	}

	public void setModern(boolean modern) {
		this.modern = modern;
	}

	public boolean hasAttribute(AttributeGroupType agt) {
		return attributes.containsKey(agt);
	}

	public boolean hasAttribute(int agt) {
		return hasAttribute(AttributeGroupType.ordinal[agt]);
	}

	public Attribute queryAttribute(AttributeGroupType agt) {
		return attributes.get(agt);
	}

	public Attribute queryAttribute(int agt) {
		return queryAttribute(AttributeGroupType.ordinal[agt]);
	}

	public void addAttribute(AttributeGroupType agt) {
		attributes.put(agt, new UntrackedAttribute());
	}

	public void addAttribute(int agt) {
		addAttribute(AttributeGroupType.ordinal[agt]);
	}

	public HashMap<AttributeGroupType, Attribute> getAttributeMap() {
		return attributes;
	}

}
