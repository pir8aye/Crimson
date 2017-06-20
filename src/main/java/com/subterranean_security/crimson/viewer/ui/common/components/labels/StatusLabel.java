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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

public class StatusLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	public static final Color good = new Color(0, 149, 39);
	public static final Color warn = new Color(200, 116, 0);
	public static final Color ongoing = new Color(200, 116, 0);
	public static final Color bad = new Color(200, 0, 0);
	public static final Color info = new Color(20, 23, 139);

	private static final Font font = new Font("Dialog", Font.BOLD, 9);

	private String def = "";

	public StatusLabel(String s) {
		this();
		setDefault(s);
	}

	public StatusLabel() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setFont(font);
	}

	public void setDefault() {
		setInfo(def);
	}

	public void setDefault(String s) {
		def = s;
		setDefault();
	}

	public void setGood(String s) {
		changeText(s, good);

	}

	public void setWarn(String s) {
		changeText(s, warn);
	}

	public void setBad(String s) {
		changeText(s, bad);
	}

	public void setInfo(String s) {
		changeText(s, info);
	}

	public void setOngoing(String s) {
		changeText(s, ongoing);
	}

	private void changeText(String text, Color color) {
		setText(" ");
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(50);
				return null;
			}

			protected void done() {
				setText(text.toUpperCase());
				setForeground(color);
				setBorder(new LineBorder(color, 1, true));
			};
		}.execute();
	}

}
