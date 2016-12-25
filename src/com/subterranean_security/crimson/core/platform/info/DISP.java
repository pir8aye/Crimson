package com.subterranean_security.crimson.core.platform.info;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.profile.group.AttributeGroupType;
import com.subterranean_security.crimson.core.profile.group.GroupAttributeType;
import com.subterranean_security.crimson.core.proto.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.core.util.CUtil;

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
		return CUtil.UnitTranslator.translateDispMemSize(devices[i].getAvailableAcceleratedMemory());
	}

	public static String getBitDepth(int i) {
		return "" + devices[i].getDisplayMode().getBitDepth();
	}

	public static String computeGID(int i) {
		return getID(i);
	}

	public static ArrayList<AttributeGroupContainer> getAttributes() {
		refresh();
		ArrayList<AttributeGroupContainer> attributes = new ArrayList<AttributeGroupContainer>();
		for (int i = 0; i < devices.length; i++) {
			AttributeGroupContainer.Builder template = AttributeGroupContainer.newBuilder()
					.setGroupType(GroupAttributeType.DISP.ordinal()).setGroupId(computeGID(i));

			attributes.add(template.setAttributeType(AttributeGroupType.DISP_ID.ordinal()).setValue(getID(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.DISP_TYPE.ordinal()).setValue(getType(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.DISP_WIDTH.ordinal()).setValue(getWidth(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.DISP_HEIGHT.ordinal()).setValue(getHeight(i)).build());
			attributes.add(template.setAttributeType(AttributeGroupType.DISP_BIT_DEPTH.ordinal())
					.setValue(getBitDepth(i)).build());
			attributes.add(
					template.setAttributeType(AttributeGroupType.DISP_MEMORY.ordinal()).setValue(getMemory(i)).build());
			attributes.add(template.setAttributeType(AttributeGroupType.DISP_REFRESH_RATE.ordinal())
					.setValue(getRefreshRate(i)).build());
		}
		return attributes;
	}
}
