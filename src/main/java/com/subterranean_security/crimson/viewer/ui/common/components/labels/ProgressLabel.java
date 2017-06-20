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
package com.subterranean_security.crimson.viewer.ui.common.components.labels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.subterranean_security.crimson.viewer.ui.common.components.ProgressBarFactory;

public class ProgressLabel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private JLabel label;

	public ProgressLabel() {
		init();
	}

	public ProgressLabel(String s) {
		init();
		setText(s);
	}

	public void init() {

		setLayout(new BorderLayout(0, 0));

		label = new JLabel();
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		add(label, BorderLayout.WEST);

		progressBar = ProgressBarFactory.get();
		progressBar.setPreferredSize(new Dimension(148, 4));
		progressBar.setVisible(false);
		add(progressBar, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);

	}

	public void setText(String text) {
		label.setText(text);
	}

	public void startLoading() {
		label.setText("loading...");
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void stopLoading() {
		progressBar.setVisible(false);
		progressBar.setIndeterminate(false);
	}

	public void setLabelForeground(Color fg) {
		label.setForeground(fg);
	}

}
