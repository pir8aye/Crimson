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
package com.subterranean_security.crimson.viewer.ui.common.panels.dpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.DModule;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.Processor;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class DPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	private DPanel thisDP;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovingPanel movingBar;
	private MovingPanel movingMain;

	public Detail detail = new Detail();
	public boolean showing = false;
	public boolean moving = false;

	public DPanel(JPanel main) {
		thisDP = this;

		movingBar = new MovingPanel(detail);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		// replace these configs
		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(5f).col(3f).col(1f).place(0, 0, movingMain).place(0, 1, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public void refreshWidth() {
		pos2 = new SLConfig(this).gap(0, 0).row(6f).col(3f).col(detail.getDWidth()).place(0, 0, movingMain).place(0, 1,
				movingBar);
	}

	public synchronized void showDetail(ClientProfile sp) {
		if (!showing) {
			// move the detail panel out
			moving = true;
			movingMain.runAction();
			detail.nowOpen(sp);
			showing = true;
			moving = false;
		}

	}

	public synchronized void closeDetail() {

		if (showing) {
			// move the detail panel back
			moving = true;
			movingMain.runAction();
			detail.nowClosed();
			showing = false;
			moving = false;
		}

	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {

			thisDP.createTransition().push(new SLKeyframe(pos2, 0.9f).setStartSide(SLSide.RIGHT, movingBar)
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

			thisDP.createTransition().push(new SLKeyframe(pos1, 0.9f).setEndSide(SLSide.RIGHT, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							movingMain.setAction(actionUP);

						}
					})).play();
		}
	};

}

class Detail extends JPanel {

	private static final long serialVersionUID = 1L;

	private ArrayList<DModule> modules = new ArrayList<DModule>();

	public Detail() {
		refreshDetails();
	}

	public void refreshDetails() {
		nowClosed();
		modules.clear();
		removeAll();
		init();

		// TODO get from database
		// just add property for now
		Processor dp = new Processor();
		modules.add(dp);
		addDM(dp);

	}

	private void init() {
		setLayout(new BorderLayout(0, 0));
		last = new JPanel(new BorderLayout(0, 0));
		JScrollPane jsp = new JScrollPane(last);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp, BorderLayout.CENTER);
	}

	private JPanel last = new JPanel(new BorderLayout(0, 0));

	public void addDM(Component comp) {
		JPanel tmp = new JPanel(new BorderLayout(0, 0));
		tmp.add(comp, BorderLayout.NORTH);
		last.add(tmp, BorderLayout.CENTER);
		last = tmp;

	};

	public void nowOpen(ClientProfile sp) {
		for (DModule dm : modules) {
			dm.setTarget(sp);
			dm.setShowing(true);
		}
	}

	public void nowClosed() {
		for (DModule dm : modules) {
			dm.setShowing(false);
		}
	}

	public int getDWidth() {
		int max = 0;
		for (DModule dm : modules) {
			max = Math.max(max, dm.getDWidth());
		}
		return max;
	}

}
