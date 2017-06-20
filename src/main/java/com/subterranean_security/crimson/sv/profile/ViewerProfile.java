/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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

import java.util.Date;

import com.subterranean_security.crimson.core.attribute.TrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;

public class ViewerProfile extends Profile implements CVProfile, SVProfile {

	private static final long serialVersionUID = 1L;

	private ViewerPermissions permissions;

	public ViewerProfile(int cvid) {
		this();
		this.cvid = cvid;
	}

	public ViewerProfile() {
		super();
		this.permissions = new ViewerPermissions();
	}

	public ViewerPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(ViewerPermissions p) {
		this.permissions = p;
	}

	public String getLastLoginIp() {
		TrackedAttribute tr = (TrackedAttribute) getAttribute(AKeySimple.VIEWER_LOGIN_IP);
		if (tr.size() < 2) {
			return null;
		}
		return tr.getValue(tr.size() - 1);
	}

	public Date getLastLoginTime() {
		TrackedAttribute tr = (TrackedAttribute) getAttribute(AKeySimple.VIEWER_LOGIN_TIME);
		if (tr.size() < 2) {
			return null;
		}
		return tr.getTime(tr.size() - 1);
	}

	public void amalgamate(EV_ViewerProfileDelta c) {
		super.amalgamate(c.getPd());

		if (c.getViewerPermissionsCount() != 0) {
			// append new permissions, overwriting if necessary
			permissions.add(c.getViewerPermissionsList());
		}

	}

	public EV_ViewerProfileDelta gatherForServer(ViewerPermissions p) {
		EV_ViewerProfileDelta.Builder vpd = EV_ViewerProfileDelta.newBuilder()
				.addAllViewerPermissions(getPermissions().getFlags());

		AttributeGroupContainer.Builder general = AttributeGroupContainer.newBuilder()
				.putAttribute(AKeySimple.VIEWER_USER.getFullID(), get(AKeySimple.VIEWER_USER))
				.putAttribute(AKeySimple.VIEWER_LOGIN_IP.getFullID(),
						p == null || p.getFlag(Perm.Super) ? get(AKeySimple.VIEWER_LOGIN_IP) : "<hidden>")
				.putAttribute(AKeySimple.VIEWER_LOGIN_TIME.getFullID(),
						p == null || p.getFlag(Perm.Super) ? get(AKeySimple.VIEWER_LOGIN_IP) : "0");

		return vpd.setPd(EV_ProfileDelta.newBuilder().addGroup(general)).build();
	}

	public EV_ViewerProfileDelta getViewerUpdates(Date start) {
		return EV_ViewerProfileDelta.newBuilder().setPd(getUpdates(start)).build();
	}

}
