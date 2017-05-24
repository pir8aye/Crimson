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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.RandomUtil;

/**
 * User interface utilities
 */
public final class UIUtil {

	private static final Logger log = LoggerFactory.getLogger(UIUtil.class);

	private UIUtil() {
	}

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

	private static Map<String, ImageIcon> cache = new HashMap<String, ImageIcon>();

	public static ImageIcon getIcon(String rpath) {
		if (!cache.containsKey(rpath)) {
			ImageIcon icon = getIcon(
					UIUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/" + rpath));
			if (icon != null) {
				cache.put(rpath, icon);
			}
		}
		return cache.get(rpath);
	}

	private static ImageIcon getIcon(URL rpath) {
		try {
			return new ImageIcon(ImageIO.read(rpath));
		} catch (Exception e) {
			return null;
		}
	}

	public static ImageIcon getIconOrFallback(String preferred, String fallback) {
		ImageIcon icon = getIcon(preferred);
		if (icon == null) {
			icon = getIcon(fallback);
		}
		if (icon == null) {
			icon = new ImageIcon();
		}
		return icon;
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
		case OSX:
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				log.warn("Failed to set system LookAndFeel");
			}
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Crimson");
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

	public static String getPassword(JPasswordField field) {
		char[] password = field.getPassword();
		String hash = null;
		try {
			hash = CryptoUtil.hash("SHA-256", password);
		} catch (NoSuchAlgorithmException e) {

		}
		RandomUtil.clearChar(password);
		return hash;
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static void disableMouse(Component... components) {
		for (Component c : components) {
			if (c instanceof Container) {
				disableMouse(((Container) c).getComponents());
			}
			for (MouseListener l : c.getMouseListeners()) {
				c.removeMouseListener(l);
			}
			for (MouseMotionListener l : c.getMouseMotionListeners()) {
				c.removeMouseMotionListener(l);
			}
			for (MouseWheelListener l : c.getMouseWheelListeners()) {
				c.removeMouseWheelListener(l);
			}
		}
	}

}
