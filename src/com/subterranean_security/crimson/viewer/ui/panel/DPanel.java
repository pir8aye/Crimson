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
package com.subterranean_security.crimson.viewer.ui.panel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ui.component.DModule;

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

	private Detail detail = new Detail();
	private boolean showing = false;

	public DPanel(JPanel main) {
		thisDP = this;

		movingBar = new MovingPanel(detail);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		// replace these configs
		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f)
				.place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(6f).col(3f).col(1f)
				.place(0, 0, movingMain).place(0, 1, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public void addModule(DModule dm) {

		detail.addDM(dm);

	}

	public synchronized void showDetail() {

		if (!showing) {
			// move the detail panel out
			movingMain.runAction();
			detail.nowOpen();
			showing = true;
		}

	}

	public synchronized void closeDetail() {

		if (showing) {
			// move the detail panel back
			movingMain.runAction();
			detail.nowClosed();
			showing = false;
		}

	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {

			thisDP.createTransition()
					.push(new SLKeyframe(pos2, 0.9f).setStartSide(
							SLSide.BOTTOM, movingBar).setCallback(
							new SLKeyframe.Callback() {
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

			thisDP.createTransition()
					.push(new SLKeyframe(pos1, 0.9f).setEndSide(SLSide.BOTTOM,
							movingBar).setCallback(new SLKeyframe.Callback() {
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
	private JLabel text = new JLabel();
	private JLabel icon = new JLabel();
	private Runnable r;

	private ArrayList<DModule> modules = new ArrayList<DModule>();

	public Detail() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				new Thread(r).start();

			}
		});

		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BorderLayout(0, 0));

		text.setFont(new Font("Dialog", Font.BOLD, 13));
		add(text, BorderLayout.CENTER);
		add(icon, BorderLayout.WEST);
	}

	public void addDM(DModule dm) {

	}

	public void nowOpen() {

	}

	public void nowClosed() {

	}

}
