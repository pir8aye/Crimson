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
package com.subterranean_security.crimson.viewer.ui.common.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class MovingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Color BG_COLOR = new Color(0x3B5998);
	private static final Color BORDER_COLOR = new Color(0x000000);

	private static final TweenManager tweenManager = SLAnimator.createTweenManager();
	private Runnable action;
	private boolean actionEnabled = true;
	private boolean hover = false;
	private int borderThickness = 2;

	public MovingPanel(JPanel panel) {
		setBackground(BG_COLOR);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	public void enableAction() {
		actionEnabled = true;
		if (hover)
			showBorder();
	}

	public void disableAction() {
		actionEnabled = false;
	}

	private void showBorder() {
		tweenManager.killTarget(borderThickness);
		Tween.to(MovingPanel.this, Accessor.BORDER_THICKNESS, 0.4f).target(10).start(tweenManager);
	}

	private void hideBorder() {
		tweenManager.killTarget(borderThickness);
		Tween.to(MovingPanel.this, Accessor.BORDER_THICKNESS, 0.4f).target(2).start(tweenManager);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gg = (Graphics2D) g;

		int w = getWidth();
		int h = getHeight();

		int t = borderThickness;
		gg.setColor(BORDER_COLOR);
		gg.fillRect(0, 0, t, h - 1);
		gg.fillRect(0, 0, w - 1, t);
		gg.fillRect(0, h - 1 - t, w - 1, t);
		gg.fillRect(w - 1 - t, 0, t, h - 1);
	}

	public void runAction() {
		action.run();

	}

	// -------------------------------------------------------------------------
	// Tween Accessor
	// -------------------------------------------------------------------------

	public static class Accessor extends SLAnimator.ComponentAccessor {
		public static final int BORDER_THICKNESS = 100;

		@Override
		public int getValues(Component target, int tweenType, float[] returnValues) {
			MovingPanel tp = (MovingPanel) target;

			int ret = super.getValues(target, tweenType, returnValues);
			if (ret >= 0)
				return ret;

			switch (tweenType) {
			case BORDER_THICKNESS:
				returnValues[0] = tp.borderThickness;
				return 1;
			default:
				return -1;
			}
		}

		@Override
		public void setValues(Component target, int tweenType, float[] newValues) {
			MovingPanel tp = (MovingPanel) target;

			super.setValues(target, tweenType, newValues);

			switch (tweenType) {
			case BORDER_THICKNESS:
				tp.borderThickness = Math.round(newValues[0]);
				tp.repaint();
				break;
			}
		}
	}
}
