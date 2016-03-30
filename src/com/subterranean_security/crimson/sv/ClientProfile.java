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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.xml.stream.XMLStreamException;

import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.NetworkInterface;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class ClientProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private int cvid;

	// Transient attributes
	private transient ImageIcon locationIcon;
	private transient ImageIcon osIcon;

	// General attributes
	private Attribute osFamily;
	private Attribute osName;
	private Attribute osArch;
	private Attribute javaArch;
	private Attribute javaVersion;
	private Attribute javaVendor;
	private Attribute crimsonVersion;
	private Attribute timezone;
	private Attribute language;
	private Attribute username;
	private Attribute userStatus;
	private Attribute userHome;
	private Attribute activeWindow;
	private Attribute virtualization;
	private Attribute messageLatency;

	// RAM attributes
	private Attribute systemRamCapacity;
	private Attribute systemRamUsage;
	private Attribute crimsonRamUsage;

	// CPU attributes
	private Attribute cpuModel;
	private Attribute cpuCache;
	private Attribute cpuTemp;
	private Attribute crimsonCpuUsage;
	private ArrayList<Double> coreSpeeds;
	private ArrayList<Double> coreUsages;

	// Network attributes
	private Attribute hostname;
	private Attribute extIp;
	private Attribute dns1;
	private Attribute dns2;
	private Attribute fqdn;
	private ArrayList<NetworkInterface> interfaces;

	// Location attributes
	private Attribute latitude;
	private Attribute longitude;
	private Attribute country;
	private Attribute countryCode;
	private Attribute region;
	private Attribute city;

	public ClientProfile(int cvid) {
		this();
		this.cvid = cvid;
	}

	public ClientProfile() {
		osFamily = new UntrackedAttribute();
		osName = new UntrackedAttribute();
		osArch = new UntrackedAttribute();
		javaArch = new UntrackedAttribute();
		javaVersion = new UntrackedAttribute();
		javaVendor = new UntrackedAttribute();
		crimsonVersion = new UntrackedAttribute();
		timezone = new UntrackedAttribute();
		language = new UntrackedAttribute();
		username = new UntrackedAttribute();
		userStatus = new UntrackedAttribute();
		userHome = new UntrackedAttribute();
		activeWindow = new UntrackedAttribute();
		virtualization = new UntrackedAttribute();
		messageLatency = new UntrackedAttribute();
		systemRamCapacity = new UntrackedAttribute();
		systemRamUsage = new UntrackedAttribute();
		crimsonRamUsage = new UntrackedAttribute();
		cpuModel = new UntrackedAttribute();
		cpuCache = new UntrackedAttribute();
		cpuTemp = new UntrackedAttribute();
		crimsonCpuUsage = new UntrackedAttribute();
		// core_speeds
		// core_temps
		hostname = new UntrackedAttribute();
		extIp = new UntrackedAttribute();
		dns1 = new UntrackedAttribute();
		dns2 = new UntrackedAttribute();
		fqdn = new UntrackedAttribute();
		interfaces = new ArrayList<NetworkInterface>();
		latitude = new UntrackedAttribute();
		longitude = new UntrackedAttribute();
		country = new UntrackedAttribute();
		countryCode = new UntrackedAttribute();
		region = new UntrackedAttribute();
		city = new UntrackedAttribute();
	}

	public void loadTransientAttributes() {
		if (countryCode.get() != null && country.get() != null) {
			locationIcon = UUtil.getIcon("flags/" + countryCode.get() + ".png");
			locationIcon.setDescription(country.get());
		}
		if (osName.get() != null) {
			osIcon = UUtil.getIcon("platform/" + osName.get().replaceAll(" ", "_").toLowerCase() + ".png");
			osIcon.setDescription(osName.get());
		}

	}

	public String getOsFamily() {
		return osFamily.get();
	}

	public void setOsFamily(String osFamily) {
		this.osFamily.set(osFamily);
	}

	public String getOsName() {
		return osName.get();
	}

	public void setOsName(String osName) {
		this.osName.set(osName);
	}

	public String getOsArch() {
		return osArch.get();
	}

	public void setOsArch(String osArch) {
		this.osArch.set(osArch);
	}

	public String getJavaArch() {
		return javaArch.get();
	}

	public void setJavaArch(String javaArch) {
		this.javaArch.set(javaArch);
	}

	public String getJavaVersion() {
		return javaVersion.get();
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion.set(javaVersion);
	}

	public String getJavaVendor() {
		return javaVendor.get();
	}

	public void setJavaVendor(String javaVendor) {
		this.javaVendor.set(javaVendor);
	}

	public String getCrimsonVersion() {
		return crimsonVersion.get();
	}

	public void setCrimsonVersion(String crimsonVersion) {
		this.crimsonVersion.set(crimsonVersion);
	}

	public String getTimezone() {
		return timezone.get();
	}

	public void setTimezone(String timezone) {
		this.timezone.set(timezone);
	}

	public String getLanguage() {
		return language.get();
	}

	public void setLanguage(String language) {
		this.language.set(new Locale(language).getDisplayName());
	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getUserStatus() {
		return userStatus.get();
	}

	public void setUserStatus(String userStatus) {
		this.userStatus.set(userStatus);
	}

	public String getUserHome() {
		return userHome.get();
	}

	public void setUserHome(String userHome) {
		this.userHome.set(userHome);
	}

	public String getActiveWindow() {
		return activeWindow.get();
	}

	public void setActiveWindow(String activeWindow) {
		this.activeWindow.set(activeWindow);
	}

	public String getVirtualization() {
		return virtualization.get();
	}

	public void setVirtualization(String virtualization) {
		this.virtualization.set(virtualization);
	}

	public String getMessageLatency() {
		return messageLatency.get();
	}

	public void setMessageLatency(String messageLatency) {
		this.messageLatency.set(messageLatency);
	}

	public String getSystemRamCapacity() {
		return systemRamCapacity.get();
	}

	public void setSystemRamCapacity(String ramCapacity) {
		this.systemRamCapacity.set(ramCapacity);
	}

	public String getSystemRamUsage() {
		return systemRamUsage.get();
	}

	public void setSystemRamUsage(String ramUsage) {
		this.systemRamUsage.set(ramUsage);
	}

	public String getCrimsonRamUsage() {
		return crimsonRamUsage.get();
	}

	public void setCrimsonRamUsage(String crimsonRamUsage) {
		this.crimsonRamUsage.set(crimsonRamUsage);
	}

	public String getCpuModel() {
		return cpuModel.get();
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel.set(cpuModel);
	}

	public String getCpuCache() {
		return cpuCache.get();
	}

	public void setCpuCache(String cpuCache) {
		this.cpuCache.set(cpuCache);
	}

	public String getCpuTemp() {
		return cpuTemp.get();
	}

	public void setCpuTemp(String cpuTemp) {
		this.cpuTemp.set(cpuTemp);
	}

	public String getCrimsonCpuUsage() {
		return crimsonCpuUsage.get();
	}

	public void setCrimsonCpuUsage(String crimsonCpuUsage) {
		this.crimsonCpuUsage.set(crimsonCpuUsage);
	}

	public ArrayList<Double> getCoreSpeeds() {
		return coreSpeeds;
	}

	public void setCoreSpeeds(ArrayList<Double> coreSpeeds) {
		this.coreSpeeds = coreSpeeds;
	}

	public ArrayList<Double> getCoreUsages() {
		return coreUsages;
	}

	public void setCoreUsages(ArrayList<Double> coreUsages) {
		this.coreUsages = coreUsages;
	}

	public String getHostname() {
		return hostname.get();
	}

	public void setHostname(String hostname) {
		this.hostname.set(hostname);
	}

	public String getExtIp() {
		return extIp.get();
	}

	public void setExtIp(String extIp) {
		this.extIp.set(extIp);
	}

	public String getDns1() {
		return dns1.get();
	}

	public void setDns1(String dns1) {
		this.dns1.set(dns1);
	}

	public String getDns2() {
		return dns2.get();
	}

	public void setDns2(String dns2) {
		this.dns2.set(dns2);
	}

	public String getFqdn() {
		return fqdn.get();
	}

	public void setFqdn(String fqdn) {
		this.fqdn.set(fqdn);
	}

	public ArrayList<NetworkInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<NetworkInterface> interfaces) {
		this.interfaces = interfaces;
	}

	public String getLatitude() {
		return latitude.get();
	}

	public void setLatitude(String latitude) {
		this.latitude.set(latitude);
	}

	public String getLongitude() {
		return longitude.get();
	}

	public void setLongitude(String longitude) {
		this.longitude.set(longitude);
	}

	public String getCountry() {
		return country.get();
	}

	public void setCountry(String country) {
		this.country.set(country);
	}

	public String getCountryCode() {
		return countryCode.get();
	}

	public void setCountryCode(String countryCode) {
		this.countryCode.set(countryCode);
	}

	public String getRegion() {
		return region.get();
	}

	public void setRegion(String region) {
		this.region.set(region);
	}

	public String getCity() {
		return city.get();
	}

	public void setCity(String city) {
		this.city.set(city);
	}

	public int getCvid() {
		return cvid;
	}

	public void setCvid(int cvid) {
		this.cvid = cvid;
	}

	public void setLocation(HashMap<String, String> map) {
		setCountryCode(map.get("CountryCode").toLowerCase());
		setCountry(map.get("CountryName"));
		setRegion(map.get("RegionName"));
		setCity(map.get("City"));
		setLatitude(map.get("Latitude"));
		setLongitude(map.get("Longitude"));
	}

	public ImageIcon getLocationIcon() {
		return locationIcon;
	}

	public ImageIcon getOsIcon() {
		return osIcon;
	}

	public void amalgamate(EV_ProfileDelta c) {
		if (c.hasDepartureTime()) {
			setMessageLatency("" + (new Date().getTime() - c.getDepartureTime()));
		}
		if (c.hasOsFamily()) {
			setOsFamily(c.getOsFamily());
		}
		if (c.hasOsName()) {
			setOsName(c.getOsName());
		}
		if (c.hasOsArch()) {
			setOsArch(c.getOsArch());
		}
		if (c.hasJavaArch()) {
			setJavaArch(c.getJavaArch());
		}
		if (c.hasJavaVersion()) {
			setJavaVersion(c.getJavaVersion());
		}
		if (c.hasJavaVendor()) {
			setJavaVendor(c.getJavaVendor());
		}
		if (c.hasCrimsonVersion()) {
			setCrimsonVersion(c.getCrimsonVersion());
		}
		if (c.hasTimezone()) {
			setTimezone(c.getTimezone());
		}
		if (c.hasLanguage()) {
			setLanguage(c.getLanguage());
		}
		if (c.hasUserName()) {
			setUsername(c.getUserName());
		}
		if (c.hasUserStatus()) {
			setUserStatus(c.getUserStatus());
		}
		if (c.hasUserHome()) {
			setUserHome(c.getUserHome());
		}
		if (c.hasActiveWindow()) {
			setActiveWindow(c.getActiveWindow());
		}
		if (c.hasVirtualization()) {
			setVirtualization(c.getVirtualization());
		}
		if (c.hasSystemRamCapacity()) {
			setSystemRamCapacity(CUtil.Misc.familiarize(c.getSystemRamCapacity(), CUtil.Misc.BYTES));
		}
		if (c.hasSystemRamUsage()) {
			setSystemRamUsage(CUtil.Misc.familiarize(c.getSystemRamUsage(), CUtil.Misc.BYTES));
		}
		if (c.hasCrimsonRamUsage()) {
			setCrimsonRamUsage(CUtil.Misc.familiarize(c.getCrimsonRamUsage(), CUtil.Misc.BYTES));
		}
		if (c.hasCpuModel()) {
			setCpuModel(c.getCpuModel());
		}
		if (c.hasCpuCache()) {
			setCpuCache(c.getCpuCache());
		}
		if (c.hasCpuTemp()) {
			setCpuTemp(c.getCpuTemp());
		}
		if (c.hasCrimsonCpuUsage()) {
			setCrimsonCpuUsage(String.format("%.2f%%", 100 * c.getCrimsonCpuUsage()));
		}
		// core_speed
		// core_usage
		if (c.hasHostname()) {
			setHostname(c.getHostname());
		}
		if (c.hasExtIp()) {
			setExtIp(c.getExtIp());
			// TODO resolve only if viewer location resolution is enabled
			try {
				setLocation(CUtil.Location.resolve(c.getExtIp()));
				loadTransientAttributes();
			} catch (IOException | XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (c.hasNetDns1()) {
			setDns1(c.getNetDns1());
		}
		if (c.hasNetDns2()) {
			setDns2(c.getNetDns2());
		}
		if (c.hasFqdn()) {
			setFqdn(c.getFqdn());
		}
		// network interfaces

	}

}
