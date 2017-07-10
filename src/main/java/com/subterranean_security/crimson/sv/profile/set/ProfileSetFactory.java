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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.sv.profile.set.filter.AttributeFilter;
import com.subterranean_security.crimson.sv.profile.set.filter.PermissionFilter;
import com.subterranean_security.crimson.sv.profile.set.filter.ProfileFilter;
import com.subterranean_security.crimson.sv.store.ProfileStore;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * @author cilki
 * @since 5.0.0
 */
public class ProfileSetFactory {
	public ProfileSetFactory() {
		instances = new HashSet<>();
		filters = new ArrayList<>();
	}

	private Set<Instance> instances;
	private boolean connected;

	private List<ProfileFilter> filters;

	/**
	 * Add an instance filter. The resulting {@code ProfileSet} will contain only
	 * profiles of the types in {@code instances}.
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
	 * Add an attribute filter. The resulting {@code ProfileSet} will contain only
	 * profiles which have an {@code attribute} equal to {@code value}
	 * 
	 * @param attribute
	 *            The attribute to target
	 * @param value
	 *            The required value
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(AttributeKey attribute, Object value) {
		filters.add(new AttributeFilter(attribute, value));
		return this;
	}

	/**
	 * Add a permission filter. The resulting {@code ProfileSet} will contain only
	 * viewer profiles which have the given {@code perm} set for a given client,
	 * {@code cid}.
	 * 
	 * @param cid
	 * @param perm
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(int cid, short perm) {
		filters.add(new PermissionFilter(cid, perm));
		return this;
	}

	/**
	 * Add a connection filter. The resulting ProfileSet will contain only connected
	 * Profiles if {@code connected} is true.
	 * 
	 * @param connected
	 * @return this {@code ProfileSetFactory}
	 */
	public ProfileSetFactory addFilter(boolean connected) {
		this.connected = connected;
		return this;
	}

	private boolean checkFilters(Profile profile) {
		for (ProfileFilter pf : filters) {
			if (!pf.check(profile)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkInstance(Profile profile) {
		for (Instance instance : instances) {
			if (instance == profile.getInstance()) {
				return true;
			}
		}
		return instances.isEmpty();
	}

	public ProfileSet build() {
		ProfileSet set = new ProfileSet();

		if (connected) {
			// use ConnectionStore
			for (int cvid : ConnectionStore.getKeySet()) {
				Profile profile = ProfileStore.getProfile(cvid);
				if (checkInstance(profile) && checkFilters(profile)) {
					set.add(profile);
				}
			}
		} else {
			// use ProfileStore
			if (instances.isEmpty() || instances.contains(Instance.CLIENT)) {
				for (ClientProfile cp : ProfileStore.getClients()) {
					if (checkFilters(cp)) {
						set.add(cp);
					}
				}
			}
			if (instances.isEmpty() || instances.contains(Instance.VIEWER)) {
				for (ViewerProfile vp : ProfileStore.getViewers()) {
					if (checkFilters(vp)) {
						set.add(vp);
					}
				}
			}
			if (instances.isEmpty() || instances.contains(Instance.SERVER)) {
				// TODO
			}
		}

		return set;
	}
}
