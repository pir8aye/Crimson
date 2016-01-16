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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.subterranean_security.crimson.viewer.ui.component.StatusLights;
import com.subterranean_security.crimson.viewer.ui.panel.MovingPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class ProgressArea extends JPanel {

	private static final long serialVersionUID = 1L;
	MovingPanel main;
	MovingPanel note;
	SLPanel slPanel;
	private final SLConfig mainCfg, p1Cfg;

	public ProgressArea() {
		JPanel blank = new JPanel();
		blank.setLayout(new BorderLayout(0, 0));

		main = new MovingPanel(blank);

		pp = new ProcessPanel();

		note = new MovingPanel(pp);
		setLayout(new BorderLayout(0, 0));

		slPanel = new SLPanel();
		add(slPanel);

		// animations
		main.setAction(p1Action);

		//
		p1Cfg = new SLConfig(slPanel).gap(0, 0).row(1f).col(0f).place(0, 0, main).col(10f).place(0, 1, note);

		mainCfg = new SLConfig(slPanel).gap(0, 0).row(1f).col(1f).place(0, 0, main);

		slPanel.setTweenManager(SLAnimator.createTweenManager());
		slPanel.initialize(mainCfg);
	}

	private final Runnable p1Action = new Runnable() {
		@Override
		public void run() {
			slPanel.createTransition().push(
					new SLKeyframe(p1Cfg, 0.7f).setStartSide(SLSide.RIGHT, note).setCallback(new SLKeyframe.Callback() {
				@Override
				public void done() {
					main.setAction(p1BackAction);
					main.enableAction();
				}
			})).play();
		}
	};

	private final Runnable p1BackAction = new Runnable() {
		@Override
		public void run() {

			slPanel.createTransition().push(
					new SLKeyframe(mainCfg, 0.5f).setEndSide(SLSide.RIGHT, note).setCallback(new SLKeyframe.Callback() {
				@Override
				public void done() {
					main.setAction(p1Action);

				}
			})).play();
		}
	};

	private boolean showingDetail = false;
	public ProcessPanel pp;

	public synchronized void showDetail(String s) {

		pp.p.setText(s);
		pp.sl.animate("indeterminate", Color.CYAN, null);
		if (showingDetail) {
			// drop this one and put another up
			main.runAction();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		showingDetail = true;
		main.runAction();

	}

	public synchronized void dropDetail() {
		pp.sl.stopAnimation();
		pp.p.setText(" Finished!");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!showingDetail) {
			return;
		}
		showingDetail = false;
		main.runAction();

	}

	public static ArrayList<String> waiting = new ArrayList<String>();

	public static void start(String name) {
		if (waiting.size() != 0) {
			// something is already using the progressbar
			waiting.add(name);
			return;
		}
		// theres nothing waiting
		MainFrame.main.mm.progressArea.showDetail(" " + name);
		waiting.add(name);

	}

	public static void stop(String s) {
		if (MainFrame.main.mm.progressArea.pp.p.getText().equals(" " + s)) {
			// stopping current process
			MainFrame.main.mm.progressArea.dropDetail();

			// check for other processes waiting for progressbar
			waiting.remove(0);
			if (waiting.size() != 0) {
				start(waiting.remove(0));
			}
		} else {
			// stopping a waiting process
			waiting.remove(s);
			return;
		}

	}

}

class ProcessPanel extends JPanel {

	public StatusLights sl;

	public ProcessPanel() {
		setBorder(null);
		setPreferredSize(new Dimension(200, 20));
		setMinimumSize(new Dimension(200, 20));
		setLayout(new BorderLayout(0, 0));
		sl = new StatusLights();
		sl.setPreferredSize(new Dimension(7, 20));
		add(sl, BorderLayout.WEST);

		p = new JLabel("Process...");
		p.setBorder(new LineBorder(new Color(200, 0, 0)));
		add(p, BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;
	public JLabel p;

}
