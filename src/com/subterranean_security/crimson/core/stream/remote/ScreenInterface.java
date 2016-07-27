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
package com.subterranean_security.crimson.core.stream.remote;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.subterranean_security.crimson.client.ClientStore;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.DirtyBlock;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;

public final class ScreenInterface {

	private ScreenInterface() {
	}

	private static ImageWriter iw = ImageIO.getImageWritersBySuffix("jpeg").next();
	private static ImageWriteParam iwp = iw.getDefaultWriteParam();

	// parameters
	private static Robot robot;
	private static Rectangle fullscreenRect;
	private static double scale = 1;
	private static float compQuality = -1f;
	private static int colorQuality = BufferedImage.TYPE_INT_ARGB;
	private static CompareAlgorithm compareType = CompareAlgorithm.COMPARE_SIZE;

	// graphics rendering
	private static RenderingHints rh = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);

	// screen storage
	private static byte[][] screen;// [block data][block id]

	static {
		ImageIO.setUseCache(false);
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

	}

	private static byte[] toByteArray(BufferedImage image) {
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (compQuality == -1) {
				ImageIO.write(image, "jpeg", out);
			} else {
				ImageOutputStream ios = ImageIO.createImageOutputStream(out);
				iw.setOutput(ios);
				iw.write(null, new IIOImage(image, null, null), iwp);
				ios.close();
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[] {};
		}
	}

	public static void captureDelta(int rId, int streamId) {
		BufferedImage image = robot.createScreenCapture(fullscreenRect);
		BufferedImage drawingBlock = new BufferedImage((int) (scale * getBlockScalarSize(image.getWidth())),
				(int) (scale * getBlockScalarSize(image.getHeight())), colorQuality);
		for (int i = 0; i < screen.length; i++) {
			// TODO maybe move outside of loop
			Graphics2D subImageGraphics = drawingBlock.createGraphics();
			subImageGraphics.setRenderingHints(rh);

			int startx = getBlockScalarSize(image.getWidth()) * (i % blockNumber);
			int starty = getBlockScalarSize(image.getHeight()) * (i / blockNumber);

			subImageGraphics.drawImage(image, 0, 0, drawingBlock.getWidth(), drawingBlock.getHeight(), startx, starty,
					startx + getBlockScalarSize(image.getWidth()), starty + getBlockScalarSize(image.getHeight()),
					null);

			if (updateBlock(i, toByteArray(drawingBlock))) {
				// send update
				DirtyBlock.Builder db = DirtyBlock.newBuilder().setBlockId(i);
				for (int j = 0; j < screen[i].length; j++) {
					db.addRGB(screen[i][j]);
				}

				ClientStore.Connections.route(Message.newBuilder().setUrgent(true).setSid(Common.cvid).setRid(rId)
						.setEvStreamData(EV_StreamData.newBuilder().setStreamID(streamId).setDirtyBlock(db)));
			}
		}

	}

	public static void setDevice(GraphicsDevice device) {
		try {
			robot = new Robot(device);
			fullscreenRect = device.getDefaultConfiguration().getBounds();
			screen = new byte[blockNumber * blockNumber][0];
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setScale(double s) {
		if (s > scale) {
			// reset screen
			screen = new byte[blockNumber * blockNumber][0];
		}
		scale = s;
	}

	public static Robot getRobot() {
		return robot;
	}

	public static void setColorQuality(int colorQuality) {
		ScreenInterface.colorQuality = colorQuality;
	}

	public static void setCompQuality(float compQuality) {
		ScreenInterface.compQuality = compQuality;
		if (compQuality != -1) {
			iwp.setCompressionQuality(compQuality);
		}

	}

	public static final int blockNumber = 20;

	public static int getBlockScalarSize(int totalSize) {
		return totalSize / blockNumber;
	}

	public static boolean updateBlock(int blockID, byte[] block) {

		if (block.length != screen[blockID].length) {
			screen[blockID] = block;
			return true;

		} else {
			switch (compareType) {
			case COMPARE_BYTES:
				for (int i = 0; i < block.length; i++) {
					if (block[i] != screen[blockID][i]) {
						screen[blockID] = block;
						return true;
					}
				}
				return false;
			case COMPARE_SIZE:
				return false;

			}
		}
		return false;

	}

	public enum CompareAlgorithm {
		COMPARE_BYTES, COMPARE_SIZE;
	}

}
