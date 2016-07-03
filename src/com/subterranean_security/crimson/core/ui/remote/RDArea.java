
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
package com.subterranean_security.crimson.core.ui.remote;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JLabel;

import com.subterranean_security.crimson.core.proto.Stream.EventData;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;

public class RDArea extends JLabel {

	private static final long serialVersionUID = 1L;

	private RemoteMaster stream;

	// selection rectangle.
	private BasicStroke selectionRectangle = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
			new float[] { 12, 12 }, 0);

	// rectangle selection outline
	private GradientPaint selectionGradient = new GradientPaint(0.0f, 0.0f, Color.red, 1.0f, 1.0f, Color.white, true);

	// rectangles for all instances
	public static final Rectangle emptyRect = new Rectangle(0, 0, 0, 0);
	public static final Rectangle diffRect = new Rectangle(-1, -1, -1, -1);

	public boolean isSelecting = false;
	private Rectangle oldScreenRect = diffRect;
	private float oldScreenScale = 1.0f;
	private Rectangle oldSelectionRect = diffRect;
	private Rectangle selectionRect = emptyRect;
	boolean partialScreenMode = false;
	private BufferedImage screenImage = null;

	private Rectangle screenRect = emptyRect;

	private float screenScale = 1.0f;

	// mouse coordinates for selection
	public int srcx, srcy, destx, desty;

	public boolean running = false;
	public boolean viewOnly = false;
	public boolean pause = false;
	public boolean hold = false;

	public byte frames = 0;

	public RDArea() {
		setFocusable(true);
		initAdapters();
	};

	public void start(RemoteMaster stream) {
		this.stream = stream;
		running = true;
	}

	public void stop() {
		this.stream = null;
		running = false;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(screenImage, 0, 0, (int) (screenRect.width * screenScale), (int) (screenRect.height * screenScale),
				this);
		DrawSelectingRect(g);
	}

	public void DrawSelectingRect(Graphics g) {
		if (isSelecting)
			if (srcx != destx || srcy != desty) {
				// Compute upper-left and lower-right coordinates for selection
				// rectangle corners.

				int x1 = (srcx < destx) ? srcx : destx;
				int y1 = (srcy < desty) ? srcy : desty;

				int x2 = (srcx > destx) ? srcx : destx;
				int y2 = (srcy > desty) ? srcy : desty;

				// Establish selection rectangle origin.
				selectionRect.x = x1;
				selectionRect.y = y1;

				// Establish selection rectangle extents.
				selectionRect.width = (x2 - x1) + 1;
				selectionRect.height = (y2 - y1) + 1;

				// Draw selection rectangle.
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(selectionRectangle);
				g2d.setPaint(selectionGradient);
				g2d.draw(selectionRect);

				partialScreenMode = true;
			}
	}

	public void updateScreenRect() {

		if (!partialScreenMode) {
			// screenRect = RemoteVariables.screenRect;
			if (!screenRect.equals(oldScreenRect)) {
				oldScreenRect = screenRect;
				setSize(screenRect.getSize());
				setPreferredSize(screenRect.getSize());
				// if
				// (!recorder.viewerOptions.capture.getScreenRect().equals(screenRect))
				// {
				// recorder.viewerOptions.capture.updateScreenSize(screenRect);
				// recorder.viewerOptions.setNewScreenImage(screenRect,
				// recorder.viewerOptions.getColorQuality());
				// }
			}

			if (oldScreenScale != screenScale) {
				Dimension dimension = new Dimension((int) (screenScale * screenRect.getWidth()),
						(int) (screenScale * screenRect.getHeight()));
				setSize(dimension);
				setPreferredSize(dimension);
				oldScreenScale = screenScale;
			}
		} else {
			if (!isSelecting) {
				if (!selectionRect.equals(oldSelectionRect)) {
					// screenRect = selectionRect;
					oldSelectionRect = selectionRect;
					setSize(selectionRect.getSize());
					setPreferredSize(selectionRect.getSize());
					// if
					// (!recorder.viewerOptions.capture.getScreenRect().equals(selectionRect))
					// {
					// recorder.viewerOptions.capture.updateScreenSize(selectionRect);
					// recorder.viewerOptions.setNewScreenImage(selectionRect,
					// recorder.viewerOptions.getColorQuality());
					// }
				}
			}

			// TODO figure this out
			if (screenScale != screenScale) {
				Dimension dimension = new Dimension((int) (screenScale * selectionRect.getWidth()),
						(int) (screenScale * selectionRect.getHeight()));
				setSize(dimension);
				setPreferredSize(dimension);
				oldScreenScale = screenScale;
			}
		}
	}

	public void stopSelectingMode() {
		partialScreenMode = false;
		selectionRect = new Rectangle(0, 0, 0, 0);
		oldSelectionRect = new Rectangle(-1, -1, -1, -1);
		// RemoteVariables.screenRect = new Rectangle(0, 0, 0, 0);
		// if (recorder.config.reverseConnection)
		// recorder.viewerOptions.setChanged(true);
		// else
		// recorder.viewer.setOption(Commons.RECT_OPTION);
	}

	public void doneSelecting() {
		if (isSelecting) {
			isSelecting = false;
			oldSelectionRect = new Rectangle(0, 0, 0, 0);

			if (partialScreenMode) {

				// TODO
				float screenScale = 1.0f;
				// float screenScale = 1.0f / RemoteVariables.screenScale;
				Rectangle rect = new Rectangle(selectionRect);
				rect.x = (int) (rect.x * screenScale);
				rect.y = (int) (rect.y * screenScale);
				rect.height = (int) (rect.height * screenScale);
				rect.width = (int) (rect.width * screenScale);

				// RemoteVariables.screenRect = rect;
				// if (recorder.config.reverseConnection)
				// recorder.viewerOptions.setChanged(true);
				// else
				// recorder.viewer.setOption(Commons.RECT_OPTION);
			}

			srcx = destx;
			srcy = desty;

			Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(cursor);
		}
	}

	public boolean isPartialScreenMode() {
		return partialScreenMode;
	}

	public Rectangle getSelectionRect() {
		return selectionRect;
	}

	public void startSelectingMode() {
		isSelecting = true;
		Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		setCursor(cursor);
	}

	public void updateScreen(HashMap<String, byte[]> changedBlocks) {
		updateScreenRect();

		// screenImage = RemoteVariables.capture.setChangedBlocks(screenImage,
		// changedBlocks);

		repaint();
		frames++;
	}

	public BufferedImage screenshot() {
		// TODO!
		return screenImage;
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
				stream.sendEvent(EventData.newBuilder().build());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isSelecting) {
					destx = e.getX();
					desty = e.getY();
				} else {
					stream.sendEvent(EventData.newBuilder().build());
				}
			}
		};

		ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (isSelecting) {
					destx = srcx = e.getX();
					desty = srcy = e.getY();
				} else {
					stream.sendEvent(EventData.newBuilder().build());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				doneSelecting();
				stream.sendEvent(EventData.newBuilder().build());
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
