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

	private String lastIp;
	private Date lastTime;

	private String cpuModel;
	private String cpuTemp;
	private String cpuUsage;
	private String crimsonVersion;
	private String hostname;
	private String javaVersion;
	private String language;
	private String messagePing;
	private String osArch;
	private String osFamily;
	private String ramCapacity;
	private String ramUsage;
	private String timezone;
	private String crimsonRamUsage;
	private String crimsonCpuUsage;

	// historical attributes
	private ArrayList<String> internal_ip = new ArrayList<String>();
	private ArrayList<String> external_ip = new ArrayList<String>();

	public ServerProfile() {
	}

	public int getCvid() {
		return cvid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCpuModel() {
		return cpuModel;
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel = cpuModel;
	}

	public String getCpuTemp() {
		return cpuTemp;
	}

	public void setCpuTemp(String cpuTemp) {
		this.cpuTemp = cpuTemp;
	}

	public String getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(String cpu_usage) {
		this.cpuUsage = cpu_usage;
	}

	public String getCrimsonVersion() {
		return crimsonVersion;
	}

	public void setCrimsonVersion(String crimsonVersion) {
		this.crimsonVersion = crimsonVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getMessagePing() {
		return messagePing;
	}

	public void setMessagePing(String messagePing) {
		this.messagePing = messagePing;
	}

	public String getOsArch() {
		return osArch;
	}

	public void setOsArch(String osArch) {
		this.osArch = osArch;
	}

	public String getOsFamily() {
		return osFamily;
	}

	public void setOsFamily(String osFamily) {
		this.osFamily = osFamily;
	}

	public String getRamCapacity() {
		return ramCapacity;
	}

	public void setRamCapacity(String ramCapacity) {
		this.ramCapacity = ramCapacity;
	}

	public String getRamUsage() {
		return ramUsage;
	}

	public void setRamUsage(String ramUsage) {
		this.ramUsage = ramUsage;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public String getCrimsonRamUsage() {
		return crimsonRamUsage;
	}

	public void setCrimsonRamUsage(String crimsonRamUsage) {
		this.crimsonRamUsage = crimsonRamUsage;
	}

	public String getCrimsonCpuUsage() {
		return crimsonCpuUsage;
	}

	public void setCrimsonCpuUsage(String crimsonCpuUsage) {
		this.crimsonCpuUsage = crimsonCpuUsage;
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
		return lastIp;
	}

	public Date getLastLoginTime() {
		return lastTime;
	}

	public void setLastLoginIp(String s) {
		lastIp = s;
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
		if (c.hasCpuModel()) {
			setCpuModel(c.getCpuModel());
		}
		if (c.hasCpuTemp()) {
			setCpuTemp(c.getCpuTemp());
		}
		if (c.hasCrimsonVersion()) {
			setCrimsonVersion(c.getCrimsonVersion());
		}
		if (c.hasNetHostname()) {
			setHostname(c.getNetHostname());
		}
		if (c.hasJavaVersion()) {
			setJavaVersion(c.getJavaVersion());
		}
		if (c.hasOsFamily()) {
			setOsFamily(c.getOsFamily());
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
					if (lc.getID().equals(l.getID())) {
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
