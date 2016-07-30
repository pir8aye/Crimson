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
package com.subterranean_security.crimson.core.stream.info;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.InfoParam;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.Native;

public abstract class InfoSlave extends Stream {

	public InfoSlave(Param p) {
		param = p;
		start();
	}

	public InfoSlave(InfoParam ip) {
		this(Param.newBuilder().setInfoParam(ip).setStreamID(IDGen.getStreamid()).setVID(Common.cvid).build());
	}

	@Override
	public void received(Message m) {
		// do nothing
	}

	private String lastActiveWindow = "";
	private double lastCpuUsage;
	private double lastCrimsonCpuUsage;
	private long lastRamUsage;
	private long lastCrimsonRamUsage;

	protected EV_ProfileDelta gather() {
		EV_ProfileDelta.Builder pd = EV_ProfileDelta.newBuilder().setCvid(Common.cvid);

		// active window
		if (param.getInfoParam().hasActiveWindow()) {
			String activeWindow = Native.getActiveWindow();
			if (!lastActiveWindow.equals(activeWindow)) {
				pd.setActiveWindow(activeWindow);
				lastActiveWindow = activeWindow;
			}
		}

		// ram usage
		if (param.getInfoParam().hasRamUsage()) {
			long ramUsage = Platform.Advanced.getMemoryUsage();
			if (lastRamUsage != ramUsage) {
				pd.setSystemRamUsage(ramUsage);
				lastRamUsage = ramUsage;
			}

		}

		// cpu usage
		if (param.getInfoParam().hasCpuUsage()) {
			double coreUsage = Platform.Advanced.getCPUUsage();
			if (lastCpuUsage != coreUsage) {
				pd.setCoreUsage(coreUsage);
				lastCpuUsage = coreUsage;
			}

		}

		// cpu temps
		if (param.getInfoParam().hasCpuTemp()) {
			for (double d : Platform.Advanced.getCPUTemps()) {
				pd.addCpuTemp(d);
			}
		}

		// crimson ram usage
		if (param.getInfoParam().hasCrimsonRamUsage()) {
			long crimsonRamUsage = Platform.Advanced.getCrimsonMemoryUsage();
			if (lastCrimsonRamUsage != crimsonRamUsage) {
				pd.setCrimsonRamUsage(crimsonRamUsage);
				lastCrimsonRamUsage = crimsonRamUsage;
			}

		}

		// crimson cpu usage
		if (param.getInfoParam().hasCrimsonCpuUsage()) {
			double crimsonCpuUsage = Platform.Advanced.getCrimsonCpuUsage();
			if (lastCrimsonCpuUsage != crimsonCpuUsage) {
				pd.setCrimsonCpuUsage(crimsonCpuUsage);
				lastCrimsonCpuUsage = crimsonCpuUsage;
			}

		}
		return pd.build();
	}

	@Override
	public void start() {

		timer.schedule(sendTask, 0, param.hasPeriod() ? param.getPeriod() : 1000);

	}

	@Override
	public void stop() {
		timer.cancel();

	}

}
