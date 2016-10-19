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
package com.subterranean_security.crimson.viewer.ui.common.components.piestat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.Timer;

public class PieStat extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color historyLine = new Color(225, 81, 81);
	private static final Color historyArea = new Color(225, 81, 81);
	private static final Color sweepLine = Color.BLACK;

	public PieBuffer buffer = new PieBuffer(24);

	private double angularSpeed = Math.PI / (buffer.getSize() / 2);
	private double angle = 0;

	// TODO precomputed trig table

	private int diameter;
	private int radius;

	public PieStat(int diameter) {
		this.diameter = diameter - 1;
		this.radius = this.diameter / 2;
	}

	private Timer swingTimer = null;

	public void startCPUStat() {
		stop();
		swingTimer = new Timer(150, (e) -> {
			angle += angularSpeed;
			repaint();
		});
		swingTimer.setInitialDelay(0);
		swingTimer.start();
	}

	public void addPoint(double amplitude) {
		if (amplitude <= 0.25d) {
			buffer.add(new Point((int) Math.round(radius + 0.25 * radius * Math.cos(angle)),
					(int) Math.round(radius + 0.25 * radius * Math.sin(angle))));
		} else {
			buffer.add(new Point((int) Math.round(radius + amplitude * radius * Math.cos(angle)),
					(int) Math.round(radius + amplitude * radius * Math.sin(angle))));
		}
	}

	public void stop() {
		if (swingTimer != null) {
			swingTimer.stop();
			swingTimer = null;
		}
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D g2 = (Graphics2D) graphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw main outline
		graphics.drawOval(0, 0, diameter, diameter);

		g2.setStroke(new BasicStroke(0.5f));

		// draw connect data points and shade triangle
		Point last = null;
		for (Point p : buffer.getElements()) {
			if (last != null) {
				// shade triangle
				graphics.setColor(historyArea);
				Polygon triangle = new Polygon();
				triangle.addPoint(radius, radius);
				triangle.addPoint(last.x, last.y);
				triangle.addPoint(p.x, p.y);
				graphics.fillPolygon(triangle);

				// draw history line
				graphics.setColor(historyLine);
				graphics.drawLine(last.x, last.y, p.x, p.y);

			}
			last = p;
		}

		// draw sweep line
		graphics.setColor(sweepLine);
		g2.setStroke(new BasicStroke(0.7f));
		graphics.drawLine(radius, radius, (int) (radius + radius * Math.cos(angle)),
				(int) (radius + radius * Math.sin(angle)));

	}

}
