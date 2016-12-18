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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.lpanel.LPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.DModule;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.Preview;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.Processor;
import com.subterranean_security.crimson.viewer.ui.screen.main.detail.WorldMap;

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

	public Detail detail = new Detail(this);

	private boolean showing = false;
	private boolean moving = false;

	public boolean isOpen() {
		return showing;
	}

	public boolean isMoving() {
		return moving;
	}

	private static int transitionTime = 900;

	public DPanel(JPanel main) {
		thisDP = this;

		movingBar = new MovingPanel(detail);
		movingMain = new MovingPanel(main);
		movingMain.setAction(actionUP);

		pos1 = new SLConfig(this).gap(0, 0).row(2f).col(1f).place(0, 0, movingMain);
		pos2 = new SLConfig(this).gap(0, 0).row(5f).col(3f).col(1.25f).place(0, 0, movingMain).place(0, 1, movingBar);

		this.setTweenManager(SLAnimator.createTweenManager());
		this.initialize(pos1);

	}

	public void refreshWidth() {
		pos2 = new SLConfig(this).gap(0, 0).row(6f).col(3f).col(detail.getDWidth()).place(0, 0, movingMain).place(0, 1,
				movingBar);
	}

	public ClientProfile getTarget() {
		return detail.getTarget();
	}

	public void showDetail(ClientProfile sp) {
		if (!moving) {
			if (!showing) {
				// refresh width
				// refreshWidth();

				// move the detail panel out
				moving = true;
				movingMain.runAction();
				new EndMotion().execute();
				showing = true;
			}

			detail.nowOpen(sp);

		}

	}

	public void closeDetail() {
		if (showing && !moving) {
			// move the detail panel back
			moving = true;
			movingMain.runAction();
			new EndMotion().execute();
			detail.nowClosed();
			showing = false;

		}

	}

	class EndMotion extends SwingWorker<Void, Void> {
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

			thisDP.createTransition().push(new SLKeyframe(pos2, transitionTime / 1000f)
					.setStartSide(SLSide.RIGHT, movingBar).setCallback(new SLKeyframe.Callback() {
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

			thisDP.createTransition().push(new SLKeyframe(pos1, transitionTime / 1000f)
					.setEndSide(SLSide.RIGHT, movingBar).setCallback(new SLKeyframe.Callback() {
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

	private DPanel parent = null;

	private ArrayList<DModule> modules = new ArrayList<DModule>();

	private LPanel listPanel = new LPanel();

	private ClientProfile target = null;

	public ClientProfile getTarget() {
		return target;
	}

	private boolean processor = false;
	private boolean preview = false;
	private boolean map = false;

	public Detail(DPanel parent) {
		this.parent = parent;
		init();
		addInitialDetails();
	}

	public void addInitialDetails() {

		if (processor) {
			Processor p = new Processor();
			modules.add(p);
			listPanel.addPanel(p);
		}
		if (preview) {
			Preview p = new Preview();
			modules.add(p);
			listPanel.addPanel(p);
		}
		if (map) {
			WorldMap p = new WorldMap();
			modules.add(p);
			listPanel.addPanel(p);
		}

	}

	private void init() {
		setLayout(new BorderLayout(0, 0));

		try {
			processor = ViewerStore.Databases.local.getBoolean("detail.processor");
			preview = ViewerStore.Databases.local.getBoolean("detail.preview");
			map = ViewerStore.Databases.local.getBoolean("detail.map");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JPanel menuPanel = new JPanel(new BorderLayout());
		JMenuBar menu = new JMenuBar();
		menuPanel.add(menu, BorderLayout.CENTER);
		listPanel.addPanel(menuPanel);
		add(listPanel, BorderLayout.CENTER);

		JButton controlPanel = new JButton();
		controlPanel.setToolTipText("Open Client Control Panel");
		controlPanel.setIcon(UIUtil.getIcon("icons16/general/cog.png"));
		controlPanel.setMargin(new Insets(1, 1, 1, 1));
		controlPanel.addActionListener((ActionEvent e) -> {
			parent.closeDetail();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ClientCPFrame ccpf = new ClientCPFrame(target);
					ccpf.setLocationRelativeTo(null);
					ccpf.setVisible(true);
				}
			});
		});
		menu.add(controlPanel);
		menu.add(Box.createHorizontalGlue());

		JButton toggleProcessor = new JButton();
		if (processor) {
			toggleProcessor.setIcon(UIUtil.getIcon("icons16/general/processor_del.png"));
		} else {
			toggleProcessor.setIcon(UIUtil.getIcon("icons16/general/processor_add.png"));
		}
		toggleProcessor.setToolTipText("Toggle Processor");
		toggleProcessor.setMargin(new Insets(1, 1, 1, 1));
		toggleProcessor.addActionListener((ActionEvent e) -> {

			toggleProcessor.setEnabled(false);

			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					ViewerStore.Databases.local.storeObject("detail.processor", processor = !processor);
					return null;

				}

				protected void done() {
					if (processor) {
						toggleProcessor.setIcon(UIUtil.getIcon("icons16/general/processor_del.png"));

						// add processor module
						Processor p = new Processor();
						p.setTarget(target);
						p.setShowing(true);
						modules.add(p);
						listPanel.addPanel(p);
					} else {
						toggleProcessor.setIcon(UIUtil.getIcon("icons16/general/processor_add.png"));

						// remove module
						for (DModule dm : modules) {
							if (dm instanceof Processor) {
								listPanel.removePanel((Processor) dm);
								modules.remove(dm);
								dm.setShowing(false);
								break;
							}
						}
					}
					toggleProcessor.setEnabled(true);
					listPanel.revalidate();
					listPanel.repaint();
				};
			}.execute();

		});
		menu.add(toggleProcessor);

		JButton togglePreview = new JButton();
		if (preview) {
			togglePreview.setIcon(UIUtil.getIcon("icons16/general/monitor_del.png"));
		} else {
			togglePreview.setIcon(UIUtil.getIcon("icons16/general/monitor_add.png"));
		}
		togglePreview.setToolTipText("Toggle Preview");
		togglePreview.setMargin(new Insets(1, 1, 1, 1));
		togglePreview.addActionListener((ActionEvent e) -> {
			togglePreview.setEnabled(false);

			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					ViewerStore.Databases.local.storeObject("detail.preview", preview = !preview);
					return null;

				}

				protected void done() {
					if (preview) {
						togglePreview.setIcon(UIUtil.getIcon("icons16/general/monitor_del.png"));

						// add preview module
						Preview p = new Preview();
						p.setTarget(target);
						p.setShowing(true);
						modules.add(p);
						listPanel.addPanel(p);
					} else {
						togglePreview.setIcon(UIUtil.getIcon("icons16/general/monitor_add.png"));

						// remove module
						for (DModule dm : modules) {
							if (dm instanceof Preview) {
								listPanel.removePanel((Preview) dm);
								modules.remove(dm);
								dm.setShowing(false);
								break;
							}
						}
					}
					togglePreview.setEnabled(true);
					listPanel.revalidate();
					listPanel.repaint();
				};
			}.execute();
		});
		menu.add(togglePreview);

		JButton toggleMap = new JButton();
		if (map) {
			toggleMap.setIcon(UIUtil.getIcon("icons16/general/map_del.png"));
		} else {
			toggleMap.setIcon(UIUtil.getIcon("icons16/general/map_add.png"));
		}
		toggleMap.setToolTipText("Toggle World Map");
		toggleMap.setMargin(new Insets(1, 1, 1, 1));
		toggleMap.addActionListener((ActionEvent e) -> {
			toggleMap.setEnabled(false);

			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					ViewerStore.Databases.local.storeObject("detail.map", map = !map);
					return null;

				}

				protected void done() {
					if (processor) {
						toggleMap.setIcon(UIUtil.getIcon("icons16/general/map_del.png"));

						// add map module
						WorldMap p = new WorldMap();
						p.setTarget(target);
						p.setShowing(true);
						modules.add(p);
						listPanel.addPanel(p);
					} else {
						toggleMap.setIcon(UIUtil.getIcon("icons16/general/map_add.png"));

						// remove module
						for (DModule dm : modules) {
							if (dm instanceof WorldMap) {
								listPanel.removePanel((WorldMap) dm);
								modules.remove(dm);
								dm.setShowing(false);
								break;
							}
						}
					}
					toggleMap.setEnabled(true);
					listPanel.revalidate();
					listPanel.repaint();
				};
			}.execute();
		});
		menu.add(toggleMap);

		JButton toggleStats = new JButton();
		toggleStats.setIcon(UIUtil.getIcon("icons16/general/statistics.png"));
		toggleStats.setToolTipText("Toggle Statistics");
		toggleStats.setMargin(new Insets(1, 1, 1, 1));
		toggleStats.addActionListener((ActionEvent e) -> {

		});
		menu.add(toggleStats);
	}

	private JScrollPane jsp;

	public void nowOpen(ClientProfile sp) {
		target = sp;
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
		System.out.println("Scrollbar width: " + jsp.getVerticalScrollBar().getWidth());
		return max + jsp.getVerticalScrollBar().getWidth();
	}

}
