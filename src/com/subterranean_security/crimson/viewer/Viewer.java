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
package com.subterranean_security.crimson.viewer;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import javax.swing.UIManager;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.ui.debug.DebugFrame;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.FileLocking;
import com.subterranean_security.crimson.core.util.PlatformInfo;
import com.subterranean_security.crimson.viewer.ui.panel.MovingPanel;
import com.subterranean_security.crimson.viewer.ui.screen.eula.EULADialog;
import com.subterranean_security.crimson.viewer.ui.screen.login.LoginDialog;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.tweenengine.Tween;

public class Viewer {
	private static final Logger log = CUtil.Logging.getLogger(Viewer.class);

	/**
	 * True when a server instance is detected that was not started by the
	 * viewer
	 */
	public static boolean slsr = FileLocking.lockExists(Common.Instance.SERVER);

	public static void main(String[] argv) {

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		try {
			switch (PlatformInfo.os) {

			case LINUX:
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				break;
			default:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				break;
			}

		} catch (Exception e) {
			log.error("Failed to set look and feel");
			e.printStackTrace();
		}

		// Show the EULA if needed
		try {
			if (ViewerStore.Databases.local.getBoolean("show_eula")) {
				EULADialog eula = new EULADialog(true);
				eula.setLocationRelativeTo(null);
				eula.setVisible(true);

				synchronized (eula) {
					try {
						eula.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
				if (eula.accepted) {
					ViewerStore.Databases.local.storeObject("show_eula", false);
				}
				eula = null;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (ViewerStore.LocalServer.bundledServer.exists() && !slsr) {

			ViewerStore.LocalServer.startLocalServer();

		}

		// initialize sliding layout
		Tween.registerAccessor(MovingPanel.class, new MovingPanel.Accessor());
		SLAnimator.start();

		// show login dialog
		LoginDialog login = new LoginDialog(ViewerStore.LocalServer.bundledServer.exists() && !slsr);

		login.setVisible(true);
		try {
			synchronized (login) {
				login.wait();
			}

		} catch (InterruptedException e) {
			return;
		}
		login = null;

		// Start the main interface
		MainFrame.main = new MainFrame();
		MainFrame.main.setVisible(true);
		MainFrame.main.setLocationRelativeTo(null);

		if (Common.isDebugMode()) {
			DebugFrame df = new DebugFrame();
			df.setVisible(true);

		}

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (LoginDialog.initial.hasLastLogin()) {
					MainFrame.main.np
							.addNote("info:Last Login: " + new Date(LoginDialog.initial.getLastLogin()).toString());
				}

			}
		}).start();

	}

	public static void loadJar(String path) throws Exception {
		File target = new File(path);
		System.out.println("Loading: " + target.getAbsolutePath());
		if (!target.exists()) {
			throw new Exception();
		}

		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { target.toURI().toURL() });
	}

}
