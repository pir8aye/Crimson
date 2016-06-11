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
package com.subterranean_security.crimson.viewer.ui.common.panels.hpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.exception.InvalidObjectException;
import com.subterranean_security.crimson.core.util.ObjectTransfer;
import com.subterranean_security.crimson.viewer.ui.UICommon;

public class HiddenMenu extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel buttons;
	private JTextArea textArea;
	private JPanel btn;
	private JPanel stats;
	private JPanel help;

	private boolean help_added = false;
	private boolean stats_added = false;
	private boolean options_added = false;

	public int maxHeight = 150;

	public HiddenMenu() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		buttons = new JPanel();
		buttons.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Options", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		buttons.setLayout(new BorderLayout(0, 0));

		btn = new JPanel();
		buttons.add(btn, BorderLayout.CENTER);
		btn.setLayout(new BoxLayout(btn, BoxLayout.X_AXIS));

		stats = new JPanel();
		stats.setBorder(new TitledBorder(null, "Stats", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		stats.setLayout(new BorderLayout(0, 0));

		help = new JPanel();
		help.setMaximumSize(new Dimension(32767, 200));
		help.setBorder(new TitledBorder(null, "Help", TitledBorder.LEADING, TitledBorder.TOP,
				UICommon.font_title_hmenu_help, null));

		help.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setOpaque(false);
		textArea.setMinimumSize(new Dimension(200, 40));
		textArea.setFont(UICommon.font_text_help);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JScrollPane jsp = new JScrollPane(textArea);
		jsp.setMaximumSize(new Dimension(200, 200));
		jsp.setMinimumSize(new Dimension(200, 40));
		help.add(jsp);

		Component horizontalStrut = Box.createHorizontalStrut(10);
		horizontalStrut.setMaximumSize(new Dimension(25, 5));
		help.add(horizontalStrut, BorderLayout.WEST);
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		horizontalStrut_1.setMaximumSize(new Dimension(25, 5));
		help.add(horizontalStrut_1, BorderLayout.EAST);
	}

	public void setDesc(String d) {
		if (!help_added) {
			add(help);
			help_added = true;
		}
		textArea.setText(d);
	}

	public void addButton(JButton jb) {
		if (!options_added) {
			add(buttons);
			options_added = true;
		}
		jb.setMargin(new Insets(0, 2, 0, 2));
		btn.add(Box.createHorizontalStrut(5));
		btn.add(jb);

	}

	public void addWebsiteButton() {
		JButton jb = new JButton("Visit Website");
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// vist website
			}
		});

		btn.add(jb);
	}

	/**
	 * This method is absurd. Since its impossible to get the size of an unshown
	 * JPanel, this method clones HiddenMenu using serialization and adds it to
	 * an invisible JWindow. The dimensions are gathered and the JWindow is
	 * thrown away.
	 * 
	 * @return
	 */
	public int getHHeight() {
		HiddenMenu clone = null;
		try {
			clone = (HiddenMenu) ObjectTransfer.Default.deserialize(ObjectTransfer.Default.serialize(this));
		} catch (InvalidObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int h = 0;
		JWindow test = new JWindow();
		test.getContentPane().add(clone);
		test.setVisible(true);
		h += clone.stats.getHeight();
		h += clone.buttons.getHeight();
		h += clone.help.getHeight();

		test.removeAll();
		test = null;

		return (h < maxHeight) ? h : maxHeight;
	}

}
