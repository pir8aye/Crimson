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
package com.subterranean_security.crimson.sv;

import java.io.Serializable;
import java.util.Date;

import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.ViewerPermissions;

public class ViewerProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private int cvid;
	private Date updateTimestamp = new Date();

	private Attribute user;
	private Attribute ip;
	private ViewerPermissions permissions;

	public ViewerProfile(int cvid) {
		this();
		this.cvid = cvid;
	}

	public ViewerProfile() {
		ip = new TrackedAttribute();
		user = new UntrackedAttribute();
		permissions = ViewerPermissions.newBuilder().build();
	}

	public ViewerPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(ViewerPermissions p) {
		permissions = p;
	}

	public Integer getCvid() {
		return cvid;
	}

	public void setCvid(int cvid) {
		this.cvid = cvid;
	}

	public String getUser() {
		return user.get();
	}

	public void setUser(String user) {
		this.user.set(user);
	}

	public String getIp() {
		return ip.get();
	}

	public void setIp(String ip) {
		((TrackedAttribute) this.ip).set(ip);
	}

	public String getLastLoginIp() {
		TrackedAttribute tr = (TrackedAttribute) ip;
		if (tr.size() < 2) {
			return null;
		}
		return tr.getValue(tr.size() - 1);
	}

	public Date getLastLoginTime() {
		TrackedAttribute tr = (TrackedAttribute) ip;
		if (tr.size() < 2) {
			return null;
		}
		return tr.getTime(tr.size() - 1);
	}

	public void amalgamate(EV_ViewerProfileDelta c) {
		updateTimestamp = new Date();

		if (c.hasUser()) {
			setUser(c.getUser());
		}

		if (c.hasLastIp() && c.hasLastLogin()) {
			((TrackedAttribute) ip).set(c.getLastIp(), new Date(c.getLastLogin()));
		}

		if (c.hasIp()) {
			setIp(c.getIp());
		}
		if (c.hasViewerPermissions()) {
			permissions = c.getViewerPermissions();
		}

	}

}
