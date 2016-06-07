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
package com.subterranean_security.crimson.viewer.ui.common.panels.npanel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class NPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private NPanel thisNP;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovingPanel movingBar;
	private MovingPanel movingMain;

	private Notification note = new Notification();

	public NPanel(JPanel main) {
		thisNP = this;

		movingBar = new MovingPanel(note);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(40).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);
		nThread.start();

	}

	private Queue<Object[]> noteQ = new ConcurrentLinkedQueue<Object[]>();

	public void addNote(String type, String s) {

		addNote(type, s, " ", new Runnable() {
			public void run() {
				// default action
			}

		});

	}

	public void addNote(String type, String text, String subtext, Runnable r) {

		noteQ.offer(new Object[] { type, text, subtext, r });
		synchronized (noteQ) {
			noteQ.notifyAll();
		}

	}

	Thread nThread = new Thread(new Runnable() {
		public void run() {
			waitForNote();
			while (!Thread.interrupted()) {

				Object[] o = noteQ.poll();
				if (o == null) {
					return;
				}
				note.set((String) o[0], (String) o[1], (String) o[2], (Runnable) o[3]);
				// move the note panel up
				movingMain.runAction();

				try {
					Thread.sleep(5000);

				} catch (InterruptedException e) {

				} finally {
					// move the note back down
					movingMain.runAction();

				}

				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {

				}
				if (noteQ.size() == 0) {
					waitForNote();
				}

			}

		}

		private void waitForNote() {
			synchronized (noteQ) {
				try {
					noteQ.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			thisNP.createTransition().push(new SLKeyframe(pos2, 0.9f).setStartSide(SLSide.BOTTOM, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionDN);
							movingMain.enableAction();
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			thisNP.createTransition().push(new SLKeyframe(pos1, 0.9f).setEndSide(SLSide.BOTTOM, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionUP);

						}
					})).play();
		}
	};

}
