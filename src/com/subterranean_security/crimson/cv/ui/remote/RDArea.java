
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
package com.subterranean_security.crimson.cv.ui.remote;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import com.subterranean_security.crimson.core.proto.Stream.DirtyBlock;
import com.subterranean_security.crimson.core.proto.Stream.DirtyRect;
import com.subterranean_security.crimson.core.proto.Stream.EventData;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.core.stream.remote.ScreenInterface;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class RDArea extends JLabel {

	private static final long serialVersionUID = 1L;

	private RemoteMaster stream;

	private int monitorWidth;
	private int monitorHeight;
	private float scale;
	public BufferedImage screenImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	private RDPanel parent;

	public RDArea(RDPanel rdp) {
		this.parent = rdp;
		initAdapters();

	};

	public void setMonitorSize(int w, int h) {
		monitorHeight = h;
		monitorWidth = w;
		screenImage = new BufferedImage(w, h, parent.settings.getColorType());
	}

	/*
	 * Used to preserve aspect ratio
	 */
	@Override
	public Dimension getPreferredSize() {

		Dimension d = this.getParent().getSize();

		if (stream == null) {
			return d;
		}

		double nw = monitorWidth;
		double nh = monitorHeight;

		double ratio = nw / nh;

		if (nw > d.getWidth()) {
			nw = d.getWidth();
			nh = nw / ratio;
		}
		if (nh > d.getHeight()) {
			nh = d.getHeight();
			nw = nh * ratio;
		}

		if (!parent.ep.isMoving()) {
			screenImage = UIUtil.resize(screenImage, (int) nw, (int) nh);

			scale = (float) (nw / monitorWidth);
			stream.sendEvent(EventData.newBuilder().setScaleUpdate(scale).build());
		}

		return new Dimension((int) nw, (int) nh);
	}

	public void setStream(RemoteMaster stream) {
		this.stream = stream;
	}

	@Override
	public void paintComponent(Graphics g) {

		g.drawImage(screenImage, 0, 0, this.getWidth(), this.getHeight(), this);

	}

	/**
	 * Update a static block
	 * 
	 * @param db
	 */
	public void updateScreen(DirtyBlock db) {

		int startx = (int) (scale * ScreenInterface.getBlockScalarSize(monitorWidth))
				* (db.getBlockId() % ScreenInterface.blockNumber);
		int starty = (int) (scale * ScreenInterface.getBlockScalarSize(monitorHeight))
				* (db.getBlockId() / ScreenInterface.blockNumber);

		ByteArrayOutputStream out = new ByteArrayOutputStream(db.getRGBCount());
		for (int i = 0; i < db.getRGBCount(); i++) {
			out.write(db.getRGB(i));
		}

		try {
			BufferedImage block = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
			screenImage.getGraphics().drawImage(block, startx, starty, block.getWidth(), block.getHeight(), this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}

	/**
	 * Update an arbitrary rectangle
	 * 
	 * @param dr
	 */
	public void updateScreen(DirtyRect dr) {

		List<Integer> rgb = dr.getRGBAList();
		int r = 0;
		for (int j = dr.getSy(); j < dr.getSy() + dr.getH(); j++) {
			for (int i = dr.getSx(); i < dr.getSx() + dr.getW(); i++) {

				screenImage.setRGB(i, j, rgb.get(r++));
			}
		}
		repaint(dr.getSx(), dr.getSy(), dr.getW(), dr.getH());

	}

	public BufferedImage screenshot() {
		return UIUtil.deepCopy(screenImage);
	}

	private KeyAdapter ka;
	private MouseAdapter ma;
	private MouseWheelListener mwl;
	private MouseMotionAdapter mma;

	private void initAdapters() {
		ka = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				stream.sendEvent(EventData.newBuilder().setKeyPressed(e.getKeyCode()).build());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				stream.sendEvent(EventData.newBuilder().setKeyReleased(e.getKeyCode()).build());
			}
		};

		mwl = new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				stream.sendEvent(EventData.newBuilder().build());
			}
		};

		mma = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				stream.sendEvent(EventData.newBuilder().setMouseMovedX(e.getX()).setMouseMovedY(e.getY()).build());
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				stream.sendEvent(EventData.newBuilder().build());

			}
		};

		ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				stream.sendEvent(EventData.newBuilder().setMousePressed(e.getButton()).setMouseMovedX(e.getX())
						.setMouseMovedY(e.getY()).build());

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stream.sendEvent(EventData.newBuilder().setMouseReleased(e.getButton()).build());
			}
		};
	}

	private boolean keyboardInstalled = false;
	private boolean mouseInstalled = false;

	public void installKeyAdapters() {
		if (!keyboardInstalled) {
			keyboardInstalled = true;
			addKeyListener(ka);
		}

	}

	public void installMouseAdapters() {
		if (!mouseInstalled) {
			mouseInstalled = true;
			addMouseWheelListener(mwl);
			addMouseMotionListener(mma);
			addMouseListener(ma);

		}
	}

	public void uninstallKeyAdapters() {
		if (keyboardInstalled) {
			keyboardInstalled = false;
			removeKeyListener(ka);
		}

	}

	public void uninstallMouseAdapters() {
		if (mouseInstalled) {
			mouseInstalled = false;
			removeMouseWheelListener(mwl);
			removeMouseMotionListener(mma);
			removeMouseListener(ma);
		}

	}

}
