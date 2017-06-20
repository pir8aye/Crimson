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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.util.SerialUtil;

public class HiddenMenu extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel btn;
	private StatsPanel stats;

	public int maxHeight = 150;

	public HiddenMenu(boolean stats, JButton... hBtns) {

		init();
		if (hBtns.length > 0) {
			initBtns(hBtns);
		}
		if (stats) {
			initStats();
		}
	}

	public HiddenMenu() {
		this(true);
	}

	private void initStats() {
		stats = new StatsPanel();
		stats.setPreferredSize(new Dimension(200, 25));
		add(stats, BorderLayout.SOUTH);
	}

	private void initBtns(JButton[] btns) {
		JPanel buttons = new JPanel();
		buttons.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "More Actions",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 10), null));
		buttons.setLayout(new BorderLayout(0, 0));

		btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 2));
		buttons.add(btn, BorderLayout.CENTER);
		add(buttons);

		for (JButton jb : btns) {
			btn.add(jb);
		}
	}

	private void init() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(new BorderLayout());

	}

	public void nowShowing() {
		stats.start();
	}

	public void nowClosed() {
		stats.stop();
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
			clone = (HiddenMenu) SerialUtil.deserialize(SerialUtil.serialize(this));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int h = 0;
		JWindow test = new JWindow();
		test.getContentPane().add(clone);
		test.setVisible(true);
		h += clone.stats.getHeight();

		test.removeAll();
		test = null;

		return (h < maxHeight) ? h : maxHeight;
	}

}
