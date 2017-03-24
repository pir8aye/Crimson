/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.core.platform.info;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import com.subterranean_security.crimson.core.attribute.keys.AKeyDISP;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.util.UnitTranslator;

public final class DISP {
	private DISP() {
	}

	private static GraphicsDevice[] devices;

	public static void refresh() {
		devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

	public static String getID(int i) {
		return devices[i].getIDstring();
	}

	public static String getWidth(int i) {
		return "" + devices[i].getDisplayMode().getWidth();
	}

	public static String getHeight(int i) {
		return "" + devices[i].getDisplayMode().getHeight();
	}

	public static String getType(int i) {
		switch (devices[i].getType()) {
		case GraphicsDevice.TYPE_RASTER_SCREEN:
			return "Raster Screen";

		case GraphicsDevice.TYPE_PRINTER:
			return "Printer";

		case GraphicsDevice.TYPE_IMAGE_BUFFER:
			return "Image Buffer";

		default:
			return "N/A";
		}
	}

	public static String getRefreshRate(int i) {
		int rate = devices[i].getDisplayMode().getRefreshRate();
		if (rate == DisplayMode.REFRESH_RATE_UNKNOWN) {
			return "N/A";
		} else {
			return rate + " Hz";
		}

	}

	public static String getMemory(int i) {
		return UnitTranslator.translateDispMemSize(devices[i].getAvailableAcceleratedMemory());
	}

	public static String getBitDepth(int i) {
		return "" + devices[i].getDisplayMode().getBitDepth();
	}

	public static String computeGID(int i) {
		return getID(i);
	}

	public static ArrayList<AttributeGroupContainer> getAttributes() {
		refresh();
		ArrayList<AttributeGroupContainer> a = new ArrayList<AttributeGroupContainer>();
		for (int i = 0; i < devices.length; i++) {
			AttributeGroupContainer.Builder container = AttributeGroupContainer.newBuilder()
					.setGroupType(AttributeKey.Type.DISP.ordinal()).setGroupId(computeGID(i));

			container.putAttribute(AKeyDISP.DISP_ID.ordinal(), getID(i));
			container.putAttribute(AKeyDISP.DISP_TYPE.ordinal(), getType(i));
			container.putAttribute(AKeyDISP.DISP_WIDTH.ordinal(), getWidth(i));
			container.putAttribute(AKeyDISP.DISP_HEIGHT.ordinal(), getHeight(i));
			container.putAttribute(AKeyDISP.DISP_BIT_DEPTH.ordinal(), getBitDepth(i));
			container.putAttribute(AKeyDISP.DISP_MEMORY.ordinal(), getMemory(i));
			container.putAttribute(AKeyDISP.DISP_REFRESH_RATE.ordinal(), getRefreshRate(i));

			a.add(container.build());

		}
		return a;
	}
}
