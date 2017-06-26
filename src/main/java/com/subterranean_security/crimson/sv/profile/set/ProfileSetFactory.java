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
package com.subterranean_security.crimson.sv.profile.set;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.universal.Universal.Instance;

public class ProfileSetFactory {
	public ProfileSetFactory() {
		instances = new HashSet<>();
	}

	private Set<Instance> instances;
	private boolean connected;

	/**
	 * Add an instance filter. The resulting {@code ProfileSet} will contain
	 * only profiles of the types in {@code instances}.
	 * 
	 * @param instances
	 *            Instances to search
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(Instance... instances) {
		this.instances.addAll(Arrays.asList(instances));
		return this;
	}

	/**
	 * Add an attribute filter. The resulting {@code ProfileSet} will contain
	 * only profiles which have an {@code attribute} equal to {@code value}
	 * 
	 * @param attribute
	 *            The attribute to target
	 * @param value
	 *            The required value
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(AttributeKey attribute, Object value) {

		return this;
	}

	/**
	 * Add a permission filter. The resulting {@code ProfileSet} will contain
	 * only viewer profiles which have the given {@code perm} set for a given
	 * client, {@code cid}
	 * 
	 * @param cid
	 * @param perm
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(int cid, short perm) {

		return this;
	}

	/**
	 * Add a connection filter.
	 * 
	 * @param connected
	 *            If true, the resulting ProfileSet will contain only connected
	 *            Profiles
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(boolean connected) {
		this.connected = connected;
		return this;
	}

	public ProfileSet build() {
		if (instances.isEmpty() || instances.contains(Instance.CLIENT)) {

		}
		if (instances.isEmpty() || instances.contains(Instance.VIEWER)) {

		}
		if (instances.isEmpty() || instances.contains(Instance.SERVER)) {

		}
		return null;
	}
}
