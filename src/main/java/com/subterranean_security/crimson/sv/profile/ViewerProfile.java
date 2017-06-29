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
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * @author cilki
 * @since 4.0.0
 */
public class ViewerProfile extends Profile {

	private static final long serialVersionUID = 1L;

	public ViewerProfile(int cvid) {
		this();
		setCvid(cvid);
	}

	public ViewerProfile() {
		super();
	}

	public ViewerPermissions getPermissions() {
		return (ViewerPermissions) getObject(AK_VIEWER.PERMISSIONS);
	}

	public void setPermissions(ViewerPermissions permissions) {
		set(AK_VIEWER.PERMISSIONS, permissions);
	}

	public String getLastLoginIp() {
		TrackedAttribute<String> tr = (TrackedAttribute) getAttribute(AK_VIEWER.LOGIN_IP);
		if (tr.size() < 2) {
			return null;
		}
		return tr.getValue(tr.size() - 1);
	}

	public long getLastLoginTime() {
		TrackedAttribute<String> tr = (TrackedAttribute) getAttribute(AK_VIEWER.LOGIN_TIME);
		if (tr.size() < 2) {
			return 0;
		}
		return tr.getTime(tr.size() - 1);
	}

	@Override
	public void merge(Object updates) {
		merge((EV_ViewerProfileDelta) updates);
	}

	public EV_ViewerProfileDelta gatherForServer(ViewerPermissions p) {
		EV_ViewerProfileDelta.Builder vpd = EV_ViewerProfileDelta.newBuilder()
				.addAllViewerPermissions(getPermissions().getFlags());

		AttributeGroupContainer.Builder general = AttributeGroupContainer.newBuilder()
				.putAttribute(AKeySimple.VIEWER_USER.getWireID(), getStr(AKeySimple.VIEWER_USER))
				.putAttribute(AKeySimple.VIEWER_LOGIN_IP.getWireID(),
						p == null || p.getFlag(Perm.Super) ? getStr(AKeySimple.VIEWER_LOGIN_IP) : "<hidden>")
				.putAttribute(AKeySimple.VIEWER_LOGIN_TIME.getWireID(),
						p == null || p.getFlag(Perm.Super) ? getStr(AKeySimple.VIEWER_LOGIN_IP) : "0");

		return vpd.setPd(EV_ProfileDelta.newBuilder().addGroup(general)).build();
	}

	@Override
	public Instance getInstance() {
		return Instance.VIEWER;
	}
}
