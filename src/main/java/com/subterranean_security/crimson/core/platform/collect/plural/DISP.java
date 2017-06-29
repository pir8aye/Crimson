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
package com.subterranean_security.crimson.core.platform.collect.plural;

import java.awt.GraphicsDevice;

import com.subterranean_security.crimson.core.platform.collect.Collector;

/**
 * A collection object for graphical displays.
 * 
 * @author cilki
 * @since 4.0.0
 */
public final class DISP extends Collector {

	private GraphicsDevice device;

	public DISP(GraphicsDevice device) {
		this.device = device;
	}

	public String getID() {
		return device.getIDstring();
	}

	/**
	 * Get the width of this display
	 * 
	 * @return The width in pixels
	 */
	public int getWidth() {
		return device.getDisplayMode().getWidth();
	}

	/**
	 * Get the height of this display
	 * 
	 * @return The height in pixels
	 */
	public int getHeight() {
		return device.getDisplayMode().getHeight();
	}

	public String getType() {
		switch (device.getType()) {
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

	/**
	 * Get this display's refresh rate
	 * 
	 * @return The refresh rate in hertz
	 */
	public int getRefreshRate() {
		return device.getDisplayMode().getRefreshRate();

	}

	/**
	 * Get the amount of video memory availible to this display.
	 * 
	 * @return
	 */
	public int getMemory() {
		return device.getAvailableAcceleratedMemory();
	}

	/**
	 * Get the bit depth for this display.
	 * 
	 * @return
	 */
	public int getBitDepth() {
		return device.getDisplayMode().getBitDepth();
	}

}
