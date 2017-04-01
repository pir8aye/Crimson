/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.common.panels.lpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class LPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Component)
				compList.addAll(getAllComponents((Container) comp));
		}
		return compList;
	}

	private JPanel tail;
	private Color bg;

	public LPanel() {
		super(new BorderLayout());
		this.bg = getBackground();
		init();
	}

	public LPanel(Color bg) {
		super(new BorderLayout());
		this.bg = bg;
		init();
	}

	private void init() {
		tail = new JPanel(new BorderLayout());
		tail.setBackground(bg);
		JScrollPane jsp = new JScrollPane(tail);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		add(jsp, BorderLayout.CENTER);
	}

	public void addPanel(JPanel panel) {
		tail.add(panel, BorderLayout.NORTH);
		JPanel jp = new JPanel(new BorderLayout());
		jp.setBackground(bg);
		tail.add(jp, BorderLayout.CENTER);
		tail = jp;
	}

	public void removePanel(JPanel panel) {
		for (Component c : getAllComponents(this)) {
			if (c instanceof JPanel) {
				((JPanel) c).remove(panel);
			}
		}
	}

	public void clear() {
		this.removeAll();
		tail = this;
	}

}
