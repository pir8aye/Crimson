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
package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;

/**
 * Viewer attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_VIEWER implements SingularKey {
	LOGIN_IP, LOGIN_TIME, USER, PERMISSIONS;

	@Override
	public Attribute fabricate() {
		switch (this) {
		case LOGIN_IP:
		case LOGIN_TIME:
			return new TrackedAttribute();
		default:
			return SingularKey.super.fabricate();

		}
	}

	@Override
	public String toString() {
		switch (this) {
		}
		return super.toString();
	}

	@Override
	public String toSuperString() {
		return super.toString();
	}

	@Override
	public int getConstID() {
		return this.ordinal();
	}

	@Override
	public int getTypeID() {
		return TypeIndex.VIEWER.ordinal();
	}

	@Override
	public Object query() {
		switch (this) {
		case LOGIN_IP:
			break;
		case LOGIN_TIME:
			break;
		case USER:
			break;
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
		return null;
	}
}
