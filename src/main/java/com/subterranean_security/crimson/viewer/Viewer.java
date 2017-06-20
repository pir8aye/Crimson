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

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.storage.BasicStorageFacility;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.debug.CharcoalAppender;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.viewer.store.LocalServerStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.MovablePanel;
import com.subterranean_security.crimson.viewer.ui.screen.eula.EULADialog;
import com.subterranean_security.crimson.viewer.ui.screen.login.LoginDialog;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.tweenengine.Tween;

public final class Viewer {

	/**
	 * Nested class to prevent Logger from getting default configuration
	 */
	private static final class Log {
		public static final Logger log = LoggerFactory.getLogger(Viewer.class);
	}

	public static void main(String[] argv) {
		LogUtil.configure();

		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Error: headless graphics environment detected!");
			// Maybe a ncurses interface will be implemented someday
			return;
		}

		// Redirect standard output for charcoal
		if (DEV_MODE) {
			CharcoalAppender.redirectStdout();
		}

		// Set the fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Set the shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Initialize preference storage
		initializePreferences();

		// Check for other instances
		if (PrefStore.getPref().isLocked()) {
			System.exit(0);
		} else {
			PrefStore.getPref().lock();
		}

		// Load native libraries
		Native.Loader.load();

		// Initialize database
		initializeDatabase();

		// Make platform specific UI tweaks
		UIUtil.adaptPlatform();

		// Show the EULA if needed
		try {
			if (PrefStore.getPref().getBoolean(PrefStore.PTag.GENERAL_EULA_SHOW) && !DEV_MODE) {
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
					PrefStore.getPref().putBoolean(PrefStore.PTag.GENERAL_EULA_SHOW, false);
				}
				eula = null;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean localServerFound = ViewerState.findLocalServerInstance();
		if (LocalServerStore.bundledServer.exists() && !localServerFound) {
			// LocalServerStore.startLocalServer();
		}

		// initialize sliding layout
		Tween.registerAccessor(MovablePanel.class, new MovablePanel.Accessor());
		SLAnimator.start();

		// show login dialog
		LoginDialog login = new LoginDialog(LocalServerStore.bundledServer.exists() && !localServerFound);

		login.setVisible(true);
		try {
			synchronized (login) {
				login.wait();
			}

		} catch (InterruptedException e) {
			return;
		}
		login = null;

		// Show the main interface
		MainFrame.main.setLocationRelativeTo(null);
		MainFrame.main.setVisible(true);
		MainFrame.main.invokeAfterload();
	}

	private static void initializeDatabase() {
		BasicStorageFacility sf = new BasicDatabase(new File(Environment.base + "/var/viewer.db"));
		try {
			sf.initialize();
		} catch (ClassNotFoundException e) {
			Log.log.error("Failed to load SQLite dependancy");
			System.exit(0);
		} catch (IOException e) {
			Log.log.error("Failed to write database");
			System.exit(0);
		} catch (SQLException e) {
			Log.log.error("SQL error: {}", e.getMessage());
			System.exit(0);
		}

		DatabaseStore.setFacility(sf);

	}

	private static void initializePreferences() {
		PrefStore.loadPreferences(Instance.VIEWER);
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
