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
package com.subterranean_security.crimson.core.attribute.keys;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * An AttributeKey uniquely identifies a specific attribute. AttributeKeys can
 * be converted to and from encoded ints for use on the wire. This can be done
 * trivially for SingularKeys (because their group IDs are 0). PluralKeys
 * require an external group ID for conversion. Concrete implementations of this
 * interface conform to a naming standard (AK_*) for identifiability.
 * 
 * {@code
 * +---------+--------+---------+<br>
 * | GroupID | TypeID | ConstID |<br>
 * +---------+--------+---------+<br>
 * |           WireID           |<br>
 * +---------+--------+---------+<br>
 * |       GTID       |    0    |<br>
 * +---------+--------+---------+<br>
 * |    0    |       TCID       |<br>
 * +---------+--------+---------+<br>
 * }
 * 
 * @author cilki
 * @since 4.0.0
 */
public interface AttributeKey {

	public Object query();

	/**
	 * @return A nicely formatted name for this key
	 */
	public String toString();

	/**
	 * @return The enum constant as a String for this key
	 */
	public String toSuperString();

	/**
	 * Create a new attribute according to this key
	 * 
	 * @return A brand new attribute
	 */
	default public Attribute<Object> fabricate() {
		return new UntrackedAttribute<Object>();
	}

	default public int getWireID() {
		return (getGroupID() << (GROUP_ID_SPACE + TYPE_ID_SPACE)) + (getTypeID() << TYPE_ID_SPACE) + getConstID();
	}

	default public int getGTID() {
		return (getGroupID() << GROUP_ID_SPACE) + getTypeID();
	}

	public void setGroupID(int groupID);

	/**
	 * The group ID is the least specific of the three "attribute IDs". It
	 * identifies the group of which this key is a member. The maximum value of a
	 * group ID is 2^GROUP_ID_SPACE.
	 * 
	 * @return The group ID for this key
	 */
	public int getGroupID();

	public static final int GROUP_ID_SPACE = 20;

	/**
	 * The type ID is second to most specific of the three "attribute IDs". It
	 * identifies each concrete AttributeKey (enums prefixed with AK_). The maximum
	 * value of a type ID is 2^TYPE_ID_SPACE.
	 * 
	 * @return The type ID for this key
	 */
	public int getTypeID();

	public static final int TYPE_ID_SPACE = 6;

	/**
	 * The constant ID is the most specific of the three "attribute IDs". It
	 * identifies each enum constant in an AttributeKey. The maximum value of a
	 * constant ID is 2^CONST_ID_SPACE.
	 * 
	 * @return The constant ID for this key
	 */
	public int getConstID();

	public static final int CONST_ID_SPACE = 6;

	default public Class<?> getJavaType() {
		// most attributes are Strings
		return String.class;
	}

	/**
	 * A compatible attribute key is defined for a particular OS and Instance. Keys
	 * are compatible by default.
	 * 
	 * @param os
	 * @param instance
	 * @return True if compatible with the given platform and instance
	 */
	default public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	/**
	 * A headerable attribute key can be used as a header in a host-list or
	 * host-graph. Keys are headerable by default.
	 * 
	 * @return True if headerable, false otherwise
	 */
	default public boolean isHeaderable() {
		return true;
	}

	/**
	 * Plural attribute keys are used by multiple attribute groups rather than just
	 * one. For example, an instance may have more than one NIC. The attribute keys
	 * corresponding to properties of the NIC (like IP or MAC) would be plural
	 * attributes. Attributes like OS name must be singluar.
	 * 
	 * @return True if plural, false if singluar
	 */
	default public boolean isPlural() {
		return this instanceof PluralKey;
	}

	public static AttributeKey convert(int wireID) {
		AttributeKey key = (AttributeKey) TypeIndex.getType(extractTypeID(wireID))
				.getEnumConstants()[extractConstID(wireID)];
		key.setGroupID(extractGroupID(wireID));

		return key;
	}

	/**
	 * @param wireID
	 * @return The ConstID encoded in the WireID
	 */
	public static int extractConstID(int wireID) {
		return wireID & ((int) Math.pow(2, CONST_ID_SPACE) - 1);
	}

	/**
	 * @param wireID
	 * @return The TypeID encoded in the WireID
	 */
	public static int extractTypeID(int wireID) {
		return wireID & (((int) Math.pow(2, TYPE_ID_SPACE) - 1) << CONST_ID_SPACE);
	}

	/**
	 * @param wireID
	 * @return The GroupID encoded in the WireID
	 */
	public static int extractGroupID(int wireID) {
		return wireID >> (TYPE_ID_SPACE + CONST_ID_SPACE);
	}

	/**
	 * @param wireID
	 * @return The GTID encoded in the WireID
	 */
	public static int extractGroupTypeID(int wireID) {
		return wireID >> CONST_ID_SPACE;
	}

}
