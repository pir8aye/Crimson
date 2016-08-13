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
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.sv.keylogger.Page;
import com.subterranean_security.crimson.sv.keylogger.Paragraph;

public class KeyLogPane extends JPanel {

	private static final long serialVersionUID = 1L;

	// TODO rename to something more descriptive
	private final long paragraphTimeInterval = 60000;

	private boolean highlightPhone = true;
	private boolean highlightEmail = true;
	private boolean highlightURL = false;
	private boolean highlightFileURL = false;

	private ArrayList<KeyLogParagraph> paragraphs = new ArrayList<KeyLogParagraph>();
	private JPanel stack;

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
		setLayout(new BorderLayout(0, 0));

		stack = new JPanel();
		stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(stack);

	}

	private void addParagraph(KeyLogParagraph k) {
		paragraphs.add(k);
		stack.add(k);

	}

	public void loadData(Page page) {
		clear();
		for (Paragraph p : page.getParagraphs()) {
			addParagraph(new KeyLogParagraph(p));
		}
	}

	public void clear() {
		paragraphs.clear();
		stack.removeAll();
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

	public void updateContent(EV_KEvent k) {
		Date kdate = new Date(k.getDate());

		if (paragraphs.size() > 0) {
			KeyLogParagraph klp = paragraphs.get(paragraphs.size() - 1);
			if (klp.getParagraph().getTitle().equals(k.getTitle())) {
				if (kdate.getTime() - klp.getParagraph().getDate().getTime() < Page.sameWindowSeparationInterval) {
					klp.append(k.getEvent());
					return;
				}

			}

		}

		// new paragraph
		addParagraph(new KeyLogParagraph(new Paragraph(k.getTitle(), kdate, k.getEvent())));

	}

}
