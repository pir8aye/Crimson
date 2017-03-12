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

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.universal.stores.PrefStore.PTag;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.screen.main.graph.HostGraph;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public boolean listLoaded = false;
	public boolean graphLoaded = false;

	public HostList list;
	public HostGraph graph;
	public Console console = new Console();

	public MainPanel() {

		setLayout(new CardLayout());

		String last = PrefStore.getPref().getString(PrefStore.PTag.VIEW_MAIN_LAST);
		if (last.equals("list")) {
			loadList();
		} else {
			loadGraph();
		}

		console.addLine("Welcome to Crimson (build " + Common.build + ")");

	}

	public void switchToList() {
		if (!listLoaded) {
			loadList();
		}
		((CardLayout) getLayout()).show(this, "LIST");
		PrefStore.getPref().putString(PrefStore.PTag.VIEW_MAIN_LAST, "list");
	}

	public void switchToGraph() {
		if (!graphLoaded) {
			loadGraph();
		}
		((CardLayout) getLayout()).show(this, "GRAPH");
		PrefStore.getPref().putString(PrefStore.PTag.VIEW_MAIN_LAST, "graph");
	}

	public void loadList() {
		list = new HostList();
		add(list, "LIST");
		listLoaded = true;
	}

	public void loadGraph() {
		graph = new HostGraph();
		add(graph, "GRAPH");
		graphLoaded = true;
	}

	public void openConsole() {
		MainFrame.main.ep.raise(console, 2f, true);
	}

	public void closeConsole() {
		MainFrame.main.ep.drop();
	}

}
