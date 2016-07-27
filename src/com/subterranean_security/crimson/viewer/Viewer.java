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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.EH;
import com.subterranean_security.crimson.core.util.FileLocking;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.viewer.ViewerStore.Databases;
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

		CUtil.Logging.configure();

		// Establish the custom fallback exception handler
		Thread.setDefaultUncaughtExceptionHandler(new EH());

		// Establish the custom shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		// Try to get a lock or exit
		if (!FileLocking.lock(Instance.VIEWER)) {
			System.exit(0);
		}

		// Load native libraries
		Native.Loader.load();

		loadDatabaseValues();

		// Make platform specific UI tweaks
		UIUtil.adaptPlatform();

		// Show the EULA if needed
		try {
			if (ViewerStore.Databases.local.getBoolean("show_eula") && !Common.isDebugMode()) {
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

		boolean localServerFound = ViewerState.findLocalServerInstance();
		if (ViewerStore.LocalServer.bundledServer.exists() && !localServerFound) {

			ViewerStore.LocalServer.startLocalServer();

		}

		// initialize sliding layout
		Tween.registerAccessor(MovingPanel.class, new MovingPanel.Accessor());
		SLAnimator.start();

		loadState();

		// Preload main interface
		MainFrame.main = new MainFrame();

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

	public static void loadDatabaseValues() {
		try {
			ViewerStore.Profiles.server = (ServerProfile) ViewerStore.Databases.local.getObject("server.profile");
			ViewerStore.Profiles.viewer = (ClientProfile) ViewerStore.Databases.local.getObject("viewer.profile");

		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public static void loadState() {
		try {
			ViewerState.trialMode = Databases.local.getString("serial").equals("TRIAL");
		} catch (Exception e) {
		}
	}

}
