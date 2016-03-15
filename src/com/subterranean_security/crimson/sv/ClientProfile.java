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

import com.subterranean_security.crimson.core.proto.net.Keylogger.KLog;

public class ClientProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private int clientid;

	private String hostname;
	private ArrayList<String> username = new ArrayList<String>();
	private ArrayList<Date> username_dates = new ArrayList<Date>();
	private String timezone;
	private String language;
	private ArrayList<String> internal_ip = new ArrayList<String>();
	private ArrayList<String> external_ip = new ArrayList<String>();
	private float cpu_usage;

	private KLog klog;

	public ClientProfile(int clientid) {
		this.clientid = clientid;
	}

	public int getClientid() {
		return clientid;
	}

	public void setClientid(int clientid) {
		this.clientid = clientid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username.get(username.size() - 1);
	}

	public void setUsername(String username) {
		setUsername(new Date(), username);
	}

	public void setUsername(Date date, String username) {
		this.username.add(username);
		this.username_dates.add(date);
	}

}
