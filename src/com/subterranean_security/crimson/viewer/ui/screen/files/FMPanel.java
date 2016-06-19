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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import com.subterranean_security.crimson.viewer.ui.common.components.Console;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.files.FMFrame.Type;

public class FMPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public Console console = new Console();

	public EPanel ep;

	public FMPanel(Type type, EPanel ep) {
		this.ep = ep;
		setLayout(new BorderLayout(0, 0));

		JToolBar jtb = new JToolBar();
		add(jtb, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);

		splitPane.setLeftComponent(new Pane(this));
		splitPane.setRightComponent(new Pane(this));

		JSplitPane console_splitpane = new JSplitPane();
		console_splitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(console_splitpane, BorderLayout.CENTER);
		console_splitpane.setTopComponent(splitPane);
		console_splitpane.setBottomComponent(console);
		console_splitpane.setDividerLocation(0.85d);
		console_splitpane.setResizeWeight(0.85d);

	}
}
