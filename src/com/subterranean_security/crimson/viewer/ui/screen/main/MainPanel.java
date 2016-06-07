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
import com.subterranean_security.crimson.viewer.ViewerStore;
import com.subterranean_security.crimson.viewer.ui.common.components.Console;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public boolean listLoaded = false;
	public boolean graphLoaded = false;

	public HostList list;
	public HostGraph graph;
	public Console console = new Console();

	public MainPanel() {

		setLayout(new CardLayout());

		try {
			String last = ViewerStore.Databases.local.getString("view.last");
			if (last.equals("list")) {
				loadList();
			} else {
				loadGraph();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		console.addLine("Welcome to Crimson build: " + Common.build);

	}

	public void switchToList() {
		if (!listLoaded) {
			loadList();
		}
		((CardLayout) getLayout()).show(this, "LIST");
		ViewerStore.Databases.local.storeObject("view.last", "list");
	}

	public void switchToGraph() {
		if (!graphLoaded) {
			loadGraph();
		}
		((CardLayout) getLayout()).show(this, "GRAPH");
		ViewerStore.Databases.local.storeObject("view.last", "graph");
	}

	public void loadList() {
		listLoaded = true;
		list = new HostList();
		add(list, "LIST");
	}

	public void loadGraph() {
		graphLoaded = true;
		graph = new HostGraph();
		add(graph, "GRAPH");
	}

	public void openConsole() {
		MainFrame.main.ep.raise(console, 2f);
	}

	public void closeConsole() {
		MainFrame.main.ep.drop();
	}

}
