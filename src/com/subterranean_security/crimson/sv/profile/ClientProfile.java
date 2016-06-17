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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.Delta.NetworkInterface;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.sv.profile.attribute.Attribute;
import com.subterranean_security.crimson.sv.profile.attribute.TrackedAttribute;
import com.subterranean_security.crimson.sv.profile.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class ClientProfile implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ClientProfile.class);

	private static final long serialVersionUID = 1L;

	private int cvid;

	// Transient attributes
	private transient ImageIcon locationIcon;
	private transient ImageIcon osNameIcon;

	// General attributes
	private Log keylog;
	private Attribute online;
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
	private ArrayList<Double> cpuTemp;
	private Attribute crimsonCpuUsage;
	private ArrayList<Double> coreSpeeds;
	private Attribute cpuUsage;

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
		keylog = new Log();
		online = new TrackedAttribute();
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
		cpuTemp = new ArrayList<Double>();
		crimsonCpuUsage = new UntrackedAttribute();
		// core_speeds
		cpuUsage = new UntrackedAttribute();
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

	public Log getKeylog() {
		return keylog;
	}

	public boolean getOnline() {
		return Boolean.parseBoolean(online.get());
	}

	public void setOnline(boolean b) {
		((TrackedAttribute) online).set("" + b);
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

	public void loadOsIcon() {
		if (osNameIcon == null && osName.get() != null) {
			String icon = osName.get().replaceAll(" ", "_").toLowerCase();

			if (icon.contains("ubuntu")) {
				icon = "ubuntu";
			}

			try {
				osNameIcon = UIUtil.getIcon("icons16/platform/" + icon + ".png");

			} catch (NullPointerException e) {
				Reporter.report(Reporter.newReport().setComment("No OS icon found: " + icon).build());

				// fall back to os family
				osNameIcon = UIUtil.getIcon("icons16/platform/" + osFamily.get() + ".png");
			}
			osNameIcon.setDescription(osName.get());

		}
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

	public String getCpuUsage() {
		return cpuUsage.get();
	}

	public void setCpuUsage(String coreUsages) {
		this.cpuUsage.set(coreUsages);
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
		locationIcon = UIUtil.getIcon("icons16/flags/" + countryCode + ".png");
		locationIcon.setDescription(country.get());
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

	public ImageIcon getOsNameIcon() {
		return osNameIcon;
	}

	public Date getLastUpdate() {
		Date d = new Date(0);

		if (online.getTimestamp().after(d)) {
			d = online.getTimestamp();
		}
		if (osFamily.getTimestamp().after(d)) {
			d = osFamily.getTimestamp();
		}
		if (osName.getTimestamp().after(d)) {
			d = osName.getTimestamp();
		}
		if (osArch.getTimestamp().after(d)) {
			d = osArch.getTimestamp();
		}
		if (javaArch.getTimestamp().after(d)) {
			d = javaArch.getTimestamp();
		}
		if (javaVersion.getTimestamp().after(d)) {
			d = javaVersion.getTimestamp();
		}
		if (javaVendor.getTimestamp().after(d)) {
			d = javaVendor.getTimestamp();
		}
		if (crimsonVersion.getTimestamp().after(d)) {
			d = crimsonVersion.getTimestamp();
		}
		if (timezone.getTimestamp().after(d)) {
			d = timezone.getTimestamp();
		}
		if (language.getTimestamp().after(d)) {
			d = language.getTimestamp();
		}
		if (username.getTimestamp().after(d)) {
			d = username.getTimestamp();
		}
		if (userStatus.getTimestamp().after(d)) {
			d = userStatus.getTimestamp();
		}
		if (userHome.getTimestamp().after(d)) {
			d = userHome.getTimestamp();
		}
		if (activeWindow.getTimestamp().after(d)) {
			d = activeWindow.getTimestamp();
		}
		if (virtualization.getTimestamp().after(d)) {
			d = virtualization.getTimestamp();
		}
		if (messageLatency.getTimestamp().after(d)) {
			d = messageLatency.getTimestamp();
		}
		if (systemRamCapacity.getTimestamp().after(d)) {
			d = systemRamCapacity.getTimestamp();
		}
		if (systemRamUsage.getTimestamp().after(d)) {
			d = systemRamUsage.getTimestamp();
		}
		if (crimsonRamUsage.getTimestamp().after(d)) {
			d = crimsonRamUsage.getTimestamp();
		}
		if (cpuModel.getTimestamp().after(d)) {
			d = cpuModel.getTimestamp();
		}
		if (cpuCache.getTimestamp().after(d)) {
			d = cpuCache.getTimestamp();
		}
		if (crimsonCpuUsage.getTimestamp().after(d)) {
			d = crimsonCpuUsage.getTimestamp();
		}
		if (cpuUsage.getTimestamp().after(d)) {
			d = cpuUsage.getTimestamp();
		}
		if (hostname.getTimestamp().after(d)) {
			d = hostname.getTimestamp();
		}
		if (extIp.getTimestamp().after(d)) {
			d = extIp.getTimestamp();
		}
		if (dns1.getTimestamp().after(d)) {
			d = dns1.getTimestamp();
		}
		if (dns2.getTimestamp().after(d)) {
			d = dns2.getTimestamp();
		}
		if (fqdn.getTimestamp().after(d)) {
			d = fqdn.getTimestamp();
		}
		if (latitude.getTimestamp().after(d)) {
			d = latitude.getTimestamp();
		}
		if (longitude.getTimestamp().after(d)) {
			d = longitude.getTimestamp();
		}
		if (country.getTimestamp().after(d)) {
			d = country.getTimestamp();
		}
		if (countryCode.getTimestamp().after(d)) {
			d = countryCode.getTimestamp();
		}
		if (region.getTimestamp().after(d)) {
			d = region.getTimestamp();
		}
		if (city.getTimestamp().after(d)) {
			d = city.getTimestamp();
		}

		log.debug("Found last update date: {}", d);
		return d;
	}

	public EV_ProfileDelta getUpdates(Date last) {
		Date start = new Date();
		EV_ProfileDelta.Builder pd = EV_ProfileDelta.newBuilder().setCvid(getCvid());

		if (online.getTimestamp().after(last)) {
			pd.setOnline(Boolean.parseBoolean(online.get()));
		}
		if (osFamily.getTimestamp().after(last)) {
			pd.setOsFamily(osFamily.get());
		}
		if (osName.getTimestamp().after(last)) {
			pd.setOsName(osName.get());
		}
		if (javaArch.getTimestamp().after(last)) {
			pd.setJavaArch(javaArch.get());
		}
		if (javaVersion.getTimestamp().after(last)) {
			pd.setJavaArch(javaVersion.get());
		}
		if (javaVendor.getTimestamp().after(last)) {
			pd.setJavaVendor(javaVendor.get());
		}
		if (crimsonVersion.getTimestamp().after(last)) {
			pd.setCrimsonVersion(crimsonVersion.get());
		}
		if (timezone.getTimestamp().after(last)) {
			pd.setTimezone(timezone.get());
		}
		if (language.getTimestamp().after(last)) {
			pd.setLanguage(language.get());
		}
		if (username.getTimestamp().after(last)) {
			pd.setUserName(username.get());
		}
		if (userStatus.getTimestamp().after(last)) {
			pd.setUserStatus(userStatus.get());
		}
		if (userHome.getTimestamp().after(last)) {
			pd.setUserHome(userHome.get());
		}
		if (activeWindow.getTimestamp().after(last)) {
			pd.setActiveWindow(activeWindow.get());
		}
		if (virtualization.getTimestamp().after(last)) {
			pd.setVirtualization(virtualization.get());
		}

		if (systemRamCapacity.getTimestamp().after(last)) {
			// pd.setSystemRamCapacity(systemRamCapacity.get());
		}
		if (systemRamUsage.getTimestamp().after(last)) {
			// pd.setSystemRamUsage(systemRamUsage.get());
		}
		if (crimsonRamUsage.getTimestamp().after(last)) {
			// pd.setCrimsonRamUsage(crimsonRamUsage.get());
		}
		if (cpuModel.getTimestamp().after(last)) {
			pd.setCpuModel(cpuModel.get());
		}
		if (cpuCache.getTimestamp().after(last)) {
			pd.setCpuCache(cpuCache.get());
		}
		if (crimsonCpuUsage.getTimestamp().after(last)) {
			// pd.setCrimsonCpuUsage(crimsonCpuUsage.get());
		}
		if (cpuUsage.getTimestamp().after(last)) {
			//
		}
		if (hostname.getTimestamp().after(last)) {
			pd.setHostname(hostname.get());
		}
		if (extIp.getTimestamp().after(last)) {
			pd.setExtIp(extIp.get());
		}
		if (dns1.getTimestamp().after(last)) {
			pd.setNetDns1(dns1.get());
		}
		if (dns2.getTimestamp().after(last)) {
			pd.setNetDns2(dns2.get());
		}
		if (fqdn.getTimestamp().after(last)) {
			pd.setFqdn(fqdn.get());
		}
		if (latitude.getTimestamp().after(last)) {
			//
		}
		if (longitude.getTimestamp().after(last)) {
			//
		}
		if (country.getTimestamp().after(last)) {
			pd.setCountry(country.get());
		}
		if (countryCode.getTimestamp().after(last)) {
			pd.setCountryCode(countryCode.get());
		}
		if (region.getTimestamp().after(last)) {
			//
		}
		if (city.getTimestamp().after(last)) {
			//
		}

		log.debug("Calulated profile update in {} ms", new Date().getTime() - start.getTime());
		return pd.build();

	}

	public void amalgamate(EV_ProfileDelta c) {
		Date start = new Date();
		if (c.hasDepartureTime()) {
			setMessageLatency("" + (new Date().getTime() - c.getDepartureTime()) + " ms");
		}
		if (c.hasOnline()) {
			setOnline(c.getOnline());
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
		if (c.getCpuTempCount() != 0) {
			setCpuTemp(c.getCpuTempList());
		}
		if (c.hasCrimsonCpuUsage()) {
			setCrimsonCpuUsage(String.format("%.2f%%", 100 * c.getCrimsonCpuUsage()));
		}
		// core_speed
		if (c.hasCoreUsage()) {
			setCpuUsage(String.format("%.2f", 100 * c.getCoreUsage()));
		}
		if (c.hasHostname()) {
			setHostname(c.getHostname());
		}
		if (c.hasExtIp()) {
			setExtIp(c.getExtIp());
			// TODO resolve only if viewer location resolution is enabled
			// try {
			// setLocation(CUtil.Location.resolve(c.getExtIp()));
			// loadTransientAttributes();
			// } catch (IOException | XMLStreamException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
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

		log.debug("Profile amalgamated in {} ms", new Date().getTime() - start.getTime());
	}

}
