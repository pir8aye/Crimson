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
package com.subterranean_security.crimson.viewer.ui.common.panels.hpanel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class HPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private HPanel thisHP;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovingPanel movingHMenu; // for hidden menu
	private MovingPanel movingBar; // for normal menu
	private MovingPanel movingMain; // for content

	public HiddenMenu hmenu = new HiddenMenu();
	public NormalMenu nmenu = new NormalMenu();

	private JButton btnUp;

	private static int transitionTime = 900;
	
	private static int nMenuHeight = 30;

	private boolean moving = false;

	public HPanel(JPanel main) {
		thisHP = this;

		movingBar = new MovingPanel(nmenu);
		movingHMenu = new MovingPanel(hmenu);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(8f).row(nMenuHeight).col(1f).place(0, 0, movingMain).place(1, 0, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public void setHMenuHeight(int h) {
		pos2 = new SLConfig(this).gap(0, 0).row(8f).row(nMenuHeight).row(h).col(1f).place(0, 0, movingMain)
				.place(1, 0, movingBar).place(2, 0, movingHMenu);
	}

	ImageIcon open = UIUtil.getIcon("icons16/general/open_hmenu.png");
	ImageIcon close = UIUtil.getIcon("icons16/general/close_hmenu.png");

	public JButton initBtnUP() {
		btnUp = new JButton(open);
		btnUp.setPreferredSize(UICommon.dim_btn_up);
		btnUp.setMargin(new Insets(2, 0, 2, 0));
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!moving) {
					moving = true;
					movingMain.runAction();

					if (btnUp.getIcon().equals(open)) {
						btnUp.setIcon(close);
					} else {
						btnUp.setIcon(open);
					}
					new ButtonWorker().execute();
				}

			}
		});
		return btnUp;
	}

	class ButtonWorker extends SwingWorker<Void, Void> {
		protected Void doInBackground() throws Exception {

			Thread.sleep(transitionTime);
			return null;
		}

		protected void done() {
			moving = false;
		}
	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			thisHP.createTransition()
					.push(new SLKeyframe(pos2, transitionTime / 1000f).setCallback(new SLKeyframe.Callback() {
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
			thisHP.createTransition().push(new SLKeyframe(pos1, transitionTime / 1000f)
					.setEndSide(SLSide.BOTTOM, movingHMenu).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionUP);

						}
					})).play();
		}
	};

}