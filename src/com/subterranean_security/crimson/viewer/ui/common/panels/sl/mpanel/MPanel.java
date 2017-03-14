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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.mpanel;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.SlidingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;

public class MPanel extends SlidingPanel {

	private static final long serialVersionUID = 1L;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovablePanel bar;
	private MovablePanel detail;
	private MovablePanel main;

	private boolean open = false;

	public MPanel(JPanel jp) {

		bar = new MovablePanel(new VerticalMenu(this));
		detail = new MovablePanel();
		main = new MovablePanel(jp);
		main.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(1f).col(30).col(1f).place(0, 1, main).place(0, 0, bar);
		pos2 = new SLConfig(this).gap(0, 0).row(1f).col(30).col(120).col(1f).place(0, 0, bar).place(0, 1, detail)
				.place(0, 2, main);

		transitionTime = 800;

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * Raises the specified JPanel. If a JPanel is already showing and is equal
	 * to the specified JPanel, then the panel is dropped.
	 * 
	 * @param panel
	 * @return true if the panel will be raised
	 */
	public boolean raise(JPanel panel) {
		if (moving) {
			return false;
		}

		if (isOpen()) {
			drop();
			if (detail.getPanel() != panel) {
				new WaitAndRaise(panel).execute();
			} else {
				return false;
			}

		} else {
			detail.setPanel(panel);

			open = true;
			moving = true;
			main.runAction();
			endMotion();
		}
		return true;
	}

	class WaitAndRaise extends SwingWorker<Void, Void> {

		private JPanel panel;

		public WaitAndRaise(JPanel panel) {
			this.panel = panel;

		}

		protected Void doInBackground() throws Exception {
			Thread.sleep(transitionTime);
			return null;
		}

		protected void done() {
			open = true;
			detail.setPanel(panel);

			moving = true;
			main.runAction();
			endMotion();
		}
	}

	public void drop() {
		if (isOpen()) {
			main.runAction();
			endMotion();
			open = false;

		}
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			MPanel.this.createTransition()
					.push(new SLKeyframe(pos2, transitionTime / 1000f).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							main.setAction(actionDN);
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			MPanel.this.createTransition()
					.push(new SLKeyframe(pos1, transitionTime / 1000f).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							main.setAction(actionUP);
						}
					})).play();
		}
	};

}
