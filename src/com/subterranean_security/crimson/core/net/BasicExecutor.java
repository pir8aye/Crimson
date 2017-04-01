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
package com.subterranean_security.crimson.core.net;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.subterranean_security.crimson.universal.Universal;

public abstract class BasicExecutor {

	protected Thread dispatchThread;
	protected ExecutorService pool;

	protected Connector connector;

	public BasicExecutor() {
		pool = Executors.newCachedThreadPool();
	}

	public void stop() {
		if (dispatchThread != null) {
			dispatchThread.interrupt();
			dispatchThread = null;
		}

		try {
			pool.shutdown();
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		} finally {
			pool.shutdownNow();
			pool = null;
		}

	}

	public void start() {
		dispatchThread.start();
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public static BasicExecutor getInstanceExecutor() {
		// sorry about the reflection :(
		try {
			switch (Universal.instance) {
			case SERVER:
				return (BasicExecutor) Class.forName("com.subterranean_security.crimson.server.net.ServerExecutor")
						.getConstructor().newInstance(new Object[] {});
			case VIRIDIAN:
				return (BasicExecutor) Class.forName("com.subterranean_security.viridian.net.ViridianExecutor")
						.getConstructor().newInstance(new Object[] {});
			default:
				break;

			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
