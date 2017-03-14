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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class NPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovablePanel movingBar;
	private MovablePanel movingMain;

	private Notification note = new Notification();

	public NPanel(JPanel main) {

		movingBar = new MovablePanel(note);
		movingMain = new MovablePanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(40).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);
		nThread.start();

	}

	private ArrayBlockingQueue<Object[]> noteQ = new ArrayBlockingQueue<Object[]>(3);

	public void addNote(String type, String s) {

		addNote(type, s, " ", new Runnable() {
			public void run() {
				// default action
			}

		});

	}

	public void addNote(String type, String text, String subtext, Runnable r) {

		// ignore if queue is full
		noteQ.offer(new Object[] { type, text, subtext, r });

	}

	Runnable run = new Runnable() {
		public void run() {
			movingMain.runAction();
		}
	};

	Thread nThread = new Thread(new Runnable() {
		public void run() {
			while (!Thread.interrupted()) {

				Object[] o = null;
				try {
					o = noteQ.take();
				} catch (InterruptedException e1) {
					return;
				}

				if (o == null) {
					return;
				}
				note.set((String) o[0], (String) o[1], (String) o[2], (Runnable) o[3]);
				// move the note panel up
				movingMain.runAction();
				try {
					SwingUtilities.invokeAndWait(run);
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					Thread.sleep(5000);

				} catch (InterruptedException e) {

				} finally {
					// move the note back down
					try {
						SwingUtilities.invokeAndWait(run);
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					return;
				}

			}

		}

	});

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			NPanel.this.createTransition().push(new SLKeyframe(pos2, 0.9f).setStartSide(SLSide.BOTTOM, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionDN);
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			NPanel.this.createTransition().push(new SLKeyframe(pos1, 0.9f).setEndSide(SLSide.BOTTOM, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionUP);
						}
					})).play();
		}
	};

}
