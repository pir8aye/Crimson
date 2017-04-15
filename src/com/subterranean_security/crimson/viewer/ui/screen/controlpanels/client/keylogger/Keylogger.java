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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger.ep.Settings;

import aurelienribon.slidinglayout.SLSide;

public class Keylogger extends JPanel implements CPPanel, Observer {

	private static final long serialVersionUID = 1L;

	private ClientProfile profile;

	private JPanel selection_panel;
	private JPanel logs_panel;

	public KeyLogPane content;

	private JMenuBar menuBar;

	private JPanel blank;

	private LogTree logTree;

	private EPanel ep;
	private JPanel content_panel;
	private JPanel loading;

	private JButton btnSettingsEP;
	private JButton btnStatsEP;
	private JButton btnView;

	private boolean flatView;

	public Keylogger(ClientProfile profile, Console console) {
		this.profile = profile;
		init(console);
		try {
			flatView = PrefStore.getPref().getBoolean(PrefStore.PTag.VIEW_KEYLOG_FLAT);
		} catch (Exception e) {
			EH.handle(e);
		}
	}

	public void init(Console console) {

		setLayout(new BorderLayout(0, 0));

		logs_panel = new JPanel();
		logs_panel.setLayout(new BorderLayout(0, 0));
		add(logs_panel);

		selection_panel = new JPanel();
		selection_panel.setBorder(null);
		logs_panel.add(selection_panel, BorderLayout.WEST);
		selection_panel.setLayout(new BorderLayout(0, 0));

		logTree = new LogTree(this, profile);
		selection_panel.add(logTree);

		content_panel = new JPanel();
		ep = new EPanel(content_panel, SLSide.TOP);
		logs_panel.add(ep);
		content_panel.setLayout(new CardLayout(0, 0));

		blank = new JPanel();
		content_panel.add(blank, "BLANK");

		content = new KeyLogPane();
		content_panel.add(content, "KEYLOG");

		loading = new JPanel();
		content_panel.add(loading, "LOADING");

		menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		btnView = new JButton(UIUtil.getIcon("icons16/general/tree_hierarchy.png"));
		btnView.setFocusable(false);
		btnView.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				logTree.refreshing = true;
				flatView = !flatView;
				PrefStore.getPref().putBoolean(PrefStore.PTag.VIEW_KEYLOG_FLAT, flatView);
				if (flatView) {
					btnView.setIcon(UIUtil.getIcon("icons16/general/tree_list.png"));
					btnView.setToolTipText("Switch to flat view");
				} else {
					btnView.setIcon(UIUtil.getIcon("icons16/general/tree_hierarchy.png"));
					btnView.setToolTipText("Switch to hierarchical view");
				}

				logTree.resetTree();
				logTree.setFormatters();
				logTree.refreshTree();

			}
		});
		btnView.setToolTipText(flatView ? "Switch to hierarchical view" : "Switch to flat view");
		btnView.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnView);

		menuBar.add(Box.createHorizontalGlue());

		btnStatsEP = new JButton(UIUtil.getIcon("icons16/general/statistics.png"));
		btnStatsEP.setToolTipText("Keylogger Statistics");
		btnStatsEP.setFocusable(false);
		btnStatsEP.setMargin(new Insets(2, 4, 2, 4));
		menuBar.add(btnStatsEP);

		btnSettingsEP = new JButton(UIUtil.getIcon("icons16/general/cog.png"));
		btnSettingsEP.setToolTipText("Keylogger Settings");
		btnSettingsEP.setFocusable(false);
		btnSettingsEP.setMargin(new Insets(2, 4, 2, 4));
		btnSettingsEP.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ep.isOpen() && ep.getEP() != null && ep.getEP() instanceof Settings) {
					ep.drop();
					((Settings) ep.getEP()).save();
				} else {
					ep.raise(new Settings(profile.getCvid(), profile.getKeyloggerState(), profile.getKeyloggerTrigger(),
							profile.getKeyloggerTriggerValue()), 140);
				}

			}
		});
		menuBar.add(btnSettingsEP);

	}

	public void showKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "KEYLOG");
	}

	public void loadKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "LOADING");
	}

	public void hideKeylog() {
		((CardLayout) content_panel.getLayout()).show(content_panel, "BLANK");
	}

	@Override
	public void clientOffline() {
		btnSettingsEP.setEnabled(false);

	}

	@Override
	public void serverOffline() {
		clientOffline();

	}

	@Override
	public void clientOnline() {
		btnSettingsEP.setEnabled(true);

	}

	@Override
	public void serverOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabOpened() {
		profile.getKeylog().addObserver(this);
	}

	@Override
	public void tabClosed() {
		profile.getKeylog().deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof EV_KEvent) {
			logTree.refreshTree();
			content.updateContent((EV_KEvent) arg);
		}

	}

}
