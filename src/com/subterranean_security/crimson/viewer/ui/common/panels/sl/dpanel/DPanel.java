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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.dpanel;

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
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.lpanel.LPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.SlidingPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.dpanel.DModule;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.main.dmodules.NetInterfaces;
import com.subterranean_security.crimson.viewer.ui.screen.main.dmodules.Preview;
import com.subterranean_security.crimson.viewer.ui.screen.main.dmodules.Processor;
import com.subterranean_security.crimson.viewer.ui.screen.main.dmodules.WorldMap;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLSide;

/**
 * A Detail Panel (DPanel) shows a panel containing host information on the
 * right side of the screen.
 */
public class DPanel extends SlidingPanel {

	private static final long serialVersionUID = 1L;

	private SLConfig pos1;
	private SLConfig pos2;

	private MovablePanel movingBar;
	private MovablePanel movingMain;

	public Detail detail = new Detail(this);

	public DPanel(JPanel main) {
		this(main, 0.9f);
	}

	public DPanel(JPanel main, float transitionTime) {
		this.transitionTime = transitionTime;

		movingBar = new MovablePanel(detail);
		movingMain = new MovablePanel(main);
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
			if (!open) {
				// refresh width
				// refreshWidth();

				// move the detail panel out
				movingMain.runAction();
				open = true;
			}

			detail.nowOpen(sp);

		}

	}

	public void closeDetail() {
		if (open && !moving) {
			// move the detail panel back
			movingMain.runAction();
			detail.nowClosed();
			open = false;

		}

	}

	private final Runnable actionUP = new Runnable() {
		@Override
		public void run() {
			moving = true;
			DPanel.this.createTransition().push(new SLKeyframe(pos2, transitionTime)
					.setStartSide(SLSide.RIGHT, movingBar).setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							moving = false;
							movingMain.setAction(actionDN);
						}
					})).play();
		}
	};

	private final Runnable actionDN = new Runnable() {
		@Override
		public void run() {
			moving = true;
			DPanel.this.createTransition().push(new SLKeyframe(pos1, transitionTime).setEndSide(SLSide.RIGHT, movingBar)
					.setCallback(new SLKeyframe.Callback() {
						@Override
						public void done() {
							moving = false;
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
	private boolean nic = false;
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
		if (nic) {
			NetInterfaces p = new NetInterfaces();
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

		processor = PrefStore.getPref().getBoolean(PrefStore.PTag.VIEW_DETAIL_PROCESSOR);
		nic = PrefStore.getPref().getBoolean(PrefStore.PTag.VIEW_DETAIL_NIC);
		preview = PrefStore.getPref().getBoolean(PrefStore.PTag.VIEW_DETAIL_PREVIEW);
		map = PrefStore.getPref().getBoolean(PrefStore.PTag.VIEW_DETAIL_MAP);

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
					for (ClientCPFrame frame : UIStore.clientControlPanels) {
						if (frame.profile.getCvid() == target.getCvid()) {
							// there is already an open control panel
							frame.setLocationRelativeTo(null);
							frame.toFront();
							return;
						}
					}
					ClientCPFrame ccpf = new ClientCPFrame(target);
					UIStore.clientControlPanels.add(ccpf);
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
					PrefStore.getPref().putBoolean(PrefStore.PTag.VIEW_DETAIL_PROCESSOR, processor = !processor);
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

		//
		JButton toggleNic = new JButton();
		if (nic) {
			toggleNic.setIcon(UIUtil.getIcon("icons16/general/nic_del.png"));
		} else {
			toggleNic.setIcon(UIUtil.getIcon("icons16/general/nic_add.png"));
		}
		toggleNic.setToolTipText("Toggle Network Adapter");
		toggleNic.setMargin(new Insets(1, 1, 1, 1));
		toggleNic.addActionListener((ActionEvent e) -> {

			toggleNic.setEnabled(false);

			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					PrefStore.getPref().putBoolean(PrefStore.PTag.VIEW_DETAIL_NIC, nic = !nic);
					return null;
				}

				protected void done() {
					if (nic) {
						toggleNic.setIcon(UIUtil.getIcon("icons16/general/nic_del.png"));

						// add processor module
						NetInterfaces p = new NetInterfaces();
						p.setTarget(target);
						p.setShowing(true);
						modules.add(p);
						listPanel.addPanel(p);
					} else {
						toggleNic.setIcon(UIUtil.getIcon("icons16/general/nic_add.png"));

						// remove module
						for (DModule dm : modules) {
							if (dm instanceof NetInterfaces) {
								listPanel.removePanel((NetInterfaces) dm);
								modules.remove(dm);
								dm.setShowing(false);
								break;
							}
						}
					}
					toggleNic.setEnabled(true);
					listPanel.revalidate();
					listPanel.repaint();
				};
			}.execute();

		});
		menu.add(toggleNic);
		//

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
					PrefStore.getPref().putBoolean(PrefStore.PTag.VIEW_DETAIL_PREVIEW, preview = !preview);
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
					PrefStore.getPref().putBoolean(PrefStore.PTag.VIEW_DETAIL_MAP, map = !map);
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
