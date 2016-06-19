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
package com.subterranean_security.crimson.viewer.ui.common.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console extends JPanel {

	private static final long serialVersionUID = 1L;

	private StyledDocument doc = null;
	private JTextPane txtpndateLoadedConsole = new JTextPane();
	private JScrollPane jsp = new JScrollPane(txtpndateLoadedConsole);

	private static final Color blue = new Color(0, 204, 204);
	private Style blueStyle = null;
	private static final Color green = new Color(0, 215, 123);
	private Style greenStyle = null;
	private static final Color orange = new Color(255, 191, 0);
	private Style orangeStyle = null;

	private SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

	public Console() {
		setLayout(new BorderLayout(0, 0));

		doc = txtpndateLoadedConsole.getStyledDocument();
		txtpndateLoadedConsole.setEditable(false);
		txtpndateLoadedConsole.setFont(new Font("Monospaced", Font.PLAIN, 11));
		txtpndateLoadedConsole.setForeground(Color.WHITE);
		txtpndateLoadedConsole.setBackground(Color.DARK_GRAY);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp, BorderLayout.CENTER);

		blueStyle = txtpndateLoadedConsole.addStyle("blueConsoleStyle", null);
		StyleConstants.setForeground(blueStyle, blue);

		greenStyle = txtpndateLoadedConsole.addStyle("greenConsoleStyle", null);
		StyleConstants.setForeground(greenStyle, green);

		orangeStyle = txtpndateLoadedConsole.addStyle("orangeConsoleStyle", null);
		StyleConstants.setForeground(orangeStyle, orange);
	}

	public synchronized void addLine(String s) {
		addLine(s, LineType.BLUE);
	}

	public synchronized void addLine(String s, LineType lt) {

		Style style = null;
		switch (lt) {
		case ORANGE:
			style = orangeStyle;
			break;
		case BLUE:
			style = blueStyle;
			break;
		case GREEN:
			style = greenStyle;
			break;
		default:
			style = null;

		}

		try {
			doc.insertString(doc.getLength(), "[" + formatter.format(new Date()) + "] " + s + "\n", style);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scroll();
	}

	public void scroll() {
		JScrollBar vertical = jsp.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	public enum LineType {
		ORANGE, BLUE, GREEN;

	}

}
