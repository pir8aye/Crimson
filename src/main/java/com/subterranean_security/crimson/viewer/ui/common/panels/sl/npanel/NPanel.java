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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.npanel;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.SlidingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLSide;

/**
 * A Notification Panel (NPanel) shows a clickable notification at the bottom of
 * the screen for a short amount of time.
 */
public class NPanel extends SlidingPanel {

	private static final long serialVersionUID = 1L;
	private static final int OVERFLOW_CAPACITY = 5;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovablePanel movingBar;
	private MovablePanel movingMain;

	private List<Notification> overflow;

	public NPanel(JPanel main) {
		this(main, 0.9f);
	}

	public NPanel(JPanel main, float transitionTime) {
		this.transitionTime = transitionTime;

		overflow = new LinkedList<>();

		movingBar = new MovablePanel();
		movingMain = new MovablePanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(40).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);
	}

	public void addNote(String type, String s) {
		addNote(type, s, " ", () -> {
		});
	}

	public void addNote(String type, String text, String subtext, Runnable r) {
		if (overflow.size() > OVERFLOW_CAPACITY) {
			// drop note
			return;
		}

		Notification note = new Notification(type, text, subtext, r);

		if (!open) {
			raise(note);
		} else {
			overflow.add(note);
		}

	}

	private void raise(Notification note) {
		movingBar.setPanel(note);
		movingMain.runAction();
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			moving = true;
			open = true;
			NPanel.this.createTransition().push(new SLKeyframe(pos2, transitionTime)
					.setStartSide(SLSide.BOTTOM, movingBar).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							moving = false;
							movingMain.setAction(actionDN);

							new SwingWorker<Void, Void>() {
								@Override
								protected Void doInBackground() throws Exception {
									Thread.sleep(5000);
									return null;
								}

								protected void done() {
									movingMain.runAction();
								};
							}.execute();
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			moving = true;
			NPanel.this.createTransition().push(new SLKeyframe(pos1, transitionTime)
					.setEndSide(SLSide.BOTTOM, movingBar).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							moving = false;
							open = false;
							movingMain.setAction(actionUP);

							if (overflow.size() > 0) {
								SwingUtilities.invokeLater(() -> {
									raise(overflow.remove(0));
								});

							}

						}
					})).play();
		}
	};

}
