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
package com.subterranean_security.crimson.viewer.ui.screen.password;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class EntropyHarvester extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel hpanel;
	private ArrayList<Point> points = new ArrayList<Point>();
	public int progress = 0;
	private JProgressBar progressBar;
	private JLabel prompt;

	private static Color paintColor = Color.RED;

	public EntropyHarvester() {
		setBorder(new TitledBorder(null, "Entropy Harvester", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setMaximum(700);
		add(progressBar, BorderLayout.SOUTH);

		hpanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(paintColor);
				for (Point p : points) {
					g.drawLine(p.x, p.y, p.x, p.y);
				}

			}

		};
		hpanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (progressBar.getValue() >= progressBar.getMaximum()) {
					return;
				}
				points.add(e.getPoint());
				if (prompt.isVisible()) {
					prompt.setVisible(false);
				}
				updateProgress();
				hpanel.repaint();
			}
		});
		hpanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout(0, 0));
		main.add(hpanel);

		prompt = new JLabel("Move your mouse in this box");
		// hpanel.add(prompt);
		add(main, BorderLayout.CENTER);

		JSeparator Nseparator = new JSeparator();
		Nseparator.setVisible(false);
		Nseparator.setPreferredSize(new Dimension(0, 10));
		Nseparator.setOrientation(SwingConstants.VERTICAL);
		main.add(Nseparator, BorderLayout.NORTH);

		JSeparator Sseparator = new JSeparator();
		Sseparator.setVisible(false);
		Sseparator.setPreferredSize(new Dimension(0, 10));
		Sseparator.setOrientation(SwingConstants.VERTICAL);
		main.add(Sseparator, BorderLayout.SOUTH);

		JSeparator Wseparator = new JSeparator();
		Wseparator.setVisible(false);
		Wseparator.setPreferredSize(new Dimension(10, 0));
		main.add(Wseparator, BorderLayout.WEST);

		JSeparator Eseparator = new JSeparator();
		Eseparator.setVisible(false);
		Eseparator.setPreferredSize(new Dimension(10, 0));
		main.add(Eseparator, BorderLayout.EAST);

	}

	public void updateProgress() {
		progress++;
		progressBar.setValue(progress);

	}

}
