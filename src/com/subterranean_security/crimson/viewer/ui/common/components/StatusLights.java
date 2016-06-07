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
package com.subterranean_security.crimson.viewer.ui.common.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import com.subterranean_security.crimson.core.util.CUtil;

public class StatusLights extends JComponent {

	private static final long serialVersionUID = 1L;
	private static final Color unlit = Color.lightGray;

	private Color TOP = unlit;
	private Color MID = unlit;
	private Color BOT = unlit;

	private String topTip;
	private String midTip;
	private String botTip;

	private Thread animator = new Thread();

	public boolean isAnimating() {
		return animator.isAlive();
	}

	public StatusLights() {

	}

	public StatusLights(String tT, String mT, String bT) {
		topTip = tT;
		midTip = mT;
		botTip = bT;

	}

	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.black);

		Graphics2D g2 = (Graphics2D) graphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(1.1f));

		int circleDiameter = this.getWidth() - 1;

		// draw outlines
		graphics.drawOval(0, 0, circleDiameter, circleDiameter);
		graphics.drawOval(0, circleDiameter, circleDiameter, circleDiameter);
		graphics.drawOval(0, circleDiameter * 2, circleDiameter, circleDiameter);

		// top
		graphics.setColor(TOP);
		graphics.fillOval(0, 0, circleDiameter, circleDiameter);

		// middle
		graphics.setColor(MID);
		graphics.fillOval(0, circleDiameter, circleDiameter, circleDiameter);

		// bottom
		graphics.setColor(BOT);
		graphics.fillOval(0, circleDiameter * 2, circleDiameter, circleDiameter);
	}

	// 1 = top 2 = mid 3 = bottom
	public void addLight(Color color, int pos) {
		switch (pos) {
		case 0: {
			TOP = color;
			MID = color;
			BOT = color;
			break;
		}
		case 1: {
			TOP = color;
			break;
		}
		case 2: {
			MID = color;
			break;
		}
		case 3: {
			BOT = color;
			break;
		}
		}

		repaint();

	}

	public void setLight(Color color, int pos) {
		clear();
		addLight(color, pos);
	}

	public void clear() {
		TOP = unlit;
		MID = unlit;
		BOT = unlit;

		repaint();
	}

	public void animate(String type) {
		animate(type, Color.BLACK, Color.BLACK);
	}

	public void animate(String type, final Color primary, final Color secondary) {
		if (animator != null) {
			animator.interrupt();
		}

		Runnable runnable = null;
		switch (type.toLowerCase()) {
		case "random1": {
			runnable = new Runnable() {
				public void run() {
					int time = 250;
					clear();
					while (!Thread.currentThread().isInterrupted()) {

						try {
							switch (CUtil.Misc.rand(0, 2)) {
							case 0: {
								TOP = randomColor();
								repaint();
								Thread.sleep(time);
								TOP = unlit;
								repaint();

							}
							case 1: {
								MID = randomColor();
								repaint();
								Thread.sleep(time);
								MID = unlit;
								repaint();
							}
							case 2: {
								BOT = randomColor();
								repaint();
								Thread.sleep(time);
								BOT = unlit;
								repaint();
							}

							}
						} catch (InterruptedException e) {
							break;
						}

					}
					clear();
				}
			};

			break;
		}
		case "random2": {
			runnable = new Runnable() {
				public void run() {
					Color c = new Color(0, 90, 70);
					int time = 250;
					clear();
					while (!Thread.currentThread().isInterrupted()) {

						try {
							switch (CUtil.Misc.rand(0, 2)) {
							case 0: {
								TOP = c;
								repaint();
								Thread.sleep(time);
								TOP = unlit;
								repaint();

							}
							case 1: {
								MID = c;
								repaint();
								Thread.sleep(time);
								MID = unlit;
								repaint();
							}
							case 2: {
								BOT = c;
								repaint();
								Thread.sleep(time);
								BOT = unlit;
								repaint();
							}

							}
						} catch (InterruptedException e) {
							break;
						}

					}
					clear();
				}
			};

			break;
		}
		case "indeterminate": {
			runnable = new Runnable() {
				public void run() {
					int time = 200;
					clear();
					while (!Thread.currentThread().isInterrupted()) {

						try {
							TOP = primary;
							repaint();
							Thread.sleep(time);
							MID = primary;
							TOP = unlit;
							repaint();
							Thread.sleep(time);
							BOT = primary;
							MID = unlit;
							repaint();
							Thread.sleep(time);
							MID = primary;
							BOT = unlit;
							repaint();
							Thread.sleep(time);
							MID = unlit;
						} catch (InterruptedException e) {
							break;
						}

					}
					clear();
				}
			};
			break;
		}
		default: {
			return;
		}

		}

		animator = new Thread(runnable);
		animator.start();

	}

	public void stopAnimation() {
		animator.interrupt();
	}

	Thread blinker = new Thread(new Runnable() {
		public void run() {

		}
	});

	public void blink(final Color color, final int i) {

	}

	private static Color randomColor() {
		switch (CUtil.Misc.rand(0, 5)) {
		case 0: {
			return Color.CYAN;
		}
		case 1: {
			return Color.GREEN;
		}
		case 2: {
			return Color.YELLOW;
		}
		case 3: {
			return Color.RED;
		}
		case 4: {
			return Color.PINK;
		}
		case 5: {
			return Color.WHITE;
		}
		}
		return Color.BLACK;
	}

}
