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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.cpanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.SlidingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLSide;

/**
 * A Carousel Panel (CPanel) holds multiple panels side-by-side. One panel is
 * shown at a time.
 */
public class CPanel extends SlidingPanel {

	private static final long serialVersionUID = 1L;

	private List<MovablePanel> panels;
	private int currentPanel;

	public CPanel(JPanel... panels) {
		this(1f, panels);
	}

	public CPanel(float transitionTime, JPanel... panels) {
		this.transitionTime = transitionTime;
		this.panels = new ArrayList<>();
		currentPanel = 0;

		for (JPanel panel : panels) {
			this.panels.add(new MovablePanel(panel));
		}

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(new SLConfig(this).gap(0, 0).row(1f).col(1f).place(0, 0, this.panels.get(currentPanel)));

	}

	/**
	 * Show the panel to the left of the current panel
	 */
	public void moveLeft() {
		if (!isMoving() && currentPanel > 0) {
			panels.get(currentPanel).setAction(moveLeft);
			panels.get(currentPanel).runAction();
		}
	}

	/**
	 * Show the panel to the right of the current panel
	 */
	public void moveRight() {
		if (!isMoving() && currentPanel < panels.size() - 1) {
			panels.get(currentPanel).setAction(moveRight);
			panels.get(currentPanel).runAction();
		}
	}

	private final Runnable moveRight = new Runnable() {
		@Override
		public void run() {
			moving = true;
			CPanel.this.createTransition()
					.push(new SLKeyframe(new SLConfig(CPanel.this).gap(0, 0).row(1f).col(1f).place(0, 0,
							panels.get(currentPanel + 1)), transitionTime)
									.setStartSide(SLSide.RIGHT, panels.get(currentPanel + 1))
									.setEndSide(SLSide.LEFT, panels.get(currentPanel))
									.setCallback(new SLKeyframe.Callback() {
										@Override
										public void done() {
											currentPanel++;
											moving = false;
										}
									}))
					.play();
		}
	};

	private final Runnable moveLeft = new Runnable() {
		@Override
		public void run() {
			moving = true;
			CPanel.this.createTransition()
					.push(new SLKeyframe(new SLConfig(CPanel.this).gap(0, 0).row(1f).col(1f).place(0, 0,
							panels.get(currentPanel - 1)), transitionTime)
									.setStartSide(SLSide.LEFT, panels.get(currentPanel - 1))
									.setEndSide(SLSide.RIGHT, panels.get(currentPanel))
									.setCallback(new SLKeyframe.Callback() {
										@Override
										public void done() {
											currentPanel--;
											moving = false;
										}
									}))
					.play();
		}
	};

}
