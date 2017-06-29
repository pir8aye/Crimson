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
package com.subterranean_security.crimson.sv.profile.set.filter;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.sv.profile.Profile;

/**
 * @author cilki
 * @since 5.0.0
 */
public class AttributeFilter implements ProfileFilter {

	private AttributeKey key;
	private Object value;

	public AttributeFilter(AttributeKey key, Object value) {
		if (key == null)
			throw new IllegalArgumentException();
		if (value == null)
			throw new IllegalArgumentException();

		this.key = key;
		this.value = value;
	}

	@Override
	public boolean check(Profile profile) {
		return value.equals(profile.getAttribute(key).get());
	}

}
