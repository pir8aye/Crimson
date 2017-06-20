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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.SlidingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLSide;

/**
 * A Hidden Panel (HPanel) has a hidden menu below a typical menu.
 */
public class HPanel extends SlidingPanel {

	private static final long serialVersionUID = 1L;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovablePanel movingHMenu; // for hidden menu
	private MovablePanel movingBar; // for normal menu
	private MovablePanel movingMain; // for content

	public HiddenMenu hmenu;
	public NormalMenu nmenu;

	private static int nMenuHeight = 30;

	public HPanel(JPanel main) {
		this(main, 0.9f);
	}

	public HPanel(JPanel main, float transitionTime) {
		this.transitionTime = transitionTime;

		movingMain = new MovablePanel(main);
		movingMain.setAction(actionUP);

	}

	public void init(NormalMenu n, HiddenMenu h) {
		hmenu = h;
		nmenu = n;

		movingBar = new MovablePanel(nmenu);
		movingHMenu = new MovablePanel(hmenu);

		pos1 = new SLConfig(this).gap(0, 0).row(8f).row(nMenuHeight).col(1f).place(0, 0, movingMain).place(1, 0,
				movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);
	}

	public void setHMenuHeight(int h) {
		pos2 = new SLConfig(this).gap(0, 0).row(8f).row(nMenuHeight).row(h).col(1f).place(0, 0, movingMain)
				.place(1, 0, movingBar).place(2, 0, movingHMenu);
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			moving = true;
			HPanel.this.createTransition().push(new SLKeyframe(pos2, transitionTime)
					.setStartSide(SLSide.BOTTOM, movingHMenu).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							hmenu.nowShowing();
							movingMain.setAction(actionDN);
							moving = false;
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			moving = true;
			HPanel.this.createTransition().push(new SLKeyframe(pos1, transitionTime)
					.setEndSide(SLSide.BOTTOM, movingHMenu).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							hmenu.nowClosed();
							movingMain.setAction(actionUP);
							moving = false;
						}
					})).play();
		}
	};

	public JButton getUpBtn() {
		JButton btnUp = new JButton(UICommon.hmenu_open);
		btnUp.setPreferredSize(UICommon.dim_btn_up);
		btnUp.setMargin(new Insets(2, 0, 2, 0));
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!isMoving()) {
					movingMain.runAction();

					if (btnUp.getIcon().equals(UICommon.hmenu_open)) {
						btnUp.setIcon(UICommon.hmenu_close);
					} else {
						btnUp.setIcon(UICommon.hmenu_open);
					}
				}

			}
		});
		return btnUp;
	}

}