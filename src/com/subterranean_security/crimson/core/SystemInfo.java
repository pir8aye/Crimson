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
package com.subterranean_security.crimson.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;

public class SystemInfo {

	private static Sigar sigar = new Sigar();

	private static Cpu cpu = new Cpu();
	private static CpuInfo cpuInfo = new CpuInfo();
	private static NetInfo netInfo = new NetInfo();

	private static long pid = 0;

	static {

		try {
			pid = new ProcessFinder(sigar).findSingleProcess("");
			cpu.gather(sigar);
			cpuInfo = sigar.getCpuInfoList()[0];// TODO
			netInfo.gather(sigar);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static long getCrMemUsage() {
		ProcMem mem = new ProcMem();
		try {
			mem.gather(sigar, pid);
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Gathered Crimson memory footprint: " + mem.getSize());
		return mem.getSize();
	}

	public static EV_ProfileDelta getStatic() {
		EV_ProfileDelta.Builder info = EV_ProfileDelta.newBuilder();

		try {
			info.setCvid(Client.clientDB.getInteger("svid"));
		} catch (Exception e1) {
			// TODO handle
			info.setCvid(0);
		}

		try {
			info.setNetHostname(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			info.setNetHostname("unknown");
		}

		info.setCrimsonVersion(Common.version);

		info.setUserName(System.getProperty("user.name"));
		info.setUserDir(System.getProperty("user.dir"));
		info.setUserHome(System.getProperty("user.home"));
		info.setLanguage(System.getProperty("user.language"));
		info.setJavaVersion(System.getProperty("java.version"));
		info.setJavaVendor(System.getProperty("java.vendor"));
		info.setJavaHome(System.getProperty("java.home"));
		info.setJavaArch(System.getProperty("os.arch"));

		String model = cpuInfo.getModel().replaceAll("\\(.+?\\)", "");
		info.setCpuModel(model.substring(0, model.indexOf("CPU @")).trim());

		return info.build();
	}

}
