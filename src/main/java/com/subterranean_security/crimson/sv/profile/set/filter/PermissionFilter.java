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

import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

/**
 * @author cilki
 * @since 5.0.0
 */
public class PermissionFilter implements ProfileFilter {

	private int cid;
	private short permission;

	public PermissionFilter(int cid, short permission) {
		this.cid = cid;
		this.permission = permission;
	}

	@Override
	public boolean check(Profile profile) {
		if (profile instanceof ViewerProfile) {
			return ((ViewerProfile) profile).getPermissions().getFlag(cid, permission);
		}
		return false;
	}

}
