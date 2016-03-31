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

import com.subterranean_security.crimson.core.proto.Delta.EV_ServerInfoDelta;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ui.utility.UIStore;

public class ServerProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int cvid = 0;
	private Date updateTimestamp = new Date();

	private boolean status;
	private int connectedUsers;
	private int connectedClients;

	public ArrayList<ListenerConfig> listeners = new ArrayList<ListenerConfig>();

	// User specific attributes
	private Attribute lastIp;
	private Date lastTime;

	// General attributes
	private Attribute messageLatency;

	// RAM attributes
	private Attribute crimsonRamUsage;

	// CPU attributes
	private Attribute cpuTemp;
	private Attribute crimsonCpuUsage;

	public ServerProfile() {
		lastIp = new UntrackedAttribute();
		messageLatency = new UntrackedAttribute();
		crimsonRamUsage = new UntrackedAttribute();
		cpuTemp = new UntrackedAttribute();
		crimsonCpuUsage = new UntrackedAttribute();
	}

	public int getCvid() {
		return cvid;
	}

	public String getCpuTemp() {
		return cpuTemp.get();
	}

	public void setCpuTemp(String cpuTemp) {
		this.cpuTemp.set(cpuTemp);
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

	public String getLastLoginIp() {
		return lastIp.get();
	}

	public Date getLastLoginTime() {
		return lastTime;
	}

	public void setLastLoginIp(String s) {
		lastIp.set(s);
	}

	public void setLastLoginTime(Date d) {
		lastTime = d;
	}

	public void amalgamate(EV_ServerInfoDelta c) {
		if (c.hasServerStatus()) {
			setStatus(c.getServerStatus());
		}
		if (c.hasClientCount()) {
			setConnectedClients(c.getClientCount());
		}
		if (c.hasUserCount()) {
			setConnectedUsers(c.getUserCount());
		}
		if (c.hasCpuTemp()) {
			setCpuTemp(c.getCpuTemp());
		}
		if (c.hasRamCrimsonUsage()) {
			setCrimsonRamUsage(CUtil.Misc.familiarize(c.getRamCrimsonUsage(), CUtil.Misc.BYTES));
		}
		if (c.hasCpuCrimsonUsage()) {
			setCrimsonCpuUsage(String.format("%.2f%%", c.getCpuCrimsonUsage()));
		}
		if (c.getListenersCount() != 0) {
			for (ListenerConfig lc : c.getListenersList()) {
				for (ListenerConfig l : listeners) {
					if (lc.getID() == l.getID()) {
						listeners.remove(l);
						break;
					}
				}
				if (UIStore.netMan != null) {
					UIStore.netMan.lp.lt.fireTableDataChanged();
				}

				listeners.add(lc);
			}
		}
		if (c.hasLastIp()) {
			setLastLoginIp(c.getLastIp());
		}
		if (c.hasLastLogin()) {
			setLastLoginTime(new Date(c.getLastLogin()));
		}

	}

}
