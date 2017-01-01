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
package com.subterranean_security.crimson.core.ui;

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
	public static final Color bad = new Color(200, 0, 0);
	public static final Color info = new Color(20, 23, 139);

	private static final Font font = new Font("Dialog", Font.BOLD, 9);

	private boolean frozen = false;

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

	public void setGood(final String s) {
		if (frozen) {
			return;
		}

		setText(" ");
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(50);
				return null;
			}

			protected void done() {
				setText(s.toUpperCase());
				setForeground(good);
				setBorder(new LineBorder(good, 1, true));
			};
		}.execute();

	}

	public void setWarn(final String s) {
		if (frozen) {
			return;
		}

		setText(" ");
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(50);
				return null;
			}

			protected void done() {
				setText(s.toUpperCase());
				setForeground(warn);
				setBorder(new LineBorder(warn, 1, true));
			};
		}.execute();

	}

	public void setBad(final String s) {
		if (frozen) {
			return;
		}

		setText(" ");
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(50);
				return null;
			}

			protected void done() {
				setText(s.toUpperCase());
				setForeground(bad);
				setBorder(new LineBorder(bad, 1, true));
			};
		}.execute();

	}

	public void setInfo(final String s) {
		if (frozen) {
			return;
		}

		setText(" ");
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(50);
				return null;
			}

			protected void done() {
				setText(s.toUpperCase());
				setForeground(info);
				setBorder(new LineBorder(info, 1, true));
			};
		}.execute();

	}

	public void freeze() {
		frozen = true;
	}

	public void unfreeze() {
		frozen = false;
	}
}
