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
package com.subterranean_security.crimson.viewer.ui.common.panels.epanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class EPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private EPanel thisNP;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovingPanel movingBar;
	private MovingPanel movingMain;

	private ENote note = new ENote();

	private boolean open = false;

	private int timeout = 900;

	public EPanel(JPanel main) {
		thisNP = this;

		movingBar = new MovingPanel(note);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(40).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public synchronized boolean isOpen() {
		return open;
	}

	private JPanel last = null;

	public synchronized void raise(JPanel panel, int height) {
		if (panel.equals(last)) {
			return;
		} else {
			last = panel;
		}

		if (isOpen()) {
			drop();
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		open = true;
		note.setPanel(panel);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(height).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		movingMain.runAction();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void raise(JPanel panel, float height) {
		if (panel.equals(last)) {
			return;
		} else {
			last = panel;
		}

		if (isOpen()) {
			drop();
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		open = true;
		note.setPanel(panel);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).row(height).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		movingMain.runAction();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void drop() {
		if (isOpen()) {
			movingMain.runAction();
			open = false;
		}
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			thisNP.createTransition().push(new SLKeyframe(pos2, timeout / 1000f).setStartSide(SLSide.BOTTOM, movingBar)
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
			thisNP.createTransition().push(new SLKeyframe(pos1, timeout / 1000f).setEndSide(SLSide.BOTTOM, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionUP);

						}
					})).play();
		}
	};

	class ENote extends JPanel {

		private static final long serialVersionUID = 1L;

		public JPanel panel = new JPanel();

		public ENote() {

			setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			setLayout(new BorderLayout(0, 0));

			panel.setLayout(new BorderLayout());
			add(panel, BorderLayout.CENTER);

		}

		public void setPanel(JPanel j) {
			j.setVisible(true);
			panel.removeAll();
			panel.add(j, BorderLayout.CENTER);
		}

	}

}
