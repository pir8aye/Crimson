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
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class EPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private SLSide orientation;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovingPanel movingBar;
	private MovingPanel movingMain;

	private ENote note = new ENote();

	private boolean open = false;
	private boolean moving = false;

	private int transitionTime = 900;

	public EPanel(JPanel main) {
		this(main, SLSide.BOTTOM);
	}

	public EPanel(JPanel main, SLSide o) {
		this.orientation = o;

		movingBar = new MovingPanel(note);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		switch (orientation) {
		case BOTTOM:
			pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
			pos2 = new SLConfig(this).gap(0, 0).row(6f).row(40).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);
			break;
		case TOP:
			pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
			pos2 = new SLConfig(this).gap(0, 0).row(40).row(6f).col(1f).place(0, 0, movingBar).place(1, 0, movingMain);
			break;
		default:
			break;

		}

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public boolean isOpen() {
		return open;
	}

	public boolean isMoving() {
		return moving;
	}

	public void raise(JPanel panel, int height, boolean persist) {
		raise(panel, height);
		if (persist) {
			persistPanel = panel;
			persistConfig = pos2;
		}
	}

	public void raise(JPanel panel, int height) {
		switch (orientation) {
		case BOTTOM:
			pos2 = new SLConfig(this).gap(0, 0).row(6f).row(height).col(1f).place(0, 0, movingMain).place(1, 0,
					movingBar);
			break;
		case TOP:
			pos2 = new SLConfig(this).gap(0, 0).row(height).row(6f).col(1f).place(0, 0, movingBar).place(1, 0,
					movingMain);
			break;
		default:
			break;

		}

		raise(panel);
	}

	public void raise(JPanel panel, float height, boolean persist) {
		raise(panel, height);

		if (persist) {
			persistPanel = panel;
			persistConfig = pos2;
		}
	}

	public void raise(JPanel panel, float height) {
		switch (orientation) {
		case BOTTOM:
			pos2 = new SLConfig(this).gap(0, 0).row(6f).row(height).col(1f).place(0, 0, movingMain).place(1, 0,
					movingBar);
			break;
		case TOP:
			pos2 = new SLConfig(this).gap(0, 0).row(height).row(6f).col(1f).place(0, 0, movingBar).place(1, 0,
					movingMain);
			break;
		default:
			break;

		}

		raise(panel);
	}

	private JPanel persistPanel;
	private SLConfig persistConfig;

	private JPanel lastPanel;

	public JPanel getEP() {
		return lastPanel;
	}

	private void raise(JPanel panel) {
		if (moving) {
			return;
		}

		this.lastPanel = panel;

		if (isOpen()) {
			drop(false);
			new WaitAndRaise().execute();
		} else {
			open = true;
			note.setPanel(panel);

			moving = true;
			movingMain.runAction();
			new EndMotion().execute();
		}

	}

	class WaitAndRaise extends SwingWorker<Void, Void> {
		protected Void doInBackground() throws Exception {
			Thread.sleep(transitionTime);
			return null;
		}

		protected void done() {
			open = true;
			note.setPanel(lastPanel);

			moving = true;
			movingMain.runAction();
			new EndMotion().execute();
		}
	}

	class EndMotion extends SwingWorker<Void, Void> {
		protected Void doInBackground() throws Exception {
			moving = true;
			Thread.sleep(transitionTime);
			return null;
		}

		protected void done() {
			moving = false;
		}
	}

	public void drop() {
		drop(true);
	}

	public void drop(boolean checkPersist) {
		if (isOpen()) {
			movingMain.runAction();
			new EndMotion().execute();
			open = false;

			if (checkPersist && persistPanel != null) {
				if (persistPanel == lastPanel) {
					persistPanel = null;
				} else {
					lastPanel = persistPanel;
					pos2 = persistConfig;
					new WaitAndRaise().execute();
				}

			}
		}
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			EPanel.this.createTransition().push(new SLKeyframe(pos2, transitionTime / 1000f)
					.setStartSide(orientation, movingBar).setCallback(new SLKeyframe.Callback() {
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
			EPanel.this.createTransition().push(new SLKeyframe(pos1, transitionTime / 1000f)
					.setEndSide(orientation, movingBar).setCallback(new SLKeyframe.Callback() {
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
