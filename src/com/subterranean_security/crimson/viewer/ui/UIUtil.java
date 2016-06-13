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
package com.subterranean_security.crimson.viewer.ui;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Platform;

public enum UIUtil {
	;

	private static final Logger log = LoggerFactory.getLogger(UIUtil.class);

	public static ArrayList<Image> getIconList() {
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(
				UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-128.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-64.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-32.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-16.png")));
		return icons;
	}

	public static ImageIcon getIcon(String rpath) {
		try {
			return new ImageIcon(ImageIO
					.read(UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/" + rpath)));
		} catch (Exception e) {
			return null;
		}
	}

	public static void adaptPlatform() {
		switch (Platform.osFamily) {

		case LIN:
			UIManager.put("TitledBorder.font", new Font("Dialog", Font.BOLD, 12));
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				log.warn("Failed to set GTK LookAndFeel");
			}
			break;

		default:
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				log.warn("Failed to set system LookAndFeel");
			}
			break;

		}
	}

}
