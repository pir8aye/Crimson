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
package com.subterranean_security.crimson.viewer.ui.common.components.labels;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * A JLabel which can be smoothly faded in and out.
 */
public class FadeLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public FadeLabel() {
		super();
	}

	/**
	 * Initialize a new FadeLabel with defaults
	 * 
	 * @param icon
	 */
	public FadeLabel(Icon icon) {
		this(icon, 0, 0.05f, 50);
	}

	public FadeLabel(int delay, float increment, int repaintDelay) {
		super();

		setInitialDelay(delay);
		setFadeIncrement(increment);
		setRepaintDelay(repaintDelay);

		setAlpha(1f);
	}

	public FadeLabel(Icon icon, int delay, float increment, int repaintDelay) {
		this(delay, increment, repaintDelay);
		setIcon(icon);
	}

	/**
	 * Fade out the current label and fade in the specified image.
	 * 
	 * @param image
	 *            The new image
	 */
	public void fadeImage(Icon image) {
		fadeOut();

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				while (timer != null) {
					Thread.sleep(refreshFrequency);
				}
				setIcon(image);
				return null;
			}

			protected void done() {
				fadeIn();
			};
		}.execute();

	}

	/**
	 * Fade the label from 100% opacity to 0% according to the given parameters
	 */
	public void fadeOut() {
		fade(-increment);
	}

	/**
	 * Fade the label from 0% opacity to 100% according to the given parameters
	 */
	public void fadeIn() {
		fade(increment);
	}

	private Timer timer;

	/**
	 * Execute the fade. An in-progress fade will be canceled.
	 * 
	 * @pre: timer == null, increment != 0
	 * @param increment
	 *            How much to change the alpha in each step
	 */
	private void fade(float increment) {
		if (timer != null)
			timer.stop();

		timer = new Timer(refreshFrequency, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float alpha = getAlpha();
				alpha += increment;
				if (alpha < 0) {
					alpha = 0;
					timer.stop();
					timer = null;
				} else if (alpha > 1) {
					alpha = 1;
					timer.stop();
					timer = null;
				}
				setAlpha(alpha);
			}
		});
		timer.setRepeats(true);
		timer.setInitialDelay(initialDelay);
		timer.start();
	}

	private float alpha;

	/**
	 * Change the alpha property
	 * 
	 * @param value
	 */
	public void setAlpha(float value) {
		if (alpha != value) {
			float old = alpha;
			alpha = value;
			firePropertyChange("alpha", old, alpha);
			repaint();
		}
	}

	public float getAlpha() {
		return alpha;
	}

	private float increment;

	/**
	 * Sets the amount to change the opacity in each step
	 * 
	 * @param increment
	 */
	public void setFadeIncrement(float increment) {
		if (increment == 0)
			throw new IllegalArgumentException("The fade increment cannot be 0");

		this.increment = increment;
	}

	private int refreshFrequency;

	/**
	 * Sets the time between repaints
	 * 
	 * @param repaintDelay
	 */
	public void setRepaintDelay(int repaintDelay) {
		if (repaintDelay <= 0)
			throw new IllegalArgumentException("The repaint delay cannot be less than or equal to 0");

		this.refreshFrequency = repaintDelay;
	}

	private int initialDelay;

	/**
	 * Sets the time delay between the call to fade() and the first repaint
	 * 
	 * @param delay
	 */
	public void setInitialDelay(int delay) {
		if (delay < 0)
			throw new IllegalArgumentException("The initial delay cannot be less than 0");

		this.initialDelay = delay;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
		super.paint(g2d);
		g2d.dispose();
	}

}
