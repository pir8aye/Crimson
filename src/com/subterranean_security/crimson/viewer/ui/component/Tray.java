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
package com.subterranean_security.crimson.viewer.ui.component;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class Tray {

	private static TrayIcon trayObject = new TrayIcon(
			(new ImageIcon(Tray.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-16.png"),
					"Crimson")).getImage());

	static {
		trayObject.setImageAutoSize(true);
		final PopupMenu popup = new PopupMenu();

		// Create a pop-up menu components
		MenuItem restoreItem = new MenuItem("Restore Crimson");
		restoreItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// restore Crimson
				MainFrame.main.setVisible(true);
				removeTray();

			}
		});

		MenuItem exitItem = new MenuItem("Exit Crimson");
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);

			}
		});

		// Add components to pop-up menu

		popup.add(restoreItem);
		popup.add(exitItem);

		trayObject.setPopupMenu(popup);
	}

	public static void addTray() {

		try {
			SystemTray.getSystemTray().add(trayObject);
			MainFrame.main.setVisible(false);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void removeTray() {
		SystemTray.getSystemTray().remove(trayObject);
	}

}
