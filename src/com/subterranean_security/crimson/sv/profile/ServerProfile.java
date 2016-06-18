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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.subterranean_security.crimson.core.proto.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
import com.subterranean_security.crimson.sv.profile.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.viewer.ui.UIStore;

public class ServerProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int cvid = 0;
	private Date updateTimestamp = new Date();

	private boolean status;
	private int connectedUsers;
	private int connectedClients;

	public ArrayList<ListenerConfig> listeners = new ArrayList<ListenerConfig>();
	public ArrayList<AuthMethod> authMethods = new ArrayList<AuthMethod>();
	public ArrayList<ViewerProfile> users = new ArrayList<ViewerProfile>();

	// General attributes
	private Attribute messageLatency;

	// RAM attributes
	private Attribute crimsonRamUsage;

	// CPU attributes
	private ArrayList<Double> cpuTemp;
	private Attribute crimsonCpuUsage;

	public ServerProfile() {

		messageLatency = new UntrackedAttribute();
		crimsonRamUsage = new UntrackedAttribute();
		cpuTemp = new ArrayList<Double>();
		crimsonCpuUsage = new UntrackedAttribute();
	}

	public int getCvid() {
		return cvid;
	}

	public String getCpuTempAverage() {
		return CUtil.Misc.average(cpuTemp) + " C";
	}

	public ArrayList<Double> getCpuTemps() {
		return cpuTemp;
	}

	public void setCpuTemp(List<Double> l) {
		this.cpuTemp.clear();
		this.cpuTemp.addAll(l);
	}

	public String getMessageLatency() {
		return messageLatency.get();
	}

	public void setMessageLatency(String messageLatency) {
		this.messageLatency.set(messageLatency);
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public String getCrimsonRamUsage() {
		return crimsonRamUsage.get();
	}

	public void setCrimsonRamUsage(String crimsonRamUsage) {
		this.crimsonRamUsage.set(crimsonRamUsage);
	}

	public String getCrimsonCpuUsage() {
		return crimsonCpuUsage.get();
	}

	public void setCrimsonCpuUsage(String crimsonCpuUsage) {
		this.crimsonCpuUsage.set(crimsonCpuUsage);
	}

	public int getConnectedClients() {
		return connectedClients;
	}

	public void setConnectedClients(int connectedClients) {
		this.connectedClients = connectedClients;
	}

	public int getConnectedUsers() {
		return connectedUsers;
	}

	public void setConnectedUsers(int connectedUsers) {
		this.connectedUsers = connectedUsers;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void amalgamate(EV_ServerProfileDelta c) {
		if (c.hasServerStatus()) {
			setStatus(c.getServerStatus());
		}
		if (c.hasClientCount()) {
			setConnectedClients(c.getClientCount());
		}
		if (c.hasUserCount()) {
			setConnectedUsers(c.getUserCount());
		}
		if (c.getCpuTempCount() != 0) {
			setCpuTemp(c.getCpuTempList());
		}
		if (c.hasRamCrimsonUsage()) {
			setCrimsonRamUsage(CUtil.Misc.familiarize(c.getRamCrimsonUsage(), CUtil.Misc.BYTES));
		}
		if (c.hasCpuCrimsonUsage()) {
			setCrimsonCpuUsage(String.format("%.2f%%", c.getCpuCrimsonUsage()));
		}

		for (ListenerConfig lc : c.getListenerList()) {
			for (ListenerConfig l : listeners) {
				if (lc.getId() == l.getId()) {
					listeners.remove(l);
					break;
				}
			}

			listeners.add(lc);
		}
		if (UIStore.netMan != null) {
			UIStore.netMan.lp.lt.fireTableDataChanged();
		}

		for (EV_ViewerProfileDelta lc : c.getViewerUserList()) {
			boolean modified = false;
			for (ViewerProfile l : users) {
				if (lc.getUser().equals(l.getUser())) {
					modified = true;
					l.amalgamate(lc);
					break;
				}
			}
			if (!modified) {

				ViewerProfile vp = new ViewerProfile();
				vp.amalgamate(lc);
				users.add(vp);
			}

		}
		for (AuthMethod am : c.getAuthMethodList()) {
			boolean modified = false;
			for (AuthMethod a : authMethods) {
				if (a.getId() == am.getId()) {
					a = AuthMethod.newBuilder().mergeFrom(a).mergeFrom(am).build();
					modified = true;
				}
			}
			if (!modified) {
				System.out.println("Adding new auth method");
				authMethods.add(am);
			}
		}

		if (UIStore.userMan != null) {
			UIStore.userMan.up.ut.fireTableDataChanged();
		}

	}

}
