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

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.EH;
import com.subterranean_security.crimson.core.misc.FileLocking;
import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.storage.StorageFacility;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.Database;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.MovingPanel;
import com.subterranean_security.crimson.viewer.ui.screen.eula.EULADialog;
import com.subterranean_security.crimson.viewer.ui.screen.login.LoginDialog;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.tweenengine.Tween;

public class Viewer {
	private static final Logger log = LoggerFactory.getLogger(Viewer.class);

	public static void main(String[] argv) {

		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Error: headless graphics environment detected!");
			return;
		}

		LogUtil.configure();

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Try to get a lock or exit
		if (!FileLocking.lock(Universal.Instance.VIEWER)) {
			System.exit(0);
		}

		// Load native libraries
		Native.Loader.load();

		// Initialize database
		initializeDatabase();

		// Make platform specific UI tweaks
		UIUtil.adaptPlatform();

		// Show the EULA if needed
		try {
			if (Database.getFacility().getBoolean("show_eula") && !Common.isDebugMode()) {
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
					Database.getFacility().store("show_eula", false);
				}
				eula = null;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean localServerFound = ViewerState.findLocalServerInstance();
		if (ViewerStore.LocalServer.bundledServer.exists() && !localServerFound) {

			ViewerStore.LocalServer.startLocalServer();

		}

		// initialize sliding layout
		Tween.registerAccessor(MovingPanel.class, new MovingPanel.Accessor());
		SLAnimator.start();

		loadState();

		// show login dialog
		LoginDialog login = new LoginDialog(ViewerStore.LocalServer.bundledServer.exists() && !localServerFound);

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
		StorageFacility sf = new BasicDatabase(Viewer.class.getName(),
				new File(Common.Directories.base + "/var/viewer.db"));
		try {
			sf.initialize();
		} catch (ClassNotFoundException e) {
			log.error("Failed to load SQLite dependancy");
			System.exit(0);
		} catch (IOException e) {
			log.error("Failed to write database");
			System.exit(0);
		} catch (SQLException e) {
			log.error("SQL error: {}", e.getMessage());
			System.exit(0);
		}

		Database.setFacility(sf);
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

	public static void loadState() {
		try {
			ViewerState.trialMode = Database.getFacility().getString("serial").isEmpty();
		} catch (Exception e) {
			ViewerState.trialMode = true;
		}
	}

}
