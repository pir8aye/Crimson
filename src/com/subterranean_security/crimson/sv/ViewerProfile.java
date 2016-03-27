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
import java.util.ArrayList;
import java.util.Date;

import com.subterranean_security.crimson.sv.permissions.Permissions;

public class ViewerProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private String user;
	private int cvid;
	private Date updateTimestamp = new Date();
	private ArrayList<Date> login_times = new ArrayList<Date>();
	private ArrayList<String> login_ip = new ArrayList<String>();
	private Permissions permissions = new Permissions();

	public ViewerProfile(int cvid) {
		this.cvid = cvid;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public Integer getCvid() {
		return cvid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ArrayList<Date> getLogin_times() {
		return login_times;
	}

	public ArrayList<String> getLogin_ip() {
		return login_ip;
	}

}
